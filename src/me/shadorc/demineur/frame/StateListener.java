package me.shadorc.demineur.frame;

import me.shadorc.demineur.frame.Case.Image;
import me.shadorc.demineur.frame.Case.State;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class StateListener extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent event) {

        if (event.getButton() == MouseEvent.BUTTON3) {

            Case button = (Case) event.getSource();

            // Set a flag in the compartment.
            if (button.getState() == State.DEFAULT || button.getState() == State.BOMB) {
                button.removeActionListener(button.getActionListeners()[0]);
                button.setText("");
                button.setIcon(Image.FLAG);
                button.setState(State.FLAG);
                Grid.foundBombs--;
            }

            // Set an interrogation point in the compartment.
            else if (button.getState() == State.FLAG) {
                button.addActionListener();
                button.setText("<html><font size=7 color=white>?");
                button.setIcon(null);
                button.setState(State.INTERROGATION);
                Grid.foundBombs++;
            }

            // Remove the interrogation point.
            else {
                button.setText("");
                button.setState(State.DEFAULT);
            }

            Frame.refresh();
        }
    }
}