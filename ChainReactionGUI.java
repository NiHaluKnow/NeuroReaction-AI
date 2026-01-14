import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import GaMe.ChainReactionGame;
import GaMe.Cell;
import Ai_Player.AIPlayer;

public class ChainReactionGUI {
    public static final String STATE_FILE = "gamestate.txt";
    public static final String HUMAN_HEADER = "Human Move:";
    public static final String AI_HEADER = "AI Move:";
    public static final int CELL_SIZE = 60;

    // constants
    public static final Color BG_COLOR = new Color(28, 40, 51);
    public static final Color BOARD_COLOR = new Color(44, 62, 80);
    public static final Color GRID_COLOR = new Color(52, 73, 94);
    public static final Color RED_PLAYER = new Color(231, 76, 60);
    public static final Color BLUE_PLAYER = new Color(52, 152, 219);
    public static final Color TEXT_COLOR = new Color(236, 240, 241);
    public static final Color HIGHLIGHT = new Color(241, 196, 15);

    public ChainReactionGame game;
    public JFrame frame;
    public JPanel boardPanel;
    public JLabel statusLabel;
    public AIPlayer aiPlayer;
    public boolean VsHum;
    public boolean vsAI;
    public boolean aiVsAI;
    public Point selectedCell = null;
    public Point lastMove = null;
    public javax.swing.Timer animationTimer;
    public List<Particle> particles = new ArrayList<>();
    public int aiDelay = 2000; // Default AI delay

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main frame for mode selection
            JFrame modeFrame = new JFrame("Chain Reaction - Mode Selection");
            modeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            modeFrame.setSize(600, 500);
            modeFrame.setLayout(new BorderLayout());
            modeFrame.getContentPane().setBackground(new Color(28, 40, 51));

