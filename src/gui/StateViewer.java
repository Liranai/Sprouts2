package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

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
		parent.setSize(new Dimension((int) (screen.getWidth() * 0.8), (int) (screen.getHeight() * 0.8)));
		add(parent);

		setMinimumSize(new Dimension((int) (screen.getWidth() * 0.8), (int) (screen.getHeight() * 0.8)));
		pack();
		setLocationRelativeTo(null);
	}

	public void drawState(State2 state) {
		GridBagLayout gbl = new GridBagLayout();
		parent.setLayout(gbl);
		int panels_per_x = (int) Math.ceil(Math.sqrt(state.getNum_of_planes()));
		int panels_per_y = (int) Math.ceil(Math.sqrt(state.getNum_of_planes()));

		Dimension size = new Dimension(this.getWidth() / panels_per_x, this.getHeight() / panels_per_y);
		for (Plane2 plane : state.getPlanes().values()) {
			panels.add(new PlanePanel(plane, state, size));
		}
		for (int i = 0; i < panels.size(); i++) {
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.VERTICAL;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.gridx = (i % panels_per_x);
			constraints.gridy = (int) Math.ceil(i / panels_per_y);
			parent.add(panels.get(i), constraints);
		}
		this.setVisible(true);
		this.pack();
	}
}
