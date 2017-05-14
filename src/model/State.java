package model;

import java.util.HashMap;
import java.util.HashSet;

import lombok.Getter;

@Getter
public class State {

	private HashMap<Integer, Plane> planes;
	public static int SPROUTS = 0;

	public State(int numberOfVertices) {
		SPROUTS = numberOfVertices;
		planes = new HashMap<Integer, Plane>();

		Plane plane = new Plane();
		planes.put(plane.getUniqueID(), plane);

		for (int i = 0; i < numberOfVertices; i++) {
			Vertex vertex = new Vertex(plane);
			vertex.setOriginal(true);
			plane.getVertices().put(vertex.getUniqueID(), vertex);
			Cluster cluster = new Cluster();
			cluster.getVertices().put(vertex.getUniqueID(), vertex);
			cluster.setClusterType();
			plane.getClusters().add(cluster);
		}
	}

	public State(HashMap<Integer, Plane> planes) {
		this.planes = planes;
	}

	/**
	 * Runs through all Planes EVERY TIME to collect all clusters TODO: This can
	 * probably be optimized
	 * 
	 * @return
	 */
	public HashSet<Cluster> getClusters() {
		HashSet<Cluster> allClusters = new HashSet<Cluster>();
		for (Plane plane : planes.values()) {
			allClusters.addAll(plane.getClusters());
		}
		return allClusters;
	}

	/**
	 * Runs through all Planes EVERY TIME to collect all vertices TODO: This can
	 * probably be optimized
	 * 
	 * @return
	 */
	public HashSet<Vertex> getVertices() {
		HashSet<Vertex> allVertices = new HashSet<Vertex>();
		for (Plane plane : planes.values()) {
			allVertices.addAll(plane.getVertices().values());
		}
		return allVertices;
	}

	/**
	 * Runs through all Planes EVERY TIME to collect all edges TODO: This can
	 * probably be optimized
	 * 
	 * @return
	 */
	public HashSet<Edge> getEdges() {
		HashSet<Edge> allEdges = new HashSet<Edge>();
		for (Plane plane : planes.values()) {
			allEdges.addAll(plane.getEdges().values());
		}
		return allEdges;
	}

	@Override
	public State clone() {
		HashMap<Integer, Plane> clonedPlanes = new HashMap<Integer, Plane>();
		for (Plane plane : planes.values()) {
			clonedPlanes.put(plane.getUniqueID(), plane.clone());
		}
		return new State(clonedPlanes);
	}

	public String toString() {
		String str = "";
		for (Vertex vertex : getVertices()) {
			str += "V|" + vertex + " ID:" + vertex.getUniqueID() + " || ";
		}
		str += "\n";
		for (Edge edge : getEdges()) {
			str += "E|v1:" + edge.getNodes().getFirst().getUniqueID() + " E|v2:" + edge.getNodes().getSecond().getUniqueID() + " || ";
		}
		str += "\n";
		for (Integer key : planes.keySet()) {
			str += "P|vs:" + planes.get(key).getVertices();
		}
		return str;
	}
}
