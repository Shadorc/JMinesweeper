package me.shadorc.minesweeper;

import me.shadorc.minesweeper.Cell.State;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StateListener extends MouseAdapter {

    private final Frame frame;
    private final Grid grid;

    public StateListener(Frame frame, Grid grid) {
        this.frame = frame;
        this.grid = grid;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON3) {
            Cell source = (Cell) event.getSource();

            // Set a flag in the cell
            if (source.getState() == State.DEFAULT || source.getState() == State.BOMB) {
                source.removeActionListener(source.getActionListeners()[0]);
                source.setText("");
                source.setIcon(Cell.FLAG_ICON);
                source.setState(State.FLAG);
                this.grid.decreaseRemainingBombs();
            }

            // Set an interrogation point in the cell
            else if (source.getState() == State.FLAG) {
                source.addActionListener();
                source.setText("<html><font size=7 color=white>?");
                source.setIcon(null);
                source.setState(State.INTERROGATION);
                this.grid.increaseRemainingBombs();
            }

            // Remove the interrogation point
            else {
                source.setText("");
                source.setState(State.DEFAULT);
            }

            this.frame.refresh();
        }
    }
}