package me.shadorc.demineur.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

class Frame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Grid grid;

	private Border border;
	private Font font;

	private static JLabel bombsInfo = new JLabel("", JLabel.CENTER);
	private static JLabel timeInfo = new JLabel("", JLabel.CENTER);

	private JButton scoresButton = new JButton("Scores");
	private JButton gameButton = new JButton("Nouvelle Partie");

	private static DecimalFormat df;
	private static float time = 0;

	private static Timer timer = new Timer(100, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			time += 0.1;
			Frame.refresh();
		}
	});

	public static void main(String[] args) {
		// Change the separator "," to "."
		df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "UK"));
		df.applyPattern("0.0");

		Scores.read();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Frame();
			}
		});
	}

	Frame() {

		JFrame frame = new JFrame("D�mineur by Shadorc");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(new Color(213, 221, 232));

		JPanel contenuFenetre = new JPanel(new BorderLayout());
		contenuFenetre.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contenuFenetre.setOpaque(false);

		frame.setContentPane(contenuFenetre);

		JPanel hautFenetre = new JPanel(new GridLayout(2, 0, 0, 5));
		hautFenetre.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
		hautFenetre.setOpaque(false);

		border = BorderFactory.createLineBorder(new Color(0, 100, 255), 3, true);
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/res/ASENINE.TTF")).deriveFont(Font.BOLD, 30);
		} catch (FontFormatException | IOException e1) {
			font = new Font("Serif", Font.BOLD, 30);
		}

		this.config(scoresButton);
		this.config(gameButton);

		hautFenetre.add(scoresButton);
		hautFenetre.add(gameButton);

		contenuFenetre.add(hautFenetre, BorderLayout.NORTH);

		grid = new Grid(9, 9);
		grid.setBorder(BorderFactory.createEmptyBorder(6, 3, 6, 3));
		grid.setOpaque(false);
		contenuFenetre.add(grid, BorderLayout.CENTER);

		JPanel infosPane = new JPanel(new GridLayout(0, 2, 10, 0));
		infosPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
		infosPane.setOpaque(false);

		this.config(bombsInfo, "/res/bombe.png");
		this.config(timeInfo, "/res/time.png");

		infosPane.add(bombsInfo);
		infosPane.add(timeInfo);

		contenuFenetre.add(infosPane, BorderLayout.SOUTH);

		frame.setIconImage(new ImageIcon(this.getClass().getResource("/res/bombe.png")).getImage());
		frame.pack();
		frame.setSize(new Dimension(600, 770));
		frame.setMinimumSize(new Dimension(520, 685));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void config(JButton button) {
		button.setFont(font);
		button.setBorder(border);
		button.setBackground(Color.WHITE);
		button.addActionListener(this);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				((JButton) e.getSource()).setBackground(new Color(220, 220, 220));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				((JButton) e.getSource()).setBackground(Color.WHITE);
			}
		});
	}

	private void config(JLabel label, String path) {
		label.setFont(new Font("ARIAL", Font.PLAIN, 40));
		label.setBorder(border);
		label.setOpaque(true);
		label.setForeground(Color.BLACK);
		label.setBackground(Color.WHITE);
		label.setIcon(new ImageIcon(this.getClass().getResource(path)));
	}

	protected static void start() {
		timer.start();
	}

	protected static void stop(boolean victoire) {
		timer.stop();

		if(victoire) {
			String record = "";
			if(Scores.getScore().size() == 0 || Scores.getScore().get(0) > time) {
				record = "\n! NOUVEAU RECORD !";
			}

			JOptionPane.showConfirmDialog(null, "Partie termin�e. Temps : " + df.format(time) + "s." + record, "Partie finie", JOptionPane.PLAIN_MESSAGE);
			Scores.add(df.format(time));

		} else {
			JOptionPane.showConfirmDialog(null, "Partie termin�e. Vous avez perdu.", "Partie finie", JOptionPane.PLAIN_MESSAGE);
		}
	}

	protected static void reset() {
		timer.stop();
		time = 0;
		Frame.refresh();
	}

	protected static void refresh() {
		bombsInfo.setText(Integer.toString(Grid.foundBombs));
		timeInfo.setText(df.format(time));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();

		if(button.equals(gameButton)) {
			grid.init(false);

		} else if(button.equals(scoresButton)) {
			String text = "Scores :\n";

			ArrayList <Float> scores = Scores.getScore();

			for(int i = 0; i < scores.size() && i < 5; i++) {
				text += "\n" + (i + 1) + " : " + scores.get(i) + "s";
			}

			if(scores.size() > 0) {
				JOptionPane.showMessageDialog(null, text, "Scores", JOptionPane.PLAIN_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Aucun score enregistr�.", "Scores", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
}