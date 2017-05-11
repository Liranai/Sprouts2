package model;

import lombok.Getter;

@Getter
public class Edge {

	private static int edges = 0;
	private int uniqueID;
	private Plane plane;
	private Pair<Vertex, Vertex> nodes;

	public Edge(Plane plane, Vertex v1, Vertex v2) {
		uniqueID = acquireID();
		nodes = new Pair<Vertex, Vertex>(v1, v2);
	}

	public Edge(int id, Plane plane, Pair<Vertex, Vertex> nodes) {
		this.uniqueID = id;
		this.plane = plane;
		this.nodes = nodes;
	}

	@Override
	protected Edge clone() {
		return new Edge(uniqueID, plane, new Pair<Vertex, Vertex>(nodes.getFirst().clone(), nodes.getSecond().clone()));
	}

	@Override
	public boolean equals(Object obj) {
		return ((Edge) obj).getUniqueID() == uniqueID;
	}

	@Override
	public int hashCode() {
		return uniqueID + "Edge".hashCode();
	}

	public static int acquireID() {
		edges++;
		return edges;
	}
}
