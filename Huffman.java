import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/*
 * base source code
*/

// tree node
class Node {
    Node left = null, right = null;
    Character character;
    Integer frequency;
    Node (Character character, Integer frequency) {
        this.character = character;
        this.frequency = frequency;
    }
    Node (Character character, Integer frequency, Node left, Node right) {
        this.character = character;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
}

public class Huffman {

    public static boolean isLeaf(Node root) {
        return root.left == null && root.right == null;
    }

    // traverse huffman tree and store huffman code in map
    public static void encode(Node root, String expression, Map<Character, String> huffmanCode) {
        if (root == null)
            return;
        if (isLeaf(root))
            huffmanCode.put(root.character, expression.length() > 0 ? expression : "1");
        encode(root.left, expression + "0", huffmanCode);
        encode(root.right, expression + "1", huffmanCode);
    }

    // traverse huffman tree and decode the encoded string
    public static void decode(Node root, StringBuilder encodedString, StringBuilder decodedString) {
        Node node = root;
        for (int i=0; i<encodedString.length(); i++) {
            node = (encodedString.charAt(i) == '0') ? node.left : node.right;
            if (isLeaf(node)) {
                decodedString.append(node.character);
                node = root; // reset node to root node
            }
        }
    }

    // build huffman tree
    public static void buildHuffmanTree(String text) {
        // base case
        if (text == null || text.length() == 0)
            return;
        
        // count the frequency of each appearing character
        Map<Character, Integer> frequency = new HashMap<Character, Integer>();
        for (char c : text.toCharArray())
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);
        
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
        Node root = pq.peek();

        // start encoding and store the huffman code
        Map<Character, String> huffmanCode = new HashMap<Character, String>();
        encode(root, "", huffmanCode);

        // encoded data
        StringBuilder encodedString = new StringBuilder();
        for (char ch : text.toCharArray())
            encodedString.append(huffmanCode.get(ch));
        System.out.println("encoded string -> " + encodedString.toString());

        // decode the string
        StringBuilder decodedString = new StringBuilder();
        if (isLeaf(root)) {
            // special case inputs like "a". "aa", "aaa", ...
            while (root.frequency-- > 0) {
                decodedString.append(root.character);
            }
        } else {
            decode(root, encodedString, decodedString);
        }
        System.out.println("decoded string -> " + decodedString.toString());

    }

    public static void main(String[] args) {
        buildHuffmanTree("huffman coding & decoding");
        // encoded string - 101000011101110111010111100100011111111010100001001100011001001101011101111111010100001001100
        // decoded string - huffman coding & decoding
    }
}
