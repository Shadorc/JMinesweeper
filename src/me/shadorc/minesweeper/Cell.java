package me.shadorc.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class Cell extends JButton implements ActionListener {

    public static final ImageIcon BOMB_ICON = new ImageIcon(
            Objects.requireNonNull(Cell.class.getResource("/res/bomb.png")));
    public static final ImageIcon FLAG_ICON = new ImageIcon(
            Objects.requireNonNull(Cell.class.getResource("/res/flag.png")));
    public static final ImageIcon CROSSED_FLAG = new ImageIcon(
            Objects.requireNonNull(Cell.class.getResource("/res/crossedFlag.png")));

    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(0, 100, 255);

    private final Grid grid;
    private final Frame frame;
    private final int x;
    private final int y;
    private final StateListener stateListener;

    private State state;
    private boolean isBomb;
    private int surroundingBombCount;


    public enum State {
        DEFAULT,
        REVEALED,
        FLAG,
        INTERROGATION,
        BOMB
    }

    public Cell(Frame frame, Grid grid, int x, int y) {
        this.frame = frame;
        this.grid = grid;
        this.x = x;
        this.y = y;
        this.stateListener = new StateListener(frame, grid);

        this.state = State.DEFAULT;
        this.surroundingBombCount = 0;
        this.isBomb = false;

        this.setFont(new Font("Arial", Font.BOLD, 35));
        this.addActionListener(this);
        this.addMouseListener(this.stateListener);
        this.setBackground(DEFAULT_BACKGROUND_COLOR);
        this.setFocusable(false);
    }

    protected void addActionListener() {
        this.addActionListener(this);
    }

    private void removeListeners() {
        this.removeMouseListener(this.stateListener);
        this.removeActionListener(this);
    }

    protected void incrementSurroundingBombCount() {
        this.surroundingBombCount++;
    }

    private void showBombsAround() {
        if (this.surroundingBombCount == 0) {
            return;
        }

        final Color color = switch (this.surroundingBombCount) {
            case 1 -> new Color(58, 44, 254);
            case 2 -> new Color(12, 146, 8);
            case 3 -> new Color(146, 8, 8);
            case 4 -> new Color(14, 0, 215);
            default -> Color.BLACK;
        };

        this.setForeground(color);
        this.setText(Integer.toString(surroundingBombCount));
    }

    public void reset() {
        this.state = State.DEFAULT;
        this.surroundingBombCount = 0;
        this.isBomb = false;

        this.setBackground(DEFAULT_BACKGROUND_COLOR);
        this.setForeground(Color.WHITE);
        this.setIcon(null);
        this.setText("");

        this.removeListeners();

        this.addMouseListener(this.stateListener);
        this.addActionListener(this);
    }

    public void reveal() {
        this.setBackground(Color.WHITE);
        this.setText("");
        this.showBombsAround();
        this.removeListeners();
        this.setState(State.REVEALED);
    }

    // When it's the end of the game.
    @Override
    public void disable() {
        this.removeListeners();

        if (this.getState() == State.FLAG && !this.isBomb()) {
            this.setIcon(CROSSED_FLAG);
        }

        // Set the color of cells with bombs in red
        if (this.isBomb()) {
            if (this.getState() == State.FLAG) {
                this.setBackground(Color.GREEN);
            } else {
                this.setBackground(Color.RED);
            }

            this.setIcon(BOMB_ICON);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Cell source = (Cell) event.getSource();

        // If bombs weren't placed yet, start the timer and place them
        if (!this.grid.areBombsSet()) {
            this.grid.setBombs(this);
            this.frame.start();
        }

        // Else, if the cell is a bomb, end the game
        if (source.isBomb()) {
            this.grid.endGame(false);
        } else {
            this.grid.check(this.x, this.y);
        }

        // If there are no empty compartments anymore, end the game
        if (this.grid.getRemainingCells() == 0) {
            this.grid.endGame(true);
        }
    }

    public int getGridX() {
        return this.x;
    }

    public int getGridY() {
        return this.y;
    }

    public int getSurroundingBombCount() {
        return this.surroundingBombCount;
    }

    public State getState() {
        return this.state;
    }

    protected boolean isBomb() {
        return isBomb;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setAsBomb() {
        this.isBomb = true;
        this.setState(State.BOMB);
    }
}
