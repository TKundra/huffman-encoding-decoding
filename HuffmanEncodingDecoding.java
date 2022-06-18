package HuffmanProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

/*
 * file.txt - contains data (but data shouldn't contain any space in sentences - not optimized for sentences with space)
 * pass-keys.txt - contains hash table of all characters
*/

// tree node
class Node {
    Node left = null, right = null;
    Character character;
    Integer frequency;

    Node(Character character, Integer frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    Node(Character character, Integer frequency, Node left, Node right) {
        this.character = character;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
}

public class HuffmanEncodingDecoding {

    private StringBuilder encodedString = new StringBuilder();
    private StringBuilder decodedString = new StringBuilder();
    private StringBuilder data = new StringBuilder();
    private Map<Character, Integer> frequency = new HashMap<>();
    private Map<Character, String> huffmanCode = new HashMap<>();

    // point to root of huffman tree
    Node root = null;

    // util function to check is leaf node
    private boolean isLeaf(Node root) {
        return root.left == null && root.right == null;
    }

    // traverse huffman tree and store huffman code in map
    private void encode(Node root, String expression, Map<Character, String> huffmanCode) {
        if (root == null)
            return;
        if (isLeaf(root))
            huffmanCode.put(root.character, expression.length() > 0 ? expression : "1");
        encode(root.left, expression + "0", huffmanCode);
        encode(root.right, expression + "1", huffmanCode);
    }

    // traverse huffman tree and decode the encoded string
    public void decode(Node root, StringBuilder encodedString) {
        if (isLeaf(root)) {
            // special case inputs like "a". "aa", "aaa", ...
            while (root.frequency-- > 0) {
                decodedString.append(root.character);
            }
        } else {
            decode(root, encodedString, decodedString);
        }
    }
    private void decode(Node root, StringBuilder encodedString, StringBuilder decodedString) {
        Node node = root;
        for (int i = 0; i < encodedString.length(); i++) {
            node = (encodedString.charAt(i) == '0') ? node.left : node.right;
            if (isLeaf(node)) {
                decodedString.append(node.character);
                node = root; // reset node to root node
            }
        }
    }

    // basic computations
    private void reCalculations() {
        // highest priority item has lowest frequency (minHeap)
        PriorityQueue<Node> pq = new PriorityQueue<Node>(Comparator.comparingInt(k -> k.frequency));
        // create a leaf node and add it to the priority queue
        for (var entry : frequency.entrySet())
            pq.add(new Node(entry.getKey(), entry.getValue()));
        
        // store only one node in pq
        while (pq.size() != 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            int sum = left.frequency + right.frequency;
            // create a new internal node with frequency as sum of both children, left, right, character null
            pq.add(new Node(null, sum, left, right));
        }

        // store the pointer to rhe root of huffman tree
        root = pq.peek();
    }

    // build huffman tree
    private void buildHuffmanTree(String text) {
        // base case
        if (text == null || text.length() == 0)
            return;
        
        for (char c : text.toCharArray())
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);

        reCalculations();

        // start encoding and store the huffman code
        encode(root, "", huffmanCode);

        // encoded data
        for (char ch : text.toCharArray())
            encodedString.append(huffmanCode.get(ch));

        // saving pass keys to external file to decode file again
        savePassKeys(frequency);
    }

    // util function to save pass keys
    private void savePassKeys(Map<Character, Integer> frequency) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("HuffmanProject/pass-keys.txt"));
            writer.append(encodedString.toString() + ":" + frequency);
            writer.close();
        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
        }
    }

    // encode file
    public void encodeFile(String path) {
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            scanner.close();
            buildHuffmanTree(data.toString());
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(encodedString.toString());
            writer.close();
        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
        }
    }

    // decode file
    public void decodeFile(String path) {
        StringBuilder encodedStringFromFile = new StringBuilder();
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                encodedStringFromFile.append(scanner.nextLine());
            }
            scanner.close();

            // finding that string in encoded keys file
            File keysFile = new File("HuffmanProject/pass-keys.txt");
            Scanner keysScanner = new Scanner(keysFile);
            String lineFromFile = "";
            while (keysScanner.hasNextLine()) {
                lineFromFile = keysScanner.nextLine();
                if (lineFromFile.split(":")[0].equals(encodedStringFromFile.toString()))
                    break;
            }
            keysScanner.close();
            
            // get mapped key:values
            int lineFromFileLength = lineFromFile.split(":")[1].length();
            lineFromFile = lineFromFile.split(":")[1].substring(1, lineFromFileLength-1).toString();
            
            Map<Character, Integer> mappedKey = new HashMap<>();
            String[] pairs = lineFromFile.split(",");
            
            // converting string to map
            for (int i = 0; i < pairs.length; i++) {
                String pair = pairs[i];
                String[] keyValue = pair.split("=");
                mappedKey.put(keyValue[0].trim().charAt(0), Integer.parseInt(keyValue[1]));
            }

            frequency.clear();
            frequency.putAll(mappedKey);

            reCalculations();

            decode(root, encodedStringFromFile);

            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(decodedString.toString());
            writer.close();
        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        HuffmanEncodingDecoding huffman = new HuffmanEncodingDecoding();
        huffman.encodeFile("HuffmanProject/file.txt");
        // huffman.decodeFile("HuffmanProject/file.txt");
    }

}
