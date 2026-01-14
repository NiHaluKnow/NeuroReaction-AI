package Heuristics;
import GaMe.ChainReactionGame;
import GaMe.Cell;

public class Heuristics {
    

    public Heuristics() {
    }

    public double criticalMassHeuristic(char player, ChainReactionGame game) {
        int rows = game.getRows();
        int cols = game.getCols();
        Cell[][] board = game.getBoard();
        double score = 0;
        char opponent = (player == 'R') ? 'B' : 'R';
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = board[r][c];
                int criticalMass = game.getCriticalMass(r, c);
                
                if (cell.player != null) {
                    if (cell.player == player) {
                        score += (double) cell.mass / criticalMass;
                    } else if (cell.player == opponent) {
                        score -= (double) cell.mass / criticalMass;
                    }
                }
            }
        }
        
        return score;
    }

    public double orbCountHeuristic(char player,ChainReactionGame game) {
        int rows = game.getRows();
        int cols = game.getCols();
        Cell[][] board = game.getBoard();
        int playerOrbs = 0;
        int opponentOrbs = 0;
        char opponent = (player == 'R') ? 'B' : 'R';
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board[i][j];
                if (cell.player != null) {
                    if (cell.player == player) {
                        playerOrbs += cell.mass;
                    } else if (cell.player == opponent) {
                        opponentOrbs += cell.mass;
                    }
                }
            }
        }
        
        return playerOrbs - opponentOrbs;
    }

    public double strategicPositionHeuristic(char player, ChainReactionGame game) {
        double score = 0;
        char opponent = (player == 'R') ? 'B' : 'R';
        int rows = game.getRows();
        int cols = game.getCols();
        Cell[][] board = game.getBoard();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = board[r][c];
                if (cell.mass == 0) continue;
                
                double positionValue = 1.0; // Default for center
                int criticalMass = game.getCriticalMass(r, c);
                
                if (criticalMass == 2) { 
                    // Corner
                    positionValue = 2.0;
                } else if (criticalMass == 3) {
                    // Edge
                    positionValue = 1.5;
                }
                
                if (cell.player != null) {
                    if (cell.player == player) {
                        score += positionValue * cell.mass;
                    } else if (cell.player == opponent) {
                        score -= positionValue * cell.mass;
                    }
                }
            }
        }
        
        return score;
    }

    public double killerMoveHeuristic(char player, ChainReactionGame game) {
    int rows = game.getRows();
    int cols = game.getCols();
    Cell[][] board = game.getBoard();
    char opponent = (player == 'R') ? 'B' : 'R';

    double score = 0.0;

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            Cell cell = board[i][j];
            if (cell.player != null && cell.player == player) {
                int criticalMass = game.getCriticalMass(i, j);
                int danger = criticalMass - cell.mass;
                if (danger == 1) {
                    score += 5.0; 
                } else if (danger == 0) {
                    score += 8.0;
                }
                int[][] directions = {
                                        {1,0}, // Down
                                        {-1,0}, // Up
                                        {0,1}, // Right
                                        {0,-1} // Left
                                    };
                for (int[] dir : directions) {
                    int ni = i + dir[0];
                    int nj = j + dir[1];
                    if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                        Cell neighbor = board[ni][nj];
                        if (neighbor.player != null && neighbor.player == opponent) {
                            score += 2.0;
                        }
                    }
                }
            }
        }
    }

    return score;
}

    
    public double conversionPotentialHeuristic(char player, ChainReactionGame game) {
        int rows = game.getRows();
        int cols = game.getCols();
        Cell[][] board = game.getBoard();
        double score = 0;
        char opponent = (player == 'R') ? 'B' : 'R';
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = board[r][c];
                int criticalMass = game.getCriticalMass(r, c);

                if (cell.player != null && cell.player == player && cell.mass == criticalMass - 1) {
             
                    for (int[] dir : directions) {
                        int nr = r + dir[0], nc = c + dir[1];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                            Cell adjCell = board[nr][nc];
                            if (adjCell.player != null && adjCell.player == opponent) {
                                score += adjCell.mass * 2; 
                            }
                        }
                    }
                }
            }
        }
        return score;
    }

    public double growthPotentialHeuristic(char player, ChainReactionGame game) {
        int rows = game.getRows();
        int cols = game.getCols();
        Cell[][] board = game.getBoard();
        double score = 0;
        char opponent = (player == 'R') ? 'B' : 'R';
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = board[r][c];
                
                if (cell.player == null) {
                    // Empty cell - potential for growth
                    // Check if it's adjacent to player's cells
                    for (int[] dir : directions) {
                        int nr = r + dir[0], nc = c + dir[1];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                            Cell adjCell = board[nr][nc];
                            if (adjCell.player != null && adjCell.player == player) {
                                score += 0.5; // Bonus for growth potential
                                break;
                            }
                        }
                    }
                } else if (cell.player == player && cell.mass == 1) {
                    // Single orb - vulnerable but potential for growth
                    boolean safe = true;
                    for (int[] dir : directions) {
                        int nr = r + dir[0], nc = c + dir[1];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                            Cell adjCell = board[nr][nc];
                            if (adjCell.player != null && adjCell.player == opponent && adjCell.mass >= 1) {
                                safe = false;
                                break;
                            }
                        }
                    }
                    if (safe) {
                        score += 0.3;
                    }
                }
            }
        }
        
        return score;
    }

}
