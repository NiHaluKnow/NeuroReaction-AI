// import java.io.*;
// import java.util.*;

// public class GameState {
//     private String currentPlayer;
//     private int[][] orbCounts;
//     private char[][] orbColors;
//     private int rows = 9;
//     private int cols = 6;

//     public GameState() {
//         orbCounts = new int[rows][cols];
//         orbColors = new char[rows][cols];
//         currentPlayer = "Human";
//     }

//     public int[][] getOrbCounts() {
//         return orbCounts;
//     }

//     public char[][] getOrbColors() {
//         return orbColors;
//     }

//     public String getCurrentPlayer() {
//         return currentPlayer;
//     }

//     public void loadFromFile(String filename) throws IOException {
//         BufferedReader reader = new BufferedReader(new FileReader(filename));
//         currentPlayer = reader.readLine().replace(":", "").trim();
        
//         for (int i = 0; i < rows; i++) {
//             String[] parts = reader.readLine().split(" ");
//             for (int j = 0; j < cols; j++) {
//                 if (parts[j].equals("0")) {
//                     orbCounts[i][j] = 0;
//                     orbColors[i][j] = ' ';
//                 } else {
//                     orbCounts[i][j] = Integer.parseInt(parts[j].substring(0, parts[j].length() - 1));
//                     orbColors[i][j] = parts[j].charAt(parts[j].length() - 1);
//                 }
//             }
//         }
//         reader.close();
//     }

//     public void saveToFile(String filename) throws IOException {
//         PrintWriter writer = new PrintWriter(new FileWriter(filename));
//         writer.println(currentPlayer + ":");
        
//         for (int i = 0; i < rows; i++) {
//             for (int j = 0; j < cols; j++) {
//                 if (orbCounts[i][j] == 0) {
//                     writer.print("0");
//                 } else {
//                     writer.print(orbCounts[i][j] + "" + orbColors[i][j]);
//                 }
//                 if (j < cols - 1) writer.print(" ");
//             }
//             writer.println();
//         }
//         writer.close();
//     }

//     public void makeMove(int row, int col, char color) {
//         if (orbColors[row][col] == ' ' || orbColors[row][col] == color) {
//             orbCounts[row][col]++;
//             orbColors[row][col] = color;
//             processExplosions();
//             currentPlayer = currentPlayer.equals("Human") ? "AI" : "Human";
//         }
//     }

//     private void processExplosions() {
//         boolean changed;
//         do {
//             changed = false;
//             for (int i = 0; i < rows; i++) {
//                 for (int j = 0; j < cols; j++) {
//                     if (orbCounts[i][j] >= getCriticalMass(i, j)) {
//                         explode(i, j);
//                         changed = true;
//                     }
//                 }
//             }
//         } while (changed);
//     }

//     private void explode(int row, int col) {
//         char color = orbColors[row][col];
//         int criticalMass = getCriticalMass(row, col);
//         orbCounts[row][col] -= criticalMass;
//         if (orbCounts[row][col] == 0) {
//             orbColors[row][col] = ' ';
//         }

//         // Spread to adjacent cells
//         int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//         for (int[] dir : directions) {
//             int newRow = row + dir[0];
//             int newCol = col + dir[1];
//             if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
//                 if (orbColors[newRow][newCol] == ' ' || orbColors[newRow][newCol] == color) {
//                     orbCounts[newRow][newCol]++;
//                     orbColors[newRow][newCol] = color;
//                 } else {
//                     // Convert opponent's orbs
//                     orbColors[newRow][newCol] = color;
//                     orbCounts[newRow][newCol] = 1;
//                 }
//             }
//         }
//     }

//     public int getCriticalMass(int row, int col) {
//         if ((row == 0 || row == rows - 1) && (col == 0 || col == cols - 1)) {
//             return 2; // Corner
//         } else if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
//             return 3; // Edge
//         } else {
//             return 4; // Middle
//         }
//     }

//     public boolean isGameOver() {
//         boolean hasRed = false;
//         boolean hasBlue = false;
//         for (int i = 0; i < rows; i++) {
//             for (int j = 0; j < cols; j++) {
//                 if (orbColors[i][j] == 'R') hasRed = true;
//                 if (orbColors[i][j] == 'B') hasBlue = true;
//                 if (hasRed && hasBlue) return false;
//             }
//         }
//         return true;
//     }

//     public char getWinner() {
//         for (int i = 0; i < rows; i++) {
//             for (int j = 0; j < cols; j++) {
//                 if (orbColors[i][j] == 'R') return 'R';
//                 if (orbColors[i][j] == 'B') return 'B';
//             }
//         }
//         return ' ';
//     }

//     public GameState clone() {
//         GameState newState = new GameState();
//         newState.currentPlayer = this.currentPlayer;
//         for (int i = 0; i < rows; i++) {
//             for (int j = 0; j < cols; j++) {
//                 newState.orbCounts[i][j] = this.orbCounts[i][j];
//                 newState.orbColors[i][j] = this.orbColors[i][j];
//             }
//         }
//         return newState;
//     }

//     public List<int[]> getPossibleMoves(char playerColor) {
//         List<int[]> moves = new ArrayList<>();
//         for (int i = 0; i < rows; i++) {
//             for (int j = 0; j < cols; j++) {
//                 if (orbColors[i][j] == ' ' || orbColors[i][j] == playerColor) {
//                     moves.add(new int[]{i, j});
//                 }
//             }
//         }
//         return moves;
//     }

    
// }