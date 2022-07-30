package me.shadorc.minesweeper;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scores {

    private final List<Float> scores;
    private final File file;

    public Scores() {
        this.scores = new ArrayList<>();
        this.file = new File("./scores");

        this.init();
    }

    private void init() {
        try {
            file.createNewFile();
        } catch (IOException err) {
            JOptionPane.showMessageDialog(
                    null, "An error occurred while creating save file: " + err.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                scores.add(Float.parseFloat(line));
            }

        } catch (IOException err) {
            JOptionPane.showMessageDialog(
                    null, "Error while reading high-scores: " + err.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < Math.min(5, scores.size()); ++i) {
                out.write(scores.get(i) + "\n");
            }

        } catch (IOException err) {
            JOptionPane.showMessageDialog(
                    null, "An error occurred while saving high-scores: " + err.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void add(String score) {
        scores.add(Float.parseFloat(score));
        Collections.sort(scores);
        this.save();
    }

    public List<Float> getScores() {
        return scores;
    }
}