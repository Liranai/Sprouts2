package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;

@Getter
public class Cluster {

	/**
	 * Clusters groups of Vertices that are connected;
	 * 
	 * Cluster Types: 0 == Single vertex with degree 0
	 * 
	 * 
	 * TODO: when clusters are joined the counter CLUSTERS isn't updated
	 */

	private static int clusters = 0;

	private HashMap<Integer, Vertex> vertices;
	private HashMap<Integer, Edge> edges;
	private HashMap<Integer, Integer> structure;

	private int clusterType = -1;
	private long uniquenessFactor = -1;
	private String uniquenessString = "";
	private int uniqueID;

	public Cluster() {
		this.vertices = new HashMap<Integer, Vertex>();
		this.edges = new HashMap<Integer, Edge>();
		this.structure = new HashMap<Integer, Integer>();

		uniqueID = acquireID();
	}

	public Cluster(int id, HashMap<Integer, Vertex> vertices, HashMap<Integer, Edge> edges, HashMap<Integer, Integer> structure, int clusterType) {
		this.uniqueID = id;
		this.vertices = vertices;
		this.edges = edges;
		this.structure = structure;
		this.clusterType = clusterType;
	}

	public void analyseCluster() {
		this.structure = new HashMap<Integer, Integer>();
		uniquenessFactor = 0;
		for (Vertex vert : getVertices().values()) {
			if (structure.containsKey(vert.getDegree())) {
				structure.put(vert.getDegree(), structure.get(vert.getDegree()) + 1);
			} else {
				structure.put(vert.getDegree(), 1);
			}
		}
		setClusterType();
		switch (clusterType) {
		case 4:
			if (structure.containsKey(3)) {
				uniquenessFactor += structure.get(3) + 3 * Vertex.getNumberOfVertices();
				uniquenessString += structure.get(3) + "c";
			}
		case 3:
		case 2:
			if (structure.containsKey(2)) {
				uniquenessFactor += structure.get(2) + 2 * Vertex.getNumberOfVertices();
				uniquenessString += structure.get(2) + "b";
			}
		case 1:
			if (structure.containsKey(1)) {
				uniquenessFactor += structure.get(1) + Vertex.getNumberOfVertices();
				uniquenessString += structure.get(1) + "a";
			}
			break;
		default:
			uniquenessString += "0";
		}
	}

	/**
	 * Finds the shortest cycle starting and ending from Vertex Start
	 * 
	 * @param start
	 *            Vertex algorithm should start searching from
	 * @return ArrayLIst<Vertex> of all neighbours in the found cycle, return
	 *         null when no cycle found
	 */
	public static ArrayList<Vertex> findCycle(Vertex start) {
		if (start.getEdges().isEmpty()) {
			return null;
		}

		Queue<ArrayList<Vertex>> queue = new LinkedList<ArrayList<Vertex>>();
		ArrayList<Vertex> v1 = new ArrayList<Vertex>();
		v1.add(start);
		queue.add(v1);

		while (!queue.isEmpty()) {
			ArrayList<Vertex> evalPath = queue.poll();
			Vertex evalVertex = evalPath.get(evalPath.size() - 1);
			if (evalVertex.equals(start) && evalPath.size() > 1) {
				return evalPath;
			} else {
				for (Edge edge : evalVertex.getEdges().values()) {
					ArrayList<Vertex> queuePath = new ArrayList<Vertex>(evalPath);
					Vertex tempVertex = null;
					if (edge.getNodes().getFirst().equals(evalVertex)) {
						tempVertex = edge.getNodes().getSecond();
					} else {
						tempVertex = edge.getNodes().getFirst();
					}
					if (evalPath.size() > 1) {
						if (!queuePath.get(queuePath.size() - 2).equals(tempVertex)) {
							queuePath.add(tempVertex);
							queue.add(queuePath);
						}
					} else {
						queuePath.add(tempVertex);
						queue.add(queuePath);
					}
				}
			}
		}
		return null;
	}

	public void setClusterType() {
		for (Vertex vert : getVertices().values()) {
			if (vert.getDegree() == 0) {
				if (clusterType < 1) {
					clusterType = 0;
				}
			} else if (vert.getDegree() == 1) {
				if (clusterType < 1) {
					clusterType = 1;
				} else if (clusterType < 4) {
					clusterType = 3;
				}
			} else if (vert.getDegree() == 2) {
				if (clusterType < 1) {
					clusterType = 2;
				} else if (clusterType < 4) {
					clusterType = 3;
				}
			} else {
				clusterType = 4;
				break;
			}
		}
	}

