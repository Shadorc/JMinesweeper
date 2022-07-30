package me.shadorc.minesweeper;

import me.shadorc.minesweeper.Cell.State;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid extends JPanel {

    private final me.shadorc.minesweeper.Frame frame;
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final Random rand;

    private int remainingBombs;
    private int remainingCells;
    private boolean areBombsSet;

    public Grid(Frame frame, int width, int height) {
        super(new GridLayout(width, height));

        this.frame = frame;
        this.width = width;
        this.height = height;

        this.cells = new Cell[width][height];
        this.rand = new Random();

        this.init(true);
    }

    public void init(boolean firstTime /* TODO: this could be guessed */) {
        this.areBombsSet = false;
        this.remainingBombs = 10;
        this.remainingCells = this.width * this.height - this.remainingBombs;

        // Positioning buttons in the grid
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (firstTime) {
                    final Cell cell = new Cell(this.frame, this, x, y);
                    this.cells[x][y] = cell;
                    this.add(cell);
                } else {
                    this.cells[x][y].reset();
                }
            }
        }

        if (!firstTime) {
            this.frame.reset();
        }
    }

    public void setBombs(Cell clickedCell) {
        int bombCount = 0;
        this.areBombsSet = true;

        // Positioning bombs on the grid
        while (bombCount < this.remainingBombs) {

            final Cell randCell = this.cells[this.rand.nextInt(this.width)][this.rand.nextInt(this.height)];

            // If the case does not already contain a bomb and is not revealed
            if (!randCell.isBomb() && randCell != clickedCell) {

                final List<Cell> surroundingCells = this.getSurroundingCells(randCell);
                if (!surroundingCells.contains(clickedCell)) {
                    randCell.setAsBomb();
                    bombCount++;

                    for (final Cell surroundingCell : surroundingCells) {
                        surroundingCell.incrementSurroundingBombCount();
                    }
                }
            }
        }
    }

    private List<Cell> getSurroundingCells(Cell cell) {
        final List<Cell> surroundingCells = new ArrayList<>();

        final int x = cell.getGridX();
        final int y = cell.getGridY();

        if (this.isInBounds(x + 1, y)) {
            surroundingCells.add(this.cells[x + 1][y]);
        }
        if (this.isInBounds(x - 1, y)) {
            surroundingCells.add(this.cells[x - 1][y]);
        }
        if (this.isInBounds(x, y + 1)) {
            surroundingCells.add(this.cells[x][y + 1]);
        }
        if (this.isInBounds(x, y - 1)) {
            surroundingCells.add(this.cells[x][y - 1]);
        }
        if (this.isInBounds(x + 1, y + 1)) {
            surroundingCells.add(this.cells[x + 1][y + 1]);
        }
        if (this.isInBounds(x - 1, y - 1)) {
            surroundingCells.add(this.cells[x - 1][y - 1]);
        }
        if (this.isInBounds(x + 1, y - 1)) {
            surroundingCells.add(this.cells[x + 1][y - 1]);
        }
        if (this.isInBounds(x - 1, y + 1)) {
            surroundingCells.add(this.cells[x - 1][y + 1]);
        }

        return surroundingCells;
    }

    private boolean isInBounds(int x, int y) {
        return (x >= 0) && (x < 9) && (y >= 0) && (y < 9);
    }

    public void check(int x, int y) {
        if (this.isInBounds(x, y)) {

            final Cell cell = this.cells[x][y];

            if (cell.getState() == State.DEFAULT || cell.getState() == State.INTERROGATION) {
                this.remainingCells--;
                cell.reveal();

                if (cell.getSurroundingBombCount() == 0) {
                    for (final Cell surroundingCell : this.getSurroundingCells(cell)) {
                        this.check(surroundingCell.getGridX(), surroundingCell.getGridY());
                    }
                }
            }
        }
    }

    protected void endGame(boolean isVictory) {
        this.frame.stop(isVictory);

        // Deactivate all buttons
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                this.cells[x][y].disable();
            }
        }
    }

    public int getRemainingCells() {
        return this.remainingCells;
    }

    public int getRemainingBombs() {
        return this.remainingBombs;
    }

    public boolean areBombsSet() {
        return this.areBombsSet;
    }

    public void decreaseRemainingBombs() {
        --this.remainingBombs;
    }

    public void increaseRemainingBombs() {
        ++this.remainingBombs;
    }
}