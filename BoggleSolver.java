/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieST;

public class BoggleSolver {
    private String[] dictionary;
    private TrieST<Integer> tstForDictionary;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = dictionary.clone();
        int length = dictionary.length;
        tstForDictionary = new TrieST<Integer>();
        for (int i = 0; i < length; i++) {
            if (dictionary[i].length() >= 3) {
                tstForDictionary.put(dictionary[i], i);
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
                solver(board, marked, visted, solution, helper, i, j);
            }
        }
        return solution;
    }

    private void solver(BoggleBoard board, boolean[] marked, boolean[][] visited,
                        Queue<String> solution,
                        String helper, int row, int col) {
        // System.out.println("row: " + row + ", col: " + col);
        // System.out.println("visited");
        // for (int i = 0; i < board.rows(); i++) {
        //     System.out.print("[ ");
        //     for (int j = 0; j < board.cols(); j++) {
        //         System.out.print(visited[i][j] + ", ");
        //     }
        //     System.out.print("]");
        // }

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
        // System.out.println("Check: " + newString);
        if (!tstForDictionary.keysWithPrefix(newString).iterator().hasNext()) {
            visited[row][col] = false;
            return;
        }
        if (tstForDictionary.contains(newString)) {
            int val = tstForDictionary.get(newString);
            if (!marked[val]) {
                solution.enqueue(dictionary[val]);
                marked[val] = true;
            }
        }
        // System.out.println(newString);
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
            solver(board, marked, visited, solution, newString, row + rowOffsets[i],
                   col + colOffsets[i]);
        }
        visited[row][col] = false;

    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int length = word.length();
        int val = 0;
        if (tstForDictionary.contains(word)) {
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

