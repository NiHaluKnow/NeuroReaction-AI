package Ai_Player;
import java.util.*;
import GaMe.ChainReactionGame;

public class AIPlayer {
    public char player;
    public int depth;               // depth of the minimax search for the AI
    public String heuristic;
    public int nodesEvaluated;
    public int pruningCount;

    /* Default constructor with player color
     * @param player: the color of the player ('R' or 'B')
     * depth : 3 
     * heuristic : "combined" 
    */
    public AIPlayer(char player) {
        this(player, 3, "combined");
    }

    /*constructor with depth and heuristic
        * @param player: the color of the player ('R' or 'B')
        * @param depth: the depth of the minimax search
        * @param heuristic: the heuristic to use for evaluation 
        * nodesEvaluated: number of nodes evaluated during the search
        * pruningCount: number of times pruning occurred during the search
    */
    public AIPlayer(char player, int depth, String heuristic) {
        this.player = player;
        this.depth = depth;
        this.heuristic = heuristic;
        this.nodesEvaluated = 0;
        this.pruningCount = 0;
    }

    public char getPlayerColor() {
        return player;
    }

    /*
     * getMove method to get the moves for the AI player
     * @param game: the current state of the game
     * @return: the best move for the AI player
     * This method uses the minimax algorithm with alpha-beta pruning to find the best move.
     * It also tracks the number of nodes evaluated and the number of times pruning occurred.
    */
    public int[] getMove(ChainReactionGame game) {
        
        nodesEvaluated = 0;
        pruningCount = 0;

        long startTime = System.currentTimeMillis(); // starting time

        MinimaxResult result = minimax(game, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true); //try to maximize the player's score

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.printf("\nAI (%c) evaluated %d nodes in %.2fs%n",
                player, nodesEvaluated, elapsedTime / 1000.0);
        System.out.printf("Pruning occurred %d times\n", pruningCount);

        return result.move;
    }
    
    /*
     * minimax method to perform the minimax algorithm with alpha-beta pruning
     * @param game: the current state of the game
     * @param depth: the current depth in the search tree
     * @param alpha: the best score that the maximizer currently can guarantee at that level or above
     * @param beta: the best score that the minimizer currently can guarantee at that level or above
     * @param maximizingPlayer: true if the current player is the maximizer (AI player), false if it is the minimizer (opponent)
     * @return: a MinimaxResult object containing the best value and the corresponding move
     */
    public MinimaxResult minimax(ChainReactionGame game, int depth, double alpha, double beta, boolean maximizingPlayer) {

        nodesEvaluated++;

        if (depth == 0 || game.isTerminal()) {
            return new MinimaxResult(game.evaluate(player, heuristic), null);
        }

        if (maximizingPlayer) {
            double value = Double.NEGATIVE_INFINITY; // maximize the player's score
            int[] bestMove = null;
            List<int[]> moves = game.getLegalMoves(player);

            for (int[] move : moves) {
                ChainReactionGame newGame = game.makeMove(move, player);
                MinimaxResult result = minimax(newGame, depth - 1, alpha, beta, false);

                if (result.value > value) {
                    value = result.value;
                    bestMove = move;
                }

                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    pruningCount++;
                    break;
                }
            }
            return new MinimaxResult(value, bestMove);

        } else {
            double value = Double.POSITIVE_INFINITY; // minimize the opponent's score
            int[] bestMove = null;
            char opponent = (player == 'R') ? 'B' : 'R';
            List<int[]> moves = game.getLegalMoves(opponent);

            for (int[] move : moves) {
                ChainReactionGame newGame = game.makeMove(move, opponent);
                MinimaxResult result = minimax(newGame, depth - 1, alpha, beta, true);

                if (result.value < value) {
                    value = result.value;
                    bestMove = move;
                }

                beta = Math.min(beta, value);
                if (alpha >= beta) {
                    pruningCount++;
                    break;
                }
            }

            return new MinimaxResult(value, bestMove);
        }
    }

}