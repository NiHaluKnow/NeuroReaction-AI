import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Ai_Player.AIPlayer;
import GaMe.Cell;
import GaMe.ChainReactionGame;

public class ChainReactionLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String stateFile = "gamestate.txt";

        System.out.println("Chain Reaction Game");
        System.out.println("Choose game mode:");
        System.out.println("1. Human vs Human");
        System.out.println("2. Human vs AI");
        System.out.println("3. AI vs AI");
        System.out.print("Enter your choice (1-3): ");

        int choice = scanner.nextInt();

        ChainReactionGame game = new ChainReactionGame(4, 3);
        ChainReactionLauncher launcher = new ChainReactionLauncher();
        AIPlayer redAI = null;
        AIPlayer blueAI = null;

        switch (choice) {
            case 1:
                // Human vs Human play
                launcher.playHumanVsHuman(game, scanner, stateFile, choice);
                break;
            case 2:
                // Human vs AI
                // Human play as Red;
                blueAI = new AIPlayer('B', 3, "combined");
                try {
                    launcher.playHumanVsAI(game, scanner, 'R', blueAI, stateFile, choice);
                } catch (InterruptedException e) {
                    System.out.println("Game interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
                break;
            case 3:
                // AI vs AI play
                redAI = new AIPlayer('R', 3, "combined");
                blueAI = new AIPlayer('B', 3, "combined");
                launcher.playAIVsAI(game, redAI, blueAI, scanner, stateFile, choice);
                break;
            default:
                System.out.println("Invalid choice!");
        }

        scanner.close();
    }

    public void printBoard(ChainReactionGame game, int mode) {

        for (int i = 0; i < game.getRows(); i++) {
            for (int j = 0; j < game.getCols(); j++) {
                Cell cell = game.getBoard()[i][j];
                if (cell.mass == 0) {
                    System.out.print("0 ");
                } else {
                    System.out.print(cell.mass + "" + cell.player + " ");
                }
            }
            System.out.println();
        }
    }

    public void saveInitialBoard(String filename, ChainReactionGame game) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Initial board:");
            for (int i = 0; i < game.rows; i++) {
                for (int j = 0; j < game.cols; j++) {
                    writer.print("0 ");
                }
                writer.println();
            }
        }
    }

    public void playHumanVsHuman(ChainReactionGame game, Scanner scanner, String stateFile, int mode) {
        System.out.println("Human vs Human");

        try {
            saveInitialBoard(stateFile, game); // buffer writer
            System.out.println("Initial board:");
            printBoard(game, mode); // console print
        } catch (IOException e) {
            System.out.println("Failed to initialize game: " + e.getMessage());
            return;
        }

        while (!game.isGameOver()) {
            char currentPlayer = game.getCurrentPlayer();
            System.out.print("Player " + (currentPlayer == 'R' ? "Red" : "Blue") +
                    ", enter row(0-" + (game.getRows() - 1) + ") and column(0-" + (game.getCols() - 1) + "): ");

            int row = scanner.nextInt();
            int col = scanner.nextInt();

            if (game.addOrb(row, col, currentPlayer)) {
                try {
                    game.saveGameState(stateFile, "Human Move:", game);
                    System.out.println("Human(" + (currentPlayer == 'R' ? "Red" : "Blue") + ") Move:");
                    printBoard(game, mode);
                } catch (IOException e) {
                    System.out.println("Failed to save game state: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid move! Try again.");
            }
        }
        endGame(game, stateFile);
    }

    public void playHumanVsAI(ChainReactionGame game, Scanner scanner,
            char humanPlayer, AIPlayer ai, String stateFile, int mode)
            throws InterruptedException {
        System.out.println("Human vs AI match");

        try {
            saveInitialBoard(stateFile, game);
            System.out.println("Initial Board:");
            printBoard(game, mode);
        } catch (IOException e) {
            System.out.println("Failed to save initial board: " + e.getMessage());
        }

        while (!game.isGameOver()) {
            if (game.getCurrentPlayer() == humanPlayer) {
                System.out.print("Player " + (humanPlayer == 'R' ? "Red" : "Blue") +
                        ", enter row(0-8) and column(0-5): ");
                int row = scanner.nextInt();
                int col = scanner.nextInt();

                if (game.addOrb(row, col, humanPlayer)) {
                    try {
                        game.saveGameState(stateFile, "Human Move:", game);
                        System.out.println("Human(" + (humanPlayer == 'R' ? "Red" : "Blue") + ") Move:");
                        printBoard(game, mode);
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        System.out.println("Failed to save game state: " + e.getMessage());
                    }
                } else {
                    System.out.println("Invalid move! Try again.");
                }
            } else {
                System.out.println("AI is thinking...");
                Thread.sleep(1500); // AI thinks for 1.5 seconds

                int[] move = ai.getMove(game);
                System.out.println("AI chooses: (" + move[0] + ", " + move[1] + ")");

                if (game.addOrb(move[0], move[1], game.getCurrentPlayer())) {
                    try {
                        game.saveGameState(stateFile, "AI Move:", game);
                        System.out.println("AI Move:");
                        printBoard(game, mode);
                    } catch (IOException e) {
                        System.out.println("Failed to save game state: " + e.getMessage());
                    }
                }
                Thread.sleep(500); // Add delay before human sees the result
            }
        }
        endGame(game, stateFile);
    }

    public void playAIVsAI(ChainReactionGame game,
            AIPlayer redAI, AIPlayer blueAI,
            Scanner scanner, String stateFile, int mode) {
        System.out.println("AI vs AI");

        System.out.print("Enter delay between moves in milliseconds (0 for no delay): ");
        int delay = scanner.nextInt();

        try {
            saveInitialBoard(stateFile, game);
            System.out.println("Initial board:");
            printBoard(game, mode);
        } catch (IOException e) {
            System.out.println("Failed to save game state: " + e.getMessage());
        }
        while (!game.isGameOver()) {

            int[] move;
            if (game.getCurrentPlayer() == 'R') {
                move = redAI.getMove(game);
                System.out.println("Red AI chooses: (" + move[0] + ", " + move[1] + ")");
            } else {
                move = blueAI.getMove(game);
                System.out.println("Blue AI chooses: (" + move[0] + ", " + move[1] + ")");
            }

            if (game.addOrb(move[0], move[1], game.getCurrentPlayer())) {
                try {
                    game.saveGameState(stateFile, game.getCurrentPlayer() == 'R' ? "AI(Red) Move:" : "AI(Blue) Move:",
                            game);
                    System.out.println("AI Move:");
                } catch (IOException e) {
                    System.out.println("Failed to save game state: " + e.getMessage());
                }
                printBoard(game, mode);
            }
            // choosed delay by the user
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        endGame(game, stateFile);
    }

    public void endGame(ChainReactionGame game, String stateFile) {
        System.out.println("\nFinal board:");

        for (int i = 0; i < game.getRows(); i++) {
            for (int j = 0; j < game.getCols(); j++) {
                Cell cell = game.getBoard()[i][j];
                if (cell.mass == 0) {
                    System.out.print("0 ");
                } else {
                    System.out.print(cell.mass + "" + cell.player + " ");
                }
            }
            System.out.println();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(stateFile))) {
            writer.println(game.getCurrentPlayer() == 'R' ? "Red" : "Blue" + " Move:");
            for (int i = 0; i < game.rows; i++) {
                List<String> rowStr = new ArrayList<>();
                for (int j = 0; j < game.cols; j++) {
                    Cell cell = game.board[i][j];
                    if (cell.mass == 0) {
                        rowStr.add("0");
                    } else {
                        rowStr.add(cell.mass + String.valueOf(cell.player));
                    }
                }
                writer.println(String.join(" ", rowStr));
            }
        } catch (IOException e) {
            System.out.println("Failed to save game state: " + e.getMessage());
        }
        System.out.println("Game over! Winner: " + (game.getWinner() == 'R' ? "Red" : "Blue"));
        System.out.println("Game state saved to " + stateFile);
    }

}