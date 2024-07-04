/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private String[] dictionary;
    private static final int R = 26;

    private static class Node {
        private Integer val;
        private Node[] next = new Node[R];

    }

    private Node root;

    private void put(String key, Integer val) {
        root = put(root, key, val, 0);
    }

    private Node put(Node node, String key, Integer val, int charAt) {
        if (node == null) {
            node = new Node();
        }
        if (charAt == key.length()) {
            node.val = val;
            return node;
        }
        char c = key.charAt(charAt++);
        node.next[c - 'A'] = put(node.next[c - 'A'], key, val, charAt);
        return node;
    }

    private Integer get(String key) {
        Node check = get(root, key, 0);
        if (check == null) {
            return null;
        }
        return check.val;
    }

    private Node get(Node node, String key, int charAt) {
        if (node == null) {
            return null;
        }
        if (charAt == key.length()) {
            return node;
        }
        char c = key.charAt(charAt++);
        return get(node.next[c - 'A'], key, charAt);

    }

    private boolean contains(String key) {
        Integer i = get(key);
        return i != null;
    }

    private Node helper(String prefix, Node node, int charAt) {
        int length = prefix.length();
        if (node == null) {
            return null;
        }
        Node currentNode = node.next[prefix.charAt(charAt++) - 'A'];
        while (currentNode != null && charAt < length) {
            currentNode = currentNode.next[prefix.charAt(charAt++) - 'A'];
        }
        return currentNode;
    }

    // no need of this
    private Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> prefixes = new Queue<String>();
        int chatAt = 0;
        int length = prefix.length();
        if (root == null) {
            return prefixes;
        }
        Node currentNode = root.next[prefix.charAt(chatAt++) - 'A'];
        while (currentNode != null && chatAt < length) {
            currentNode = currentNode.next[prefix.charAt(chatAt++) - 'A'];
        }
        collect(currentNode, prefixes);
        return prefixes;
    }

    private void collect(Node node, Queue<String> prefixes) {
        if (node == null) {
            return;
        }
        Integer val = node.val;
        if (val != null) {
            prefixes.enqueue(dictionary[val]);
        }
        for (int i = 0; i < R; i++) {
            collect(node.next[i], prefixes);
        }
    }


    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = dictionary.clone();
        int length = dictionary.length;
        for (int i = 0; i < length; i++) {
            if (dictionary[i].length() >= 3) {
                put(dictionary[i], i);
            }
        }

    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Queue<String> solution = new Queue<String>();
        boolean[] marked = new boolean[dictionary.length];
        String helper = "";
        for (int i = 0, row = board.rows(); i < row; i++) {
            for (int j = 0, col = board.cols(); j < col; j++) {
                boolean[][] visted = new boolean[board.rows()][board.cols()];
                solver(board, root, 0, marked, visted, solution, helper, i, j);
            }
        }
        return solution;
    }

    private void solver(BoggleBoard board, Node node, int charAt, boolean[] marked,
                        boolean[][] visited,
                        Queue<String> solution,
                        String helper, int row, int col) {

        StringBuilder sb = new StringBuilder(helper);
        visited[row][col] = true;
        char letter = board.getLetter(row, col);
        if (letter == 'Q') {
            sb.append("QU");
        }
        else {
            sb.append(letter);
        }
        String newString = sb.toString();
        Node current = helper(newString, node, charAt);
        // System.out.println("Check: " + newString);
        if (current == null) {
            visited[row][col] = false;
            return;
        }
        Integer val = current.val;
        if (val != null) {
            if (!marked[val]) {
                solution.enqueue(dictionary[val]);
                marked[val] = true;
            }
        }
        // System.out.println(newString);
        int length = newString.length();
        int[] rowOffsets = { -1, -1, -1, 0, 0, 1, 1, 1 };
        int[] colOffsets = { -1, 0, 1, -1, 1, -1, 0, 1 };
        int newRow = 0;
        int newCol = 0;
        for (int i = 0; i < rowOffsets.length; i++) {
            newRow = row + rowOffsets[i];
            newCol = col + colOffsets[i];
            if (newRow < 0 || newRow >= board.rows() || newCol < 0 || newCol >= board.cols()
                    || visited[newRow][newCol]) {
                continue;
            }
            solver(board, current, length, marked, visited, solution, newString,
                   row + rowOffsets[i],
                   col + colOffsets[i]);
        }
        visited[row][col] = false;

    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int length = word.length();
        int val = 0;
        if (contains(word)) {
            switch (length) {
                case 3:
                case 4:
                    val = 1;
                    break;
                case 5:
                    val = 2;
                    break;
                case 6:
                    val = 3;
                    break;
                case 7:
                    val = 5;
                    break;
                default:
                    val = 11;
                    break;
            }
        }
        return val;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}

