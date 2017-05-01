package gui;

import java.awt.Point;

import lombok.Getter;
import lombok.Setter;
import model.Vertex;

@Getter
public class UIVertex {

	private Vertex vertex;
	@Setter
	private int x, y;

	public UIVertex(int x, int y, Vertex vertex) {
		this.x = x;
		this.y = y;
		this.vertex = vertex;
	}

	public UIVertex(Point p, Vertex vertex) {
		this.x = p.x;
		this.y = p.y;
		this.vertex = vertex;
	}

	public UIVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	@Override
	public boolean equals(Object obj) {
		return ((UIVertex) obj).getVertex().equals(vertex);
	}

}