            // Create particle system for background animation
            List<SelectionParticle> particles = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                particles.add(new SelectionParticle(modeFrame.getWidth(), modeFrame.getHeight()));
            }

            // Animated background panel
            JPanel animatedPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;

                    // Draw gradient background
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(28, 40, 51),
                            0, getHeight(), new Color(44, 62, 80));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Draw particles
                    for (SelectionParticle p : particles) {
                        p.draw(g2d);
                    }
                }
            };
            animatedPanel.setOpaque(false);

            // Main content panel
            JPanel contentPanel = new JPanel(new BorderLayout(10, 20));
            contentPanel.setOpaque(false);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            // Animated title
            JLabel title = new JLabel("CHAIN REACTION", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 45));
            title.setForeground(new Color(236, 240, 241));
            title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

            // Add pulsing animation to title
            Timer titleTimer = new Timer(30, e -> {
            float pulse = (float) (0.9f + 0.1f * Math.sin(System.currentTimeMillis() *
            0.003));
            title.setFont(new Font("Arial", Font.BOLD, (int)(50 * pulse)));
            });
            titleTimer.start();

            // Game description with typing animation
            JTextArea description = new JTextArea();
            description.setFont(new Font("Arial", Font.BOLD, 20));
            description.setForeground(new Color(200, 250, 227));
            description.setOpaque(false);
            description.setEditable(false);
            description.setLineWrap(true);
            description.setWrapStyleWord(true);
            description.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));
            // Center the description text horizontally
            description.setAlignmentX(Component.CENTER_ALIGNMENT);
            description.setAlignmentY(Component.CENTER_ALIGNMENT);

            final String fullText = "Welcome to Chain Reaction!\n"
                    + "Choose your mode:\n";
            new Thread(() -> {
                for (int i = 0; i <= fullText.length(); i++) {
                    final String displayText = fullText.substring(0, i);
                    try {
                        SwingUtilities.invokeLater(() -> description.setText(displayText));
                        Thread.sleep(30);
                    } catch (Exception ex) {
                    }
                }
            }).start();

            // Mode buttons with hover animations
            JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
            buttonPanel.setOpaque(false);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 0, 80));

            String[] modes = { "Human vs Human", "Human vs AI", "AI vs AI" };
            Color[] colors = { new Color(231, 76, 60), new Color(52, 152, 219), new Color(241, 196, 15) };

            for (int i = 0; i < modes.length; i++) {
                JButton modeButton = createAnimatedButton(modes[i], colors[i]);
                int finalI = i;
                modeButton.addActionListener(e -> {
                    // titleTimer.stop();
                    modeFrame.dispose();
                    if (finalI == 0) {
                        new ChainReactionGUI(true, false, false);
                    } else if (finalI == 1) {
                        new ChainReactionGUI(false, true, false);
                    } else {
                        new ChainReactionGUI(false, false, true);
                    }
                });
                buttonPanel.add(modeButton);
            }

            // Assembly
            contentPanel.add(title, BorderLayout.NORTH);
            contentPanel.add(description, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

            animatedPanel.add(contentPanel, BorderLayout.CENTER);
            modeFrame.add(animatedPanel, BorderLayout.CENTER);

            // Animation timer for particles
            Timer animationTimer = new Timer(16, e -> {
                for (SelectionParticle p : particles) {
                    p.update();
                }
                animatedPanel.repaint();
            });
            animationTimer.start();

            modeFrame.setLocationRelativeTo(null);
            modeFrame.setVisible(true);
        });
    }

    private static JButton createAnimatedButton(String text, Color baseColor) {
        // Create a custom JButton subclass with a public glow field
        class AnimatedButton extends JButton {
            public float glow = 0f;

            public AnimatedButton(String text) {
                super(text);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Animated button background
                int arc = 20;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Base color with glow effect
                Color glowColor = new Color(
                        baseColor.getRed(),
                        baseColor.getGreen(),
                        baseColor.getBlue(),
                        (int) (80 * glow));

                // Draw glow
                g2d.setColor(glowColor);
                g2d.fillRoundRect(
                        (int) (-10 * glow),
                        (int) (-10 * glow),
                        (int) (getWidth() + 20 * glow),
                        (int) (getHeight() + 20 * glow),
                        arc, arc);

                // Draw button
                g2d.setColor(baseColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                // Draw text
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        }

        AnimatedButton button = new AnimatedButton(text);

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(200, 50));

        // Add hover animation
        button.addMouseListener(new MouseAdapter() {
            private Timer hoverTimer;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverTimer != null)
                    hoverTimer.stop();
                hoverTimer = new Timer(10, evt -> {
                    button.glow = Math.min(1f, button.glow + 0.05f);
                    button.repaint();
                    if (button.glow >= 1f)
                        ((Timer) evt.getSource()).stop();
                });
                hoverTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverTimer != null)
                    hoverTimer.stop();
                hoverTimer = new Timer(10, evt -> {
                    button.glow = Math.max(0f, button.glow - 0.05f);
                    button.repaint();
                    if (button.glow <= 0f)
                        ((Timer) evt.getSource()).stop();
                });
                hoverTimer.start();
            }
        });

        return button;
    }

    static class SelectionParticle {
        float x, y;
        float vx, vy;
        float size;
        Color color;
        float life;
        float maxLife;

        public SelectionParticle(int width, int height) {
            this.x = (float) (Math.random() * width);
            this.y = (float) (Math.random() * height);
            this.vx = (float) (Math.random() * 2 - 1);
            this.vy = (float) (Math.random() * 2 - 1);
            this.size = (float) (Math.random() * 4 + 2);
            this.color = new Color(
                    (int) (Math.random() * 55 + 200),
                    (int) (Math.random() * 55 + 200),
                    (int) (Math.random() * 55 + 200),
                    (int) (Math.random() * 100 + 50));
            this.life = (float) (Math.random() * 100 + 100);
            this.maxLife = this.life;
        }

        public void update() {
            x += vx;
            y += vy;
            life--;

            if (life <= 0 || x < 0 || x > CELL_SIZE * 9 || y < 0 || y > CELL_SIZE * 6) {
                // Reset particle
                x = (float) (Math.random() * CELL_SIZE * 9);
                y = 0;
                vx = (float) (Math.random() * 2 - 1);
                vy = (float) (Math.random() * 1 + 1);
                life = (float) (Math.random() * 100 + 100);
                maxLife = life;
            }
        }

        public void draw(Graphics2D g) {
            float alpha = life / maxLife;
            g.setColor(new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    (int) (alpha * color.getAlpha())));
            g.fillOval((int) x, (int) y, (int) size, (int) size);
        }
    }

    public ChainReactionGUI(boolean VsHum, boolean vsAI, boolean aiVsAI) {
        this.VsHum = VsHum;
        this.vsAI = vsAI;
        this.aiVsAI = aiVsAI;
        this.game = new ChainReactionGame(9, 6);

        // set delay for AI vs AI
        if (aiVsAI) {
            String delayInput = JOptionPane.showInputDialog(frame,
                    "Enter delay between moves in milliseconds (0 for no delay):",
                    "2000");
            try {
                this.aiDelay = Integer.parseInt(delayInput);
            } catch (NumberFormatException e) {
                this.aiDelay = 2000; // Default value if input is invalid
            }

            this.aiPlayer = new AIPlayer('B', 3, "orb_count");

            new javax.swing.Timer(aiDelay, e -> {
                if (!game.isGameOver()) {
                    AIPlayer currentAI = game.getCurrentPlayer() == 'R' ? new AIPlayer('R', 1, "killer_move")
                            : aiPlayer;

                    int[] move = currentAI.getMove(game);
                    if (move != null && game.addOrb(move[0], move[1], game.getCurrentPlayer())) {
                        lastMove = new Point(move[0], move[1]);
                        createExplosion(move[0], move[1]);

                        try {
                            saveGameState(game.getCurrentPlayer() == 'R' ? "AI(Red) Move:" : "AI(Blue) Move:");
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(frame, "Failed to save game state");
                        }

                        if (game.isGameOver()) {
                            showWinner();
                            ((javax.swing.Timer) e.getSource()).stop();
                        }
                    }
                }
            }).start();
        } else if (vsAI) {
            this.aiPlayer = new AIPlayer('B', 3, "combined");
        }
        initializeUI();
        initializeStateFile();

        

        
    }

    public void initializeUI() {
        frame = new JFrame("Chain Reaction");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 900); // board width, height
        frame.setLayout(new BorderLayout());

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(BG_COLOR);
        statusLabel = new JLabel("Current turn: Red");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.NORTH);

        // Board panel
        boardPanel = new JPanel(new GridLayout(game.rows, game.cols, 2, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                for (Particle p : particles) {
                    p.draw(g2d);
                }
            }
        };
        boardPanel.setBackground(GRID_COLOR);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create cell buttons
        for (int i = 0; i < game.rows; i++) {
            for (int j = 0; j < game.cols; j++) {
                JButton btn = createCellButton(i, j);
                boardPanel.add(btn);
            }
        }

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // Animation timer
        this.animationTimer = new javax.swing.Timer(16, e -> {
            updateAnimations();
            boardPanel.repaint();
        });
        this.animationTimer.start();
    }

    public JButton createCellButton(int row, int col) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Draw cell background
                g2d.setColor(BOARD_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw orb if present
                Cell cell = game.board[row][col];
                if (cell.mass > 0) {
                    // Draw glow effect
                    for (int i = 3; i > 0; i--) {
                        int alpha = 50 - i * 15;
                        Color glowColor = new Color(
                                cell.player == 'R' ? RED_PLAYER.getRed() : BLUE_PLAYER.getRed(),
                                cell.player == 'R' ? RED_PLAYER.getGreen() : BLUE_PLAYER.getGreen(),
                                cell.player == 'R' ? RED_PLAYER.getBlue() : BLUE_PLAYER.getBlue(),
                                alpha);
                        g2d.setColor(glowColor);
                        int radius = (int) ((CELL_SIZE / 2 - 5) + i * 2);
                        g2d.fillOval(getWidth() / 2 - radius, getHeight() / 2 - radius,
                                radius * 2, radius * 2);
                    }

                    // Draw orb
                    g2d.setColor(cell.player == 'R' ? RED_PLAYER : BLUE_PLAYER);
                    int radius = (int) (CELL_SIZE / 2 - 5);
                    g2d.fillOval(getWidth() / 2 - radius, getHeight() / 2 - radius,
                            radius * 2, radius * 2);

                    // Draw mass number
                    g2d.setColor(TEXT_COLOR);
                    Font font = new Font("Arial", Font.BOLD, 16);
                    g2d.setFont(font);
                    String text = String.valueOf(cell.mass);
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = getWidth() / 2 - fm.stringWidth(text) / 2;
                    int y = getHeight() / 2 - fm.getHeight() / 2 + fm.getAscent();
                    g2d.drawString(text, x, y);
                }

                // Draw selection highlight
                if (selectedCell != null && selectedCell.x == row && selectedCell.y == col) {
                    g2d.setColor(HIGHLIGHT);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
                }

                // last move indicator
                if (lastMove != null && lastMove.x == row && lastMove.y == col) {
                    g2d.setColor(HIGHLIGHT);
                    g2d.setStroke(new BasicStroke(2));
                    int pulse = (int) (5 + 3 * Math.sin(System.currentTimeMillis() * 0.005));
                    g2d.drawOval(getWidth() / 2 - pulse, getHeight() / 2 - pulse,
                            pulse * 2, pulse * 2);
                }
            }
        };

        btn.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);

        btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                selectedCell = new Point(row, col);
                boardPanel.repaint();
            }

            public void mouseReleased(MouseEvent e) {
                if (selectedCell != null && selectedCell.x == row && selectedCell.y == col) {
                    handleMove(row, col);
                }
                selectedCell = null;
                boardPanel.repaint();
            }
        });

        return btn;
    }

    public void handleMove(int row, int col) {
        if (game.isGameOver() || aiVsAI)
            return;

        char currentPlayer = game.getCurrentPlayer();
        if (vsAI && currentPlayer == 'B')
            return;

        Cell cell = game.board[row][col];
        if (cell.player == null || cell.player == currentPlayer) {
            if (game.addOrb(row, col, currentPlayer)) {
                lastMove = new Point(row, col);
                createExplosion(row, col);

                try {
                    saveGameState(currentPlayer == 'R' ? HUMAN_HEADER : HUMAN_HEADER);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to save game state");
                }

                if (game.isGameOver()) {
                    showWinner();
                    return;
                }

                if (vsAI && currentPlayer == 'R') {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("AI is thinking...");
                        new javax.swing.Timer(1500, e -> {
                            makeAIMove();
                            ((javax.swing.Timer) e.getSource()).stop();
                        }).start();
                    });
                }
            }
        }
    }

    public void makeAIMove() {
        if (!game.isGameOver() && game.getCurrentPlayer() == 'B') {
            int[] move = aiPlayer.getMove(game);
            if (move != null && game.addOrb(move[0], move[1], 'B')) {
                lastMove = new Point(move[0], move[1]);
                createExplosion(move[0], move[1]);

                try {
                    saveGameState(AI_HEADER);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to save game state");
                }

                if (game.isGameOver()) {
                    showWinner();
                }
            }
        }
    }

    public void createExplosion(int row, int col) {
        // Validate coordinates
        if (row < 0 || row >= game.rows || col < 0 || col >= game.cols) {
            return;
        }

        // Use current player's color (more reliable than cell's player)
        Color color = game.getCurrentPlayer() == 'R' ? RED_PLAYER : BLUE_PLAYER;

        for (int i = 0; i < 30; i++) {
            particles.add(new Particle(
                    (int) (col * CELL_SIZE + CELL_SIZE / 2 + boardPanel.getX()),
                    (int) (row * CELL_SIZE + CELL_SIZE / 2 + boardPanel.getY()),
                    color));
        }
    }

    public void updateAnimations() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            if (!p.update()) {
                it.remove();
            }
        }
        statusLabel.setText("Current turn: " +
                (game.getCurrentPlayer() == 'R' ? "Red" : "Blue"));
        statusLabel.setForeground(game.getCurrentPlayer() == 'R' ? RED_PLAYER : BLUE_PLAYER);
    }

    public void showWinner() {
        String winner = game.getWinner() == 'R' ? "Red" : "Blue";
        JOptionPane.showMessageDialog(frame, "Game Over! Winner: " + winner);
    }

    public void saveGameState(String header) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STATE_FILE))) {
            writer.println(header);
            for (int i = 0; i < game.rows; i++) {
                List<String> row = new ArrayList<>();
                for (int j = 0; j < game.cols; j++) {
                    Cell cell = game.board[i][j];
                    row.add(cell.mass == 0 ? "0" : cell.mass + "" + cell.player);
                }
                writer.println(String.join(" ", row));
            }
        }
    }

    public void initializeStateFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STATE_FILE))) {
            writer.println("Initial board:");
            for (int i = 0; i < game.rows; i++) {
                writer.println(String.join(" ", Collections.nCopies(game.cols, "0")));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to initialize game state file");
        }
    }

    class Particle {
        float x, y;
        float vx, vy;
        Color color;
        int life;

        public Particle(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.vx = (float) (Math.random() * 4 - 2);
            this.vy = (float) (Math.random() * 4 - 2);
            this.life = (int) (Math.random() * 40 + 20);
        }

        public boolean update() {
            x += vx;
            y += vy;
            life--;
            return life > 0;
        }

        public void draw(Graphics2D g) {
            float alpha = (float) life / 60f;
            if (alpha > 1)
                alpha = 1;
            if (alpha < 0)
                alpha = 0;

            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
            int size = (int) (3 + (life / 20f) * 3);
            g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
        }
    }
}