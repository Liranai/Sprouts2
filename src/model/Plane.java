package model;

import java.util.HashMap;
import java.util.Vector;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Plane {

	private static int planes = 0;

	private HashMap<Integer, Vertex> vertices;
	private HashMap<Integer, Edge> edges;
	@Setter
	private Vector<Cluster> clusters;

	private int uniqueID;

	public Plane() {
		this.vertices = new HashMap<Integer, Vertex>();
		this.edges = new HashMap<Integer, Edge>();
		this.clusters = new Vector<Cluster>();

		uniqueID = acquireID();
	}

	public Plane(int id, HashMap<Integer, Vertex> vertices, HashMap<Integer, Edge> edges, Vector<Cluster> clusters) {
		this.uniqueID = id;
		this.vertices = vertices;
		this.edges = edges;
		this.clusters = clusters;
	}

	// public Cluster getClusterByType(int i) {
	// for (Cluster cluster : clusters) {
	// if (cluster.getClusterType() == i) {
	// return cluster;
	// }
	// }
	// return null;
	// }
	//
	// public Cluster getClusterByTypeExcluding(int i, Cluster exclude) {
	// for (Cluster cluster : clusters) {
	// if (cluster.equals(exclude))
	// continue;
	// if (cluster.getClusterType() == i) {
	// return cluster;
	// }
	// }
	// return null;
	// }

	public HashMap<Long, Cluster> getAllClustersByType(int i) {
		HashMap<Long, Cluster> tempClusters = new HashMap<Long, Cluster>();
		for (Cluster cluster : clusters) {
			if (cluster.getClusterType() == i) {
				tempClusters.putIfAbsent(cluster.getUniquenessFactor(), cluster);
			}
		}
		return tempClusters;
	}

	public HashMap<Long, Cluster> getAllClustersByTypeExcluding(int i, Cluster exclude) {
		HashMap<Long, Cluster> tempClusters = new HashMap<Long, Cluster>();
		for (Cluster cluster : clusters) {
			if (cluster.equals(exclude))
				continue;
			if (cluster.getClusterType() == i) {
				tempClusters.putIfAbsent(cluster.getUniquenessFactor(), cluster);
			}
		}
		return tempClusters;
	}

	@Override
	protected Plane clone() {
		HashMap<Integer, Vertex> clonedVertices = new HashMap<Integer, Vertex>();
		HashMap<Integer, Edge> clonedEdges = new HashMap<Integer, Edge>();
		for (Vertex vertex : vertices.values()) {
			clonedVertices.put(vertex.getUniqueID(), vertex.clone());
		}
		for (Edge edge : edges.values()) {
			clonedEdges.put(edge.getUniqueID(), edge.clone(clonedVertices));
		}
		return new Plane(uniqueID, clonedVertices, clonedEdges, clusters);
	}

	@Override
	public boolean equals(Object obj) {
		return ((Plane) obj).getUniqueID() == uniqueID;
	}

	@Override
	public int hashCode() {
		return uniqueID + "Plane".hashCode();
	}

	public static int acquireID() {
		planes++;
		return planes;
	}
}
