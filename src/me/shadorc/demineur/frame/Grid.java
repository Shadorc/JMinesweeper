package me.shadorc.demineur.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

import me.shadorc.demineur.frame.Case.State;

class Grid extends JPanel {

	private static final long serialVersionUID = 1L;

	private Case cases[][];
	private Random rand = new Random();

	protected static int foundBombs;
	private int remainingCompartments;
	private int width, height;

	private boolean bombesPosees;

	Grid(int width, int height) {
		super(new GridLayout(width, height));

		this.width = width;
		this.height = height;

		cases = new Case[width][height];

		this.init(true);

		Case.grid = this;
	}

	protected void init(boolean firstTime) {

		bombesPosees = false;
		foundBombs = 10;
		remainingCompartments = width * height - foundBombs;

		// Positioning buttons in the grid.
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(firstTime) {
					this.add(cases[x][y] = new Case(x, y));
				} else {
					cases[x][y].reset();
				}

				// Background color blue
				cases[x][y].setBackground(new Color(0, 100, 255));
			}
		}

		Frame.reset();
	}

	protected void placeBombs(Case c) {

		int bombes = 0;
		bombesPosees = true;

		// Positioning bombs in the grid.
		while(bombes < foundBombs) {

			Case randCase = cases[rand.nextInt(width)][rand.nextInt(height)];

			// If the case don't already contain a bomb & isn't reveal.
			if(!randCase.isBomb() && randCase != c && !this.getAround(randCase).contains(c)) {
				randCase.setBomb();
				bombes++;

				for(Case around : this.getAround(randCase)) {
					around.increment();
				}
			}
		}
	}

	private ArrayList <Case> getAround(Case c) {

		ArrayList <Case> around = new ArrayList <Case>();

		int x = c.getCoX();
		int y = c.getCoY();

		if(this.inBounds(x + 1, y)) {
			around.add(cases[x + 1][y]);
		}
		if(this.inBounds(x - 1, y)) {
			around.add(cases[x - 1][y]);
		}
		if(this.inBounds(x, y + 1)) {
			around.add(cases[x][y + 1]);
		}
		if(this.inBounds(x, y - 1)) {
			around.add(cases[x][y - 1]);
		}
		if(this.inBounds(x + 1, y + 1)) {
			around.add(cases[x + 1][y + 1]);
		}
		if(this.inBounds(x - 1, y - 1)) {
			around.add(cases[x - 1][y - 1]);
		}
		if(this.inBounds(x + 1, y - 1)) {
			around.add(cases[x + 1][y - 1]);
		}
		if(this.inBounds(x - 1, y + 1)) {
			around.add(cases[x - 1][y + 1]);
		}

		return around;
	}

	private boolean inBounds(int x, int y) {
		return (x >= 0) && (x < 9) && (y >= 0) && (y < 9);
	}

	protected void check(int x, int y) {
		if(this.inBounds(x, y)) {

			Case c = cases[x][y];

			if(c.getState() == State.DEFAULT || c.getState() == State.INTERROGATION) {
				remainingCompartments--;
				c.reveal();

				if(c.getBombsAround() == 0) {
					for(Case ca : this.getAround(c)) {
						this.check(ca.getCoX(), ca.getCoY());
					}
				}
			}
		}
	}

	protected void endGame(boolean win) {

		Frame.stop(win);

		// Deactivate all buttons.
		for(int i = 0; i < width; i++) {
			for(int o = 0; o < height; o++) {
				cases[i][o].disable();
			}
		}
	}

	public Case[][] getGrid() {
		return cases;
	}

	public Dimension getDimension() {
		return new Dimension(width, height);
	}

	public int getRemainingCompartments() {
		return remainingCompartments;
	}

	protected boolean bombsPlaced() {
		return bombesPosees;
	}
}