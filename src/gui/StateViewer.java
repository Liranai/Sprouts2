package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Plane2;
import model.State2;

public class StateViewer extends JFrame {

	private static final long serialVersionUID = -6408256463523559778L;
	private ArrayList<JPanel> panels;
	private JPanel parent;

	public StateViewer() {
		super("StateViewer Sprouts");
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		panels = new ArrayList<JPanel>();

		parent = new JPanel();
		parent.setLayout(new BoxLayout(parent, BoxLayout.LINE_AXIS));
		parent.setSize(new Dimension((int) (screen.getWidth() * 0.8), (int) (screen.getHeight() * 0.8)));
		add(parent);

		setMinimumSize(new Dimension((int) (screen.getWidth() * 0.8), (int) (screen.getHeight() * 0.8)));
		pack();
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());

	}

	public void drawState(State2 state) {
		// Dimension size = new Dimension((int) ((this.getWidth() - 20) /
		// Math.ceil(Math.sqrt(state.getNum_of_planes()))),
		// (int) ((this.getHeight() - 40) /
		// Math.floor(Math.sqrt(state.getNum_of_planes()))));
		Dimension size = new Dimension(this.getWidth() / 2, this.getHeight() / 2);
		for (Plane2 plane : state.getPlanes().values()) {
			panels.add(new PlanePanel(plane, state, size));
		}

		this.setVisible(true);
		for (JPanel p : panels) {
			parent.add(p);
			p.repaint();
		}
		this.pack();
	}
}
