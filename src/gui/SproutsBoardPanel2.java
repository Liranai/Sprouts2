package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

import lombok.Setter;
import model.Edge2;
import model.State;
import model.State2;
import model.Vertex2;

public class SproutsBoardPanel2 extends JPanel {

	private static final long serialVersionUID = -322845229101829011L;

	@Setter
	private State2 state;
	Random rand = new Random(System.currentTimeMillis());

	public SproutsBoardPanel2(State2 state) {
		this.state = state;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Vector<UIVertex2> vertices = new Vector<UIVertex2>();
		for (Vertex2 v : state.getVertices().values()) {
			UIVertex2 uiv = new UIVertex2(v);
			int x, y;
			if (v.isOriginal()) {
				y = SproutsUI.OFFSET;
				x = (((this.getWidth() - SproutsUI.OFFSET) / State2.SPROUTS) * (((v.getUniqueID() - 1) % State2.SPROUTS))) + SproutsUI.OFFSET;
				// x = v.getUniqueID() * SproutsUI.OFFSET;
			} else {
				x = (((this.getWidth() - SproutsUI.OFFSET) / 10) * (((v.getUniqueID() - 1 - State2.SPROUTS) % 10))) + SproutsUI.OFFSET;
				y = (((this.getHeight() - SproutsUI.OFFSET) / (int) Math.ceil((state.getNumberOfVertices() / 10.0) + 1)) * (int) Math.ceil(((v.getUniqueID() - State.SPROUTS) / 10.0))
						+ SproutsUI.OFFSET) + rand.nextInt(20);
			}
			uiv.setX(x);
			uiv.setY(y);
			vertices.add(uiv);
		}

		g2.setColor(Color.gray);
		g2.fill(getVisibleRect());

		for (UIVertex2 vertex : vertices) {
			g2.setColor(Color.BLACK);
			if (vertex.getVertex().isOriginal())
				g2.setColor(Color.RED);
			g2.fill(new Ellipse2D.Double(vertex.getX(), vertex.getY(), SproutsUI.CIRCLESIZE / 2, SproutsUI.CIRCLESIZE / 2));
			g2.setColor(Color.CYAN);
			g2.setFont(new Font("Calibri", Font.PLAIN, 10));
			g2.drawString("" + vertex.getVertex().getUniqueID(), vertex.getX(), vertex.getY());
		}
		g2.setColor(Color.BLACK);
		for (Edge2 edge : state.getEdges().values()) {
			UIVertex2 v1 = null, v2 = null;
			for (UIVertex2 vertex : vertices) {
				if (edge.getNodes().getFirst().equals(vertex.getVertex().getUniqueID())) {
					v1 = vertex;
					if (v2 != null)
						break;
				}
				if (edge.getNodes().getSecond().equals(vertex.getVertex().getUniqueID())) {
					v2 = vertex;
					if (v1 != null)
						break;
				}
			}
			// TODO: v2.x returns null??
			if (v1 == null || v2 == null) {
				// System.out.println(v1 + " OR " + v2 + " is empty?");
				// System.out.println("E|v1:" +
				// edge.getNodes().getFirst().getUniqueID() + " E|v2:" +
				// edge.getNodes().getSecond().getUniqueID());
			} else
				g2.draw(new Line2D.Double(v1.getX(), v1.getY(), v2.getX(), v2.getY()));
		}

	}
}
