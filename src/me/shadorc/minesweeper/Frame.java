package me.shadorc.minesweeper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Frame extends JFrame {

    private static final Border DEFAULT_BORDER =
            BorderFactory.createLineBorder(new Color(0, 100, 255), 3, true);
    private static final Font DEFAULT_FONT;
    private static final DecimalFormat NUMBER_FORMAT;

    static {
        Font font;
        try (final InputStream in = Frame.class.getResourceAsStream("/res/ASENINE.TTF")) {
            Objects.requireNonNull(in);
            font = Font.createFont(Font.TRUETYPE_FONT, in)
                    .deriveFont(Font.BOLD, 30);
        } catch (Exception ignored) {
            font = new Font("Serif", Font.BOLD, 30);
        }
        DEFAULT_FONT = font;

        // Change the separator "," to "."
        NUMBER_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "UK"));
        NUMBER_FORMAT.applyPattern("0.0");
    }

    private final Scores scores;
    private final Timer timer;
    private final Grid grid;

    private final JLabel bombCountLabel;
    private final JLabel timerLabel;
    private long lastUpdateMillis;
    private long elapsedMillis;

    public Frame() {
        super("Minesweeper");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(new Color(213, 221, 232));

        this.scores = new Scores();
        this.timer = new Timer(100, event -> {
            this.elapsedMillis += (System.currentTimeMillis() - this.lastUpdateMillis);
            this.lastUpdateMillis = System.currentTimeMillis();
            this.refresh();
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setOpaque(false);

        this.setContentPane(contentPanel);

        JPanel topPanel = new JPanel(new GridLayout(2, 0, 0, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
        topPanel.setOpaque(false);

        JButton scoresButton = new JButton("Scores");
        this.config(scoresButton);
        scoresButton.addActionListener((ActionEvent event) -> {
            StringBuilder text = new StringBuilder("Scores :\n");

            List<Float> scoreList = scores.getScores();
            for (int i = 0; i < Math.min(5, scoreList.size()); ++i) {
                text.append("\n")
                        .append(i + 1)
                        .append(": ")
                        .append(scoreList.get(i))
                        .append("s");
            }

            if (scoreList.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "No high-scores", "High-scores", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        this, text.toString(), "High-scores", JOptionPane.PLAIN_MESSAGE);
            }
        });
        topPanel.add(scoresButton);

        JButton newGameButton = new JButton("New Game");
        this.config(newGameButton);
        topPanel.add(newGameButton);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        this.grid = new Grid(this, 9, 9);
        this.grid.setBorder(BorderFactory.createEmptyBorder(6, 3, 6, 3));
        this.grid.setOpaque(false);
        contentPanel.add(grid, BorderLayout.CENTER);

        newGameButton.addActionListener((ActionEvent event) -> this.grid.init(false));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 0));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
        infoPanel.setOpaque(false);

        this.bombCountLabel = new JLabel("", JLabel.CENTER);
        this.config(this.bombCountLabel, "/res/bomb.png");
        infoPanel.add(this.bombCountLabel);

        this.timerLabel = new JLabel("", JLabel.CENTER);
        this.config(this.timerLabel, "/res/clock.png");
        infoPanel.add(this.timerLabel);

        contentPanel.add(infoPanel, BorderLayout.SOUTH);

        this.setIconImage(Cell.BOMB_ICON.getImage());
        this.pack();
        this.setSize(new Dimension(600, 770));
        this.setMinimumSize(new Dimension(520, 685));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void config(JButton button) {
        button.setFont(DEFAULT_FONT);
        button.setBorder(DEFAULT_BORDER);
        button.setFocusable(false);
        button.setBackground(Color.WHITE);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                final JButton source = (JButton) event.getSource();
                source.setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                final JButton source = (JButton) event.getSource();
                source.setBackground(Color.WHITE);
            }
        });
    }

    private void config(JLabel label, String path) {
        label.setFont(new Font("ARIAL", Font.PLAIN, 40));
        label.setBorder(DEFAULT_BORDER);
        label.setOpaque(true);
        label.setForeground(Color.BLACK);
        label.setBackground(Color.WHITE);
        URL iconUrl = Objects.requireNonNull(this.getClass().getResource(path));
        label.setIcon(new ImageIcon(iconUrl));
    }

    public void start() {
        this.lastUpdateMillis = System.currentTimeMillis();
        this.timer.start();
    }

    public void stop(boolean isVictory) {
        this.timer.stop();

        if (isVictory) {
            StringBuilder message = new StringBuilder();
            if (this.scores.getScores().isEmpty() || this.scores.getScores().get(0) > this.getElapsedSeconds()) {
                message.append("NEW HIGH-SCORE")
                        .append('\n');
            }

            message.append("Finished in ")
                    .append(NUMBER_FORMAT.format(this.getElapsedSeconds()))
                    .append(" seconds.");

            JOptionPane.showConfirmDialog(
                    this, message.toString(), "Game Over", JOptionPane.DEFAULT_OPTION);
            this.scores.add(NUMBER_FORMAT.format(this.getElapsedSeconds()));

        } else {
            JOptionPane.showConfirmDialog(
                    this, "Game Over. You have lost.", "Game Over", JOptionPane.DEFAULT_OPTION);
        }
    }

    protected void reset() {
        this.timer.stop();
        this.elapsedMillis = 0;
        this.refresh();
    }

    public void refresh() {
        this.bombCountLabel.setText(Integer.toString(this.grid.getRemainingBombs()));
        this.timerLabel.setText(NUMBER_FORMAT.format(this.getElapsedSeconds()));
    }

    private double getElapsedSeconds() {
        return this.elapsedMillis / 1000.0;
    }
}