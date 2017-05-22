package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.JPanel;

import model.Edge2;
import model.Plane2;
import model.State2;

public class PlanePanel extends JPanel {

	private static final long serialVersionUID = 7615850243423603120L;
	private static final int OFFSET = 20;
	private static final int CIRCLESIZE = 8;
	private Plane2 plane;
	private State2 state;

	public PlanePanel(Plane2 plane, State2 state, Dimension size) {
		this.plane = plane;
		this.state = state;
		this.setPreferredSize(size);
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		HashMap<Integer, Point> ui_vertices = new HashMap<Integer, Point>();
		int i = 0;
		int vertices_per_x = (int) Math.ceil(Math.sqrt(plane.getVertex_ids().size()));
		int distance_x = (this.getWidth() - 2 * OFFSET) / vertices_per_x;
		int vertices_per_y = (int) Math.ceil(Math.sqrt(plane.getVertex_ids().size()));
		int distance_y = (this.getHeight() - 2 * OFFSET) / vertices_per_y;
		for (Integer vertex_id : plane.getVertex_ids()) {
			int x = (i % vertices_per_x) * distance_x + OFFSET;
			int y = (int) (Math.ceil(i / vertices_per_y)) * distance_y + OFFSET;
			ui_vertices.put(vertex_id, new Point(x, y));
			i++;
		}

		g2.setColor(new Color(255, 51, 153));
		g2.draw(new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight()));
		g2.setColor(Color.GRAY);
		g2.fill(new Rectangle2D.Double(1, 1, this.getWidth() - 1, this.getHeight() - 1));

		g2.setColor(new Color(153, 0, 153));
		g2.setFont(new Font("Calibri", Font.BOLD, 15));
		g2.drawString("Plane: " + plane.getUniqueID(), 5, 15);

		for (Integer key : ui_vertices.keySet()) {
			g2.setColor(Color.BLACK);
			if (key <= State2.SPROUTS)
				g2.setColor(Color.RED);
			g2.fill(new Ellipse2D.Double(ui_vertices.get(key).getX(), ui_vertices.get(key).getY(), CIRCLESIZE, CIRCLESIZE));
			g2.setColor(Color.CYAN);
			g2.setFont(new Font("Calibri", Font.PLAIN, 10));
			g2.drawString("" + key, (int) ui_vertices.get(key).getX(), (int) ui_vertices.get(key).getY());
		}
		g2.setColor(Color.BLACK);
		for (Integer edge_id : plane.getEdge_ids()) {
			Edge2 edge = state.getEdges().get(edge_id);
			g2.draw(new Line2D.Double(ui_vertices.get(edge.getNodes().getFirst()).x, ui_vertices.get(edge.getNodes().getFirst()).y, ui_vertices.get(edge.getNodes().getSecond()).x,
					ui_vertices.get(edge.getNodes().getSecond()).y));
		}
	}
}
