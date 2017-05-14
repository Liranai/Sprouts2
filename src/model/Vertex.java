package model;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Vertex {

	private static int vertices = 0;

	private int uniqueID;
	@Setter
	private int degree;
	@Setter
	private boolean original = false;
	private HashMap<Integer, Edge> edges;
	private HashMap<Integer, Plane> planes;

	public Vertex(Plane plane) {
		degree = 0;
		uniqueID = acquireID();
		edges = new HashMap<Integer, Edge>();
		planes = new HashMap<Integer, Plane>();
		planes.put(plane.getUniqueID(), plane);
	}

	public Vertex(int id, int degree, boolean original, HashMap<Integer, Edge> edges, HashMap<Integer, Plane> planes) {
		this.uniqueID = id;
		this.degree = degree;
		this.original = original;
		this.edges = edges;
		this.planes = planes;
	}

	public boolean increaseDegree(int n) {
		if (degree + n <= 3) {
			degree += n;
			return true;
		}
		return false;
	}

	public void addEdge(Edge edge) {
		edges.put(edge.getUniqueID(), edge);
	}

	@Override
	protected Vertex clone() {
		HashMap<Integer, Edge> clonedEdges = new HashMap<Integer, Edge>();
		for (Edge edge : edges.values()) {
			clonedEdges.put(edge.getUniqueID(), edge);
		}
		HashMap<Integer, Plane> clonedPlanes = new HashMap<Integer, Plane>();
		for (Plane plane : planes.values()) {
			clonedPlanes.put(plane.getUniqueID(), plane);
		}
		return new Vertex(uniqueID, degree, original, clonedEdges, clonedPlanes);
	}

	@Override
	public String toString() {
		String str = "ID:" + uniqueID;
		str += " D:" + degree;
		for (Edge edge : edges.values()) {
			str += " E|" + edge.toString();
		}
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		return ((Vertex) obj).getUniqueID() == uniqueID;
	}

	@Override
	public int hashCode() {
		return uniqueID + "Vertex".hashCode();
	}

	public static int getNumberOfVertices() {
		return vertices;
	}

	public static int acquireID() {
		vertices++;
		return vertices;
	}
}
