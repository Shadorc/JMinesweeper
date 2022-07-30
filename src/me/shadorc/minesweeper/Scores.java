package me.shadorc.minesweeper;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
            this.file.createNewFile();
        } catch (final IOException err) {
            JOptionPane.showMessageDialog(
                    null, "An error occurred while creating save file: " + err.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        try (final BufferedReader in = new BufferedReader(new FileReader(this.file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                this.scores.add(Float.parseFloat(line));
            }

        } catch (final IOException err) {
            JOptionPane.showMessageDialog(
                    null, "Error while reading high-scores: " + err.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        try (final BufferedWriter out = new BufferedWriter(new FileWriter(this.file, StandardCharsets.UTF_8))) {
            for (int i = 0; i < Math.min(5, this.scores.size()); ++i) {
                out.write(this.scores.get(i) + "\n");
            }

        } catch (final IOException err) {
            JOptionPane.showMessageDialog(
                    null, "An error occurred while saving high-scores: " + err.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void add(String score) {
        this.scores.add(Float.parseFloat(score));
        Collections.sort(this.scores);
        this.save();
    }

    public List<Float> getScores() {
        return this.scores;
    }
}