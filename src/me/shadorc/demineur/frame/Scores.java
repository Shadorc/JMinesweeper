package me.shadorc.demineur.frame;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

class Scores {

    private static ArrayList<Float> scores = new ArrayList<Float>();
    private static File file = new File("./scores");

    private static void save() {

        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < 5 && i < scores.size(); i++) {
                out.write(scores.get(i) + "\n");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la sauvegarder, " + e.toString(), "Erreur", JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void read() {

        BufferedReader in = null;

        try {
            file.createNewFile();

            in = new BufferedReader(new FileReader(file));

            String line;
            while ((line = in.readLine()) != null) {
                scores.add(Float.parseFloat(line));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la lecture des scores, " + e.toString(), "Erreur", JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                in.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void add(String score) {
        scores.add(Float.parseFloat(score));
        Collections.sort(scores);
        Scores.save();
    }

    protected static ArrayList<Float> getScore() {
        return scores;
    }
}