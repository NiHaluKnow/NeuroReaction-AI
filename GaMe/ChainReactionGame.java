package GaMe;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import Heuristics.Heuristics;

public class ChainReactionGame {
    public int rows;
    public int cols;
    public Cell[][] board;
    public char currentPlayer; // 'R' or 'B'
    public boolean gameOver;
    public Character winner;
    Heuristics heuristics = new Heuristics();

    public ChainReactionGame() {
        this(9, 6); //initial board size
    }

    public ChainReactionGame(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell(0, null); //each board cell starts with 0 mass and no player
            }
        }
        this.currentPlayer = 'R'; // Red first
        this.gameOver = false;
        this.winner = null;

    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Character getWinner() {
        return winner;
    }

    public void setCurrentPlayer(char player) {
        if (player == 'R' || player == 'B') {
            this.currentPlayer = player;
        } else {
            throw new IllegalArgumentException("Player must be 'R' or 'B'");
        }
    }

    public ChainReactionGame(ChainReactionGame game2) {
        this.rows = game2.rows;
        this.cols = game2.cols;
        this.board = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.board[i][j] = new Cell(game2.board[i][j].mass, game2.board[i][j].player);
            }
        }
        this.currentPlayer = game2.currentPlayer;
        this.gameOver = game2.gameOver;
        this.winner = game2.winner;
    }

    public int getCriticalMass(int row, int col) {
        if ((row == 0 || row == rows - 1) && (col == 0 || col == cols - 1)) {
            return 2; // Corner case
        } else if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
            return 3; // Edge case
        }
        return 4; // Middle case
    }

    public boolean addOrb(int row, int col, char player) {
        if (gameOver) {
            return false; 
        }
        if(row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        Cell cell = board[row][col];
        if (cell.player != null && cell.player != player) {
            //oppnent occupied cell
            return false;
        }
        cell.mass++;
        cell.player = player;

        if (cell.mass >= getCriticalMass(row, col)) {
            explode(row, col, player);
        }
        if (!gameOver) {
            currentPlayer = (player == 'R') ? 'B' : 'R';
        }
        System.out.println("Added orb at (" + row + ", " + col + ") for player " + player);
        return true;
    }

    public void explode(int row, int col, char player) {

        // if(isGameOver()){
        //     return;
        // }
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { row, col });

        while (!queue.isEmpty()) {
            int[] pos = queue.poll(); //poll retrieves the head , null for empty queue
            int r = pos[0]; //row
            int c = pos[1]; //col
            Cell cell = board[r][c];
            int criticalMass = getCriticalMass(r, c);

            if (cell.mass < criticalMass) {
                //not enough mass to explode
                continue;
            }

            cell.mass = cell.mass - criticalMass;
            if (cell.mass == 0) {
                cell.player = null; //exploid ar por player clear
            }

            // Distribute orbs to adjacent cells
            int[][] directions = {
                    { -1, 0 },
                    { 1, 0 },
                    { 0, -1 },
                    { 0, 1 }
            };

            for (int[] dir : directions) {
                int newRow = r + dir[0];
                int newCol = c + dir[1];
                // if( newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                //     System.out.println("Player : " + player + " exploding orb at (" + r + ", " + c + ") and distributing to (" + newRow + ", " + newCol + ")");
                // }
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    Cell adjCell = board[newRow][newCol];
                    
                    // Convert opponent's orbs 
                    if (adjCell.player != null && adjCell.player != player) {
                        //B != R
                        adjCell.mass++;
                        adjCell.player = player;
                        System.out.println("Converted opponent's orb at (" + newRow + ", " + newCol + ") for player " + player + " after explosion ");
                    } else {
                        //R == R or B == B or empty cell
                        adjCell.mass++;
                        adjCell.player = player;
                        System.out.println("Added orb at (" + newRow + ", " + newCol + ") for player " + player + " after explosion ");
                    }
                    // recursively explode 
                    if (adjCell.mass >= getCriticalMass(newRow, newCol)) {
                        queue.add(new int[] { newRow, newCol });
                    }
                    
                }else{
                    continue; 
                }
            }
        }
        checkGameOver();
    }

    public void checkGameOver() {
        boolean redExists = false;
        boolean blueExists = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].player != null) {
                    if (board[i][j].player == 'R') {
                        redExists = true;
                        //System.out.println("Red exists at (" + i + ", " + j + ")");
                    } else if (board[i][j].player == 'B') {
                        blueExists = true;
                        //System.out.println("Blue exists at (" + i + ", " + j + ")");
                    }
                }
            }
        }
        if (!redExists) {
            gameOver = true;
            winner = 'B';
        } else if (!blueExists) {
            gameOver = true;
            winner = 'R';
        }
    }

    public List<int[]> getLegalMoves(char player) {
        List<int[]> moves = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c].player == null || board[r][c].player == player) {
                    moves.add(new int[] { r, c });
                }
            }
        }
        return moves;
    }

    public ChainReactionGame makeMove(int[] move, char player) {
        ChainReactionGame newGame = new ChainReactionGame(this);
        newGame.addOrb(move[0], move[1], player);
        return newGame;
    }

    public boolean isTerminal() {
        return gameOver;
    }

    public double evaluate(char player, String heuristic) {
        if (gameOver) {
            if (winner != null && winner == player) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }
        switch (heuristic) {
            case "orb_count":
                return heuristics.orbCountHeuristic(player, this);
            case "critical_mass":
                return heuristics.criticalMassHeuristic(player, this);
            case "strategic_position":
                return heuristics.strategicPositionHeuristic(player, this);
            case "conversion_potential":
                return heuristics.conversionPotentialHeuristic(player, this);
            case "growth_potential":
                return heuristics.growthPotentialHeuristic(player, this);
            case "killer_move":
                return heuristics.killerMoveHeuristic(player, this);
            default:
                return (0.2 * heuristics.orbCountHeuristic(player, this) +
                        0.3 * heuristics.criticalMassHeuristic(player, this) +
                        0.2 * heuristics.strategicPositionHeuristic(player, this) +
                        0.2 * heuristics.conversionPotentialHeuristic(player, this) +
                        0.1 * heuristics.growthPotentialHeuristic(player, this));
        }
    }

    public void copyFromState(GameState state) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.board[i][j].mass = state.game.board[i][j].mass;
                this.board[i][j].player = state.game.board[i][j].player;
            }
        }
        this.currentPlayer = state.game.currentPlayer;
        this.winner = state.game.winner;
        this.gameOver = state.game.gameOver;
    }

    public void printInitialBoard(PrintWriter writer) {
        writer.println("Initial board:");
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getCols(); j++) {
                Cell cell = this.getBoard()[i][j];
                if (cell.mass == 0) {
                    writer.print("0 ");
                } else {
                    writer.print(cell.mass + "" + cell.player + " ");
                }
            }
            writer.println();
        }
    }

    public void saveGameState(String filename, String player, ChainReactionGame game) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(player);
            for (int i = 0; i < game.rows; i++) {
                for (int j = 0; j < game.cols; j++) {
                    Cell cell = game.board[i][j];
                    writer.print(cell.mass == 0 ? "0 " : cell.mass + "" + cell.player + " ");
                }
                writer.println();
            }
        }
    }

    public GameState loadGameState(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String playerLine = reader.readLine();
            if (playerLine == null) {
                throw new IOException("Empty game state file.");
            }
            playerLine = playerLine.trim();
            String player = playerLine.contains("Human") ? "Human" : "AI";

            List<String> lines = reader.lines().collect(Collectors.toList());
            if (lines.isEmpty()) {
                throw new IOException("Invalid game state: board data missing.");
            }

            int rows = lines.size();
            int cols = lines.get(0).trim().split(" ").length;

            // Validate board format
            for (String line : lines) {
                if (line.trim().split(" ").length != cols) {
                    throw new IOException("Malformed board: inconsistent number of columns.");
                }
            }

            ChainReactionGame game = new ChainReactionGame(rows, cols);

            for (int i = 0; i < rows; i++) {
                String[] cells = lines.get(i).trim().split(" ");
                for (int j = 0; j < cols; j++) {
                    String cell = cells[j].trim();
                    if (cell.equals("0")) {
                        game.board[i][j] = new Cell(0, null);
                    } else {
                        int mass = Integer.parseInt(cell.substring(0, cell.length() - 1));
                        char cellPlayer = cell.charAt(cell.length() - 1);
                        game.board[i][j] = new Cell(mass, cellPlayer);
                    }
                }
            }
            //currentPlayer is last moved at gameState.txt
            if (player.equals("Human")) {
                game.currentPlayer = 'B';
            } else {
                game.currentPlayer = 'R';
            }

            game.checkGameOver();
            return new GameState(player, game);
        }
    }
}