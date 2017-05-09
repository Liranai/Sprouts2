package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Node;
import model.State;

public class SproutsUI extends JFrame {

	private static final long serialVersionUID = -5849349179976698861L;

	public static final int CIRCLESIZE = 15, BORDERSIZE = 35, OFFSET = 25;

	private JPanel boardPanel;
	private Node root;

	public SproutsUI(State state, Node root) {
		super("Sprouts");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.root = root;

		boardPanel = new SproutsBoardPanel(state);
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
		((SproutsBoardPanel) boardPanel).setState(root.getState().clone());
		super.repaint();
	}
}
