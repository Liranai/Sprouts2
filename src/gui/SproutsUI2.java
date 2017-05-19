package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Node2;
import model.State2;

public class SproutsUI2 extends JFrame {

	private static final long serialVersionUID = -5849349179976698861L;

	public static final int CIRCLESIZE = 15, BORDERSIZE = 35, OFFSET = 25;

	private JPanel boardPanel;
	private Node2 root;

	public SproutsUI2(State2 state, Node2 root) {
		super("Sprouts");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.root = root;

		boardPanel = new SproutsBoardPanel2(state);
		add(boardPanel);

		setMinimumSize(new Dimension(BORDERSIZE + 25 * CIRCLESIZE + 16, BORDERSIZE + 25 * CIRCLESIZE + 42));
		pack();
		setAlwaysOnTop(true);
		setVisible(true);
		setLocationRelativeTo(null);
	}

	public static Dimension getPanelSize() {
		Dimension dim = new Dimension(WIDTH - 2 * CIRCLESIZE, HEIGHT - 2 * CIRCLESIZE);
		return dim;
	}

	@Override
	public void repaint() {
		((SproutsBoardPanel2) boardPanel).setState(root.getState().clone());
		super.repaint();
	}
}
