package model;

import lombok.Getter;

@Getter
public class Edge {

	private static int edges = 0;
	private int uniqueID;
	private Pair<Vertex, Vertex> nodes;

	public Edge(Vertex v1, Vertex v2) {
		uniqueID = acquireID();
		nodes = new Pair<Vertex, Vertex>(v1, v2);
	}

	public Edge(int id, Pair<Vertex, Vertex> nodes) {
		this.uniqueID = id;
		this.nodes = nodes;
	}

	@Override
	protected Edge clone() {
		return new Edge(uniqueID, new Pair<Vertex, Vertex>(nodes.getFirst().clone(), nodes.getSecond().clone()));
	}

	@Override
	public String toString() {
		String str = "";
		str += "ID:" + uniqueID;
		str += " [V1:" + nodes.getFirst().getUniqueID() + " V2:" + nodes.getSecond().getUniqueID() + "] ";
		str += super.toString();
		return str;
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
