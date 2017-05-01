package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

import lombok.Setter;
import model.Edge;
import model.State;
import model.Vertex;

public class SproutsBoardPanel extends JPanel {

	private static final long serialVersionUID = -322845229101829011L;

	@Setter
	private State state;
	Random rand = new Random(System.currentTimeMillis());

	public SproutsBoardPanel(State state) {
		this.state = state;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Vector<UIVertex> vertices = new Vector<UIVertex>();
		for (Vertex v : state.getVertices()) {
			UIVertex uiv = new UIVertex(v);
			int x, y;
			if (v.isOriginal()) {
				y = SproutsUI.OFFSET;
				x = v.getUniqueID() * SproutsUI.OFFSET;
			} else {
				x = (((this.getWidth() - SproutsUI.OFFSET) / 10) * (((v.getUniqueID() - 1 - State.SPROUTS) % 10))) + SproutsUI.OFFSET;
				y = (((this.getHeight() - SproutsUI.OFFSET) / (int) Math.ceil((Vertex.getNumberOfVertices() / 10.0) + 1)) * (int) Math.ceil(((v.getUniqueID() - State.SPROUTS) / 10.0))
						+ SproutsUI.OFFSET) + rand.nextInt(80);
			}
			uiv.setX(x);
			uiv.setY(y);
			vertices.add(uiv);
		}

		g2.setColor(Color.gray);
		g2.fill(getVisibleRect());

		g2.setColor(Color.BLACK);
		for (UIVertex vertex : vertices) {
			g2.setColor(Color.BLACK);
			if (vertex.getVertex().isOriginal())
				g2.setColor(Color.RED);
			g2.fill(new Ellipse2D.Double(vertex.getX(), vertex.getY(), SproutsUI.CIRCLESIZE / 2, SproutsUI.CIRCLESIZE / 2));
		}
		for (Edge edge : state.getEdges()) {
			UIVertex v1 = null, v2 = null;
			for (UIVertex vertex : vertices) {
				if (edge.getNodes().getFirst().equals(vertex.getVertex())) {
					v1 = vertex;
					if (v2 != null)
						break;
				}
				if (edge.getNodes().getSecond().equals(vertex.getVertex())) {
					v2 = vertex;
					if (v1 != null)
						break;
				}
			}
			g2.draw(new Line2D.Double(v1.getX(), v1.getY(), v2.getX(), v2.getY()));
		}

	}
}
