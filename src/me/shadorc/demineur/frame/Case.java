package me.shadorc.demineur.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Case extends JButton implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static StateListener stateListener = new StateListener();
    protected static Grid grid;

    private boolean bomb = false;
    private int bombsAround = 0;
    private State state = State.DEFAULT;
    private int x, y;

    enum State {
        DEFAULT, REVEAL, FLAG, INTERROGATION, BOMB;
    }

    interface Image {
        ImageIcon BOMBE = new ImageIcon(Case.class.getResource("/res/bombe.png"));
        ImageIcon FLAG = new ImageIcon(Case.class.getResource("/res/drapeau.png"));
        ImageIcon FAIL_FLAG = new ImageIcon(Case.class.getResource("/res/drapeauBarrée.png"));
    }

    Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.setFont(new Font("Arial", Font.BOLD, 35));
        this.addActionListener(this);
        this.addMouseListener(stateListener);
    }

    public int getCoX() {
        return x;
    }

    public int getCoY() {
        return y;
    }

    public int getBombsAround() {
        return bombsAround;
    }

    public State getState() {
        return state;
    }

    protected boolean isBomb() {
        return bomb;
    }

    public void setState(State state) {
        this.state = state;
    }

    protected void setBomb() {
        bomb = true;
        this.setState(State.BOMB);
    }

    protected void addActionListener() {
        this.addActionListener(this);
    }

    private void removeListener() {
        this.removeMouseListener(stateListener);
        this.removeActionListener(this);
    }

    protected void increment() {
        this.bombsAround++;
    }

    private void dispBombsAround() {
        Color color;

        switch (bombsAround) {
            case 0:
                return;
            case 1:
                color = new Color(58, 44, 254);
                break;
            case 2:
                color = new Color(12, 146, 8);
                break;
            case 3:
                color = new Color(146, 8, 8);
                break;
            case 4:
                color = new Color(14, 0, 215);
                break;
            default:
                color = Color.BLACK;
                break;
        }

        this.setForeground(color);
        this.setText(Integer.toString(bombsAround));
    }

    // New game
    protected void reset() {
        state = State.DEFAULT;
        bombsAround = 0;
        bomb = false;

        this.setForeground(Color.WHITE);
        this.setIcon(null);
        this.setText("");

        this.removeListener();

        this.addMouseListener(stateListener);
        this.addActionListener(this);
    }

    // Show the case
    protected void reveal() {
        this.setBackground(Color.WHITE);
        this.setText("");
        this.dispBombsAround();
        this.removeListener();
        this.setState(State.REVEAL);
    }

    // When it's the end of the game.
    @Override
    public void disable() {
        this.removeListener();

        if (this.getState() == State.FLAG && !this.isBomb()) {
            this.setIcon(Image.FAIL_FLAG);
        }

        // Put compartments with bombs in red.
        if (this.isBomb()) {
            if (this.getState() == State.FLAG) {
                this.setBackground(Color.GREEN);
            } else {
                this.setBackground(Color.RED);
            }

            this.setIcon(Image.BOMBE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        Case button = (Case) event.getSource();

        // If bombs weren't placed yet, start the timer and place them.
        if (!grid.bombsPlaced()) {
            grid.placeBombs(this);
            Frame.start();
        }

        // Else, if the compartment is a bomb, end the game.
        if (button.isBomb()) {
            grid.endGame(false);
        } else {
            grid.check(x, y);
        }

        // If there are not empty compartments anymore, end the game.
        if (grid.getRemainingCompartments() == 0) {
            grid.endGame(true);
        }
    }
}
