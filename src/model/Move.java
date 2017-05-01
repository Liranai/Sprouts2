package model;

import lombok.Getter;

@Getter
public class Move {

	public enum MoveType {
		InterCluster, SingleBoundaryIntraCluster, DoubleBoundaryIntraCluster
	};

	private Pair<Vertex, Vertex> nodes;
	private Plane plane;

	private Pair<Edge, Edge> edge;
	private Vertex extraVertex;

	private MoveType type;

	public Move(Pair<Vertex, Vertex> nodes, Plane plane) {
		this.nodes = nodes;
		this.plane = plane;
	}

	public Move(Vertex v1, Vertex v2, Plane plane) {
		nodes = new Pair<Vertex, Vertex>(v1, v2);
		this.plane = plane;
	}

	public Move(Pair<Vertex, Vertex> nodes, Plane plane, MoveType type) {
		this.nodes = nodes;
		this.plane = plane;
	}

	public Move(Vertex v1, Vertex v2, Plane plane, MoveType type) {
		nodes = new Pair<Vertex, Vertex>(v1, v2);
		this.plane = plane;
	}

	public Vertex updateFirstVertex() {
		if (!nodes.getFirst().getPlanes().containsKey(plane.getUniqueID())) {
			nodes.getFirst().getPlanes().put(plane.getUniqueID(), plane);
		}
		return nodes.getFirst();
	}

	public Vertex updateSecondVertex() {
		if (!nodes.getSecond().getPlanes().containsKey(plane.getUniqueID())) {
			nodes.getSecond().getPlanes().put(plane.getUniqueID(), plane);
		}
		return nodes.getSecond();
	}

	public Vertex makeExtraVertex() {
		if (extraVertex == null) {
			extraVertex = new Vertex(plane);
			extraVertex.setDegree(2);
		}
		return extraVertex;
	}

	public void makeMove(State state) {
		plane.getVertices().get(nodes.getFirst().getUniqueID()).increaseDegree(1);
		plane.getVertices().get(nodes.getSecond().getUniqueID()).increaseDegree(1);
		makeExtraVertex();
		plane.getVertices().put(extraVertex.getUniqueID(), extraVertex);
		makeEdges();
		plane.getEdges().put(edge.getFirst().getUniqueID(), edge.getFirst());
		plane.getEdges().put(edge.getSecond().getUniqueID(), edge.getSecond());
	}

	public Pair<Edge, Edge> makeEdges() {
		if (edge == null) {
			edge = new Pair<Edge, Edge>(new Edge(plane, nodes.getFirst(), makeExtraVertex()), new Edge(plane, nodes.getSecond(), makeExtraVertex()));
		}
		return edge;
	}

	public Move clone() {
		return new Move(nodes.getFirst().clone(), nodes.getSecond().clone(), plane);
	}

	@Override
	public boolean equals(Object obj) {
		if (((Move) obj).nodes.getFirst().equals(nodes.getFirst()) && ((Move) obj).nodes.getSecond().equals(nodes.getSecond()))
			return true;
		return false;
	}

	@Override
	public String toString() {
		String str = "V1:" + nodes.getFirst().getUniqueID() + " |V2:" + nodes.getSecond();
		str += " |E1:" + edge.getFirst().getUniqueID() + " |E2:" + edge.getSecond().getUniqueID();
		return str;
	}
}