	/**
	 * Creates an ArrayList<Cluster> of clusters found on the given plane
	 * 
	 * @param plane
	 *            to be clustered
	 * @return ArrayList<Cluster> of all found clusters
	 */
	public static ArrayList<Cluster> clusterPlane(Plane plane) {
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		ArrayList<Integer> keysSeen = new ArrayList<Integer>();

		for (Edge edge : plane.getEdges().values()) {
			Vertex v1 = edge.getNodes().getFirst();
			Vertex v2 = edge.getNodes().getSecond();

			keysSeen.add(v1.getUniqueID());
			keysSeen.add(v2.getUniqueID());

			boolean clustered = false;
			for (Cluster c : clusters) {
				if (c.getVertices().containsKey(edge.getNodes().getFirst().getUniqueID()) || c.getVertices().containsKey(edge.getNodes().getSecond().getUniqueID())) {
					boolean joined = false;
					if (c.getVertices().containsKey(edge.getNodes().getFirst().getUniqueID())) {
						for (Cluster c2 : clusters) {
							if (c2.equals(c))
								continue;
							if (c2.getVertices().containsKey(edge.getNodes().getSecond().getUniqueID())) {
								joinClusters(c, c2);
								clusters.remove(c2);
								// c.setClusterType();
								joined = true;
								break;
							}
						}
					} else if (c.getVertices().containsKey(edge.getNodes().getSecond().getUniqueID())) {
						for (Cluster c2 : clusters) {
							if (c2.equals(c))
								continue;
							if (c2.getVertices().containsKey(edge.getNodes().getFirst().getUniqueID())) {
								joinClusters(c, c2);
								clusters.remove(c2);
								// c.setClusterType();
								joined = true;
								break;
							}
						}
					}
					if (!joined) {
						c.getVertices().putIfAbsent(v1.getUniqueID(), v1);
						c.getVertices().putIfAbsent(v2.getUniqueID(), v2);
						// c.setClusterType();
						clustered = true;
						break;
					} else {
						clustered = true;
					}
					c.getEdges().put(edge.getUniqueID(), edge);
					if (clustered)
						break;
					// c.setClusterType();
				}
			}
			if (!clustered) {
				Cluster c = new Cluster();
				c.getVertices().putIfAbsent(v1.getUniqueID(), v1);
				c.getVertices().putIfAbsent(v2.getUniqueID(), v2);
				c.getEdges().put(edge.getUniqueID(), edge);
				// c.setClusterType();
				clusters.add(c);
			}
		}

		for (Integer key : plane.getVertices().keySet()) {
			if (keysSeen.contains(key))
				continue;
			Cluster c = new Cluster();
			c.getVertices().put(plane.getVertices().get(key).getUniqueID(), plane.getVertices().get(key));
			// c.setClusterType();
			clusters.add(c);
		}
		return clusters;
	}

	public static void joinClusters(Cluster c1, Cluster c2) {
		c1.getVertices().putAll(c2.getVertices());
		c1.getEdges().putAll(c2.getEdges());
		// c1.setClusterType();
	}

	@Override
	protected Cluster clone() {
		HashMap<Integer, Vertex> clonedVertices = new HashMap<Integer, Vertex>();
		HashMap<Integer, Edge> clonedEdges = new HashMap<Integer, Edge>();
		HashMap<Integer, Integer> clonedStructure = new HashMap<Integer, Integer>();
		for (Vertex vertex : vertices.values()) {
			clonedVertices.put(vertex.getUniqueID(), vertex.clone());
		}
		for (Edge edge : edges.values()) {
			clonedEdges.put(edge.getUniqueID(), edge.clone());
		}
		for (Integer key : structure.keySet()) {
			clonedStructure.put(key, structure.get(key));
		}
		return new Cluster(uniqueID, clonedVertices, clonedEdges, clonedStructure, clusterType);
	}

	@Override
	public boolean equals(Object obj) {
		return ((Cluster) obj).getUniqueID() == uniqueID;
	}

	@Override
	public int hashCode() {
		return uniqueID + "Cluster".hashCode();
	}

	public static int acquireID() {
		clusters++;
		return clusters;
	}
}
