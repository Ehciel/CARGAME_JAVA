import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class CarRacingGame extends JPanel implements ActionListener, KeyListener {
    private final int SCREEN_WIDTH = 900;
    private final int SCREEN_HEIGHT = 600;
    private final int CAR_WIDTH = 70;
    private final int CAR_HEIGHT = 100;
    private final int ENEMY_CAR_WIDTH = 70;
    private final int ENEMY_CAR_HEIGHT = 100;
    private final Color WHITE = new Color(255, 255, 255);
    private final Color BLACK = new Color(0, 0, 0);
    private final Color GREY = new Color(50, 50, 50);
    private final Color RED = new Color(255, 0, 0);
    private final Color GREEN = new Color(0, 255, 0);
    private final int FPS = 100;
    private final int CAR_SPEED = 20;
    private final int LINE_WIDTH = 10;
    private final int LINE_HEIGHT = 60;
    private final int LINE_GAP = 20;
    private final int NUM_LANES = 4;

    private int playerX = SCREEN_WIDTH / 2 - CAR_WIDTH / 2;
    private int playerY = SCREEN_HEIGHT - CAR_HEIGHT - 20;
    private ArrayList<int[]> enemyCars = new ArrayList<>();
    private int enemySpeed = 8;
    private int score = 0;
    private int highestScore = 0;
    private ArrayList<PlayerRecord> playerHistory = new ArrayList<>();
    private String playerName = "Player";
    private boolean gameOver = false;
    private Timer timer;

    private Image playerCar;
    private Image enemyCar;

    private JPanel cards;
    private JPanel gamePanel;
    private JPanel startPanel;
    private JPanel gameOverPanel;

    public CarRacingGame() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(GREY);
        setFocusable(true);
        addKeyListener(this);

        // Load images
        playerCar = new ImageIcon("C:\\Users\\Ehciel\\CarRacingGame\\images\\ver.png").getImage().getScaledInstance(CAR_WIDTH, CAR_HEIGHT, Image.SCALE_SMOOTH);
        enemyCar = new ImageIcon("C:\\Users\\Ehciel\\CarRacingGame\\images\\ciel.png").getImage().getScaledInstance(ENEMY_CAR_WIDTH, ENEMY_CAR_HEIGHT, Image.SCALE_SMOOTH);

        
        // Setup CardLayout
        cards = new JPanel(new CardLayout());
        gamePanel = this;

        setupStartPanel();
        setupGameOverPanel();

        cards.add(startPanel, "Start");
        cards.add(gamePanel, "Game");
        cards.add(gameOverPanel, "GameOver");

        JFrame frame = new JFrame("Car Racing Game");
        frame.add(cards);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game timer
        timer = new Timer(1000 / FPS, this);

        // Start enemy car addition timer
        new Timer(2000, e -> {
            if (!gameOver) addEnemy();
        }).start();

        showStartScreen();
    }

    private void setupStartPanel() {
        startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            
                Image backgroundImage = new ImageIcon("C:\\Users\\Ehciel\\CarRacingGame\\images\\pic.jpg").getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
         };

        startPanel.setLayout(new BorderLayout());
    
        JLabel startLabel = new JLabel("CAR RACING");
        startLabel.setFont(new Font("Arial", Font.BOLD, 65));
        startLabel.setForeground(RED);
        startLabel.setHorizontalAlignment(SwingConstants.CENTER);
        startLabel.setVerticalAlignment(SwingConstants.CENTER);
        startPanel.add(startLabel, BorderLayout.CENTER);
    

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) (cards.getLayout());
            cl.show(cards, "Game");
            playerName = JOptionPane.showInputDialog("Enter your name:");
            resetGame();
            gameOver = false;
            requestFocusInWindow();
            timer.start();
        });
        startPanel.add(startButton, BorderLayout.SOUTH);
    }

    private void setupGameOverPanel() {
        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        gameOverPanel.setBackground(Color.GRAY);
        gameOverPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the panel itself
    
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 55));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
        gameOverPanel.add(gameOverLabel);
    
        JLabel scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
        gameOverPanel.add(scoreLabel);
    
        JButton restartButton = new JButton("Restart");
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        restartButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) (cards.getLayout());
            cl.show(cards, "Game");
            playerName = JOptionPane.showInputDialog("Enter your name:");
            resetGame();
            gameOver = false;
            requestFocusInWindow();
            timer.start();
        });
        gameOverPanel.add(restartButton);
    
        JButton quitButton = new JButton("Quit");
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        quitButton.addActionListener(e -> System.exit(0));
        gameOverPanel.add(quitButton);
    
        gameOverPanel.add(Box.createVerticalGlue());
    }
    
    private void addEnemy() {
        Random rand = new Random();
        int x = rand.nextInt(SCREEN_WIDTH - ENEMY_CAR_WIDTH);
        int y = -ENEMY_CAR_HEIGHT;
        enemyCars.add(new int[]{x, y});
    }

    private void moveEnemies() {
        for (int i = 0; i < enemyCars.size(); i++) {
            int[] car = enemyCars.get(i);
            car[1] += enemySpeed;
            if (car[1] > SCREEN_HEIGHT) {
                enemyCars.remove(i);
                score += 10;
            }
        }
    }

    private void drawEnemies(Graphics g) {
        for (int[] car : enemyCars) {
            g.drawImage(enemyCar, car[0], car[1], this);
        }
    }

    private void drawPlayer(Graphics g) {
        g.drawImage(playerCar, playerX, playerY, this);
    }

    private void drawScore(Graphics g) {
        g.setColor(WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("Player: " + playerName, 10, 60);
        g.drawString("Highest Score: " + highestScore, 10, 90);
    }

    private boolean checkCollision() {
        Rectangle playerRect = new Rectangle(playerX, playerY, CAR_WIDTH, CAR_HEIGHT);
        for (int[] car : enemyCars) {
            Rectangle enemyRect = new Rectangle(car[0], car[1], ENEMY_CAR_WIDTH, ENEMY_CAR_HEIGHT);
            if (playerRect.intersects(enemyRect)) {
                return true;
            }
        }
        return false;
    }

    private void increaseDifficulty() {
        enemySpeed += 0.01;
    }

    private void drawLanes(Graphics g) {
        g.setColor(WHITE);
        for (int i = 1; i < NUM_LANES; i++) {
            int laneX = SCREEN_WIDTH / NUM_LANES * i - LINE_WIDTH / 2;
            for (int y = 0; y < SCREEN_HEIGHT; y += LINE_HEIGHT + LINE_GAP) {
                g.fillRect(laneX, y, LINE_WIDTH, LINE_HEIGHT);
            }
        }
        g.fillRect(0, 0, LINE_WIDTH, SCREEN_HEIGHT);
        g.fillRect(SCREEN_WIDTH - LINE_WIDTH, 0, LINE_WIDTH, SCREEN_HEIGHT);
    }

    private void gameOverScreen() {
        if (score > highestScore) highestScore = score;

        JLabel scoreLabel = (JLabel) gameOverPanel.getComponent(1);
        scoreLabel.setText("Final Score: " + score + "   Highest Score: " + highestScore + "   Player: " + playerName);

        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, "GameOver");
    }

    private void resetGame() {
        playerX = SCREEN_WIDTH / 2 - CAR_WIDTH / 2;
        playerY = SCREEN_HEIGHT - CAR_HEIGHT - 20;
        enemyCars.clear();
        enemySpeed = 8;
        score = 0;
        gameOver = false;
    }

    private void showStartScreen() {
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, "Start");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            moveEnemies();
            if (checkCollision()) {
                playerHistory.add(new PlayerRecord(playerName, score));
                gameOver = true;
                timer.stop();
                gameOverScreen();
            }
            increaseDifficulty();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameOver) {
            drawLanes(g);
            drawEnemies(g);
            drawPlayer(g);
            drawScore(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= CAR_SPEED;
        }
        if (key == KeyEvent.VK_RIGHT && playerX < SCREEN_WIDTH - CAR_WIDTH) {
            playerX += CAR_SPEED;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarRacingGame());
    }

    private class PlayerRecord {
        String name;
        int score;

        public PlayerRecord(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}