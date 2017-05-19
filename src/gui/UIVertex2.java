package gui;

import java.awt.Point;

import lombok.Getter;
import lombok.Setter;
import model.Vertex2;

@Getter
public class UIVertex2 {

	private Vertex2 vertex;
	@Setter
	private int x, y;

	public UIVertex2(int x, int y, Vertex2 vertex) {
		this.x = x;
		this.y = y;
		this.vertex = vertex;
	}

	public UIVertex2(Point p, Vertex2 vertex) {
		this.x = p.x;
		this.y = p.y;
		this.vertex = vertex;
	}

	public UIVertex2(Vertex2 vertex) {
		this.vertex = vertex;
	}

	@Override
	public boolean equals(Object obj) {
		return ((UIVertex2) obj).getVertex().equals(vertex);
	}

}
