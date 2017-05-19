package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;

@Getter
public class Cluster2 {

	/**
	 * Clusters groups of Vertices that are connected;
	 * 
	 * Cluster Types: 0 == Single vertex with degree 0
	 * 
	 * 
	 * TODO: when clusters are joined the counter CLUSTERS isn't updated
	 */

	private static int clusters = 0;

	private HashMap<Integer, Vertex2> vertices;
	private HashMap<Integer, Edge2> edges;
	private HashMap<Integer, Integer> structure;

	private int clusterType = -1;
	private String clusterForm = "";
	private ArrayList<String> subClusters;
	private int uniqueID;

	public Cluster2() {
		this.vertices = new HashMap<Integer, Vertex2>();
		this.edges = new HashMap<Integer, Edge2>();
		this.structure = new HashMap<Integer, Integer>();

		uniqueID = acquireID();
	}

	public Cluster2(int id, HashMap<Integer, Vertex2> vertices, HashMap<Integer, Edge2> edges, HashMap<Integer, Integer> structure, int clusterType) {
		this.uniqueID = id;
		this.vertices = vertices;
		this.edges = edges;
		this.structure = structure;
		this.clusterType = clusterType;
	}

	public String recursiveDFS(String current, HashSet<Integer> keysSeen, Vertex2 currentVertex, Vertex2 lastVertex, Plane2 plane, State2 state) {
		currentVertex = state.getVertices().get(currentVertex.getUniqueID());
		String str = current;
		if (currentVertex.getDegree() < 1) {
			return "0";
		}
		switch (currentVertex.getDegree()) {
		case 3:
			str += "c";
			break;
		case 2:
			str += "b";
			break;
		case 1:
			str += "a";
			break;
		}
		if (keysSeen.contains(currentVertex.getUniqueID()) || (currentVertex.getDegree() == 1 && !keysSeen.isEmpty())) {
			str += ".";
			return str;
		}
		keysSeen.add(currentVertex.getUniqueID());
		ArrayList<String> strs = new ArrayList<String>();
		for (Integer edge_id : currentVertex.getEdge_ids()) {
			Vertex2 neighbour = null;
			if (state.getEdges().get(edge_id).getNodes().getFirst().equals(currentVertex.getUniqueID())) {
				neighbour = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getSecond());
			} else {
				neighbour = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getFirst());
			}
			if (lastVertex != null && neighbour.equals(lastVertex))
				continue;

			strs.add(recursiveDFS(str, keysSeen, neighbour, currentVertex, plane, state));
		}
		String result = "";
		for (String s : strs) {
			result += s;
		}
		return result;
	}

	public String searchCluster(Plane2 plane, State2 state) {
		String result = "";
		for (Vertex2 v : vertices.values()) {
			if (state.getVertices().get(v.getUniqueID()).getDegree() == 1) {
				result = recursiveDFS("", new HashSet<Integer>(), v, null, plane, state);
				break;
			}
		}
		if (result == "") {
			result = recursiveDFS("", new HashSet<Integer>(), state.getVertices().get(vertices.keySet().iterator().next()), null, plane, state);
		}

		StringBuilder bldr = new StringBuilder();
		ArrayList<String> splits = new ArrayList<String>();
		for (int i = 0; i < result.length(); i++) {
			if (result.charAt(i) != '.')
				bldr.append(result.charAt(i));
			else {
				bldr.append('.');
				splits.add(bldr.toString());
				bldr = new StringBuilder();
			}
		}
		if (bldr.length() > 1)
			splits.add(bldr.toString());

		if (splits.size() > 1)
			Collections.sort(splits);
		result = "";
		for (String str : splits) {
			result += str;
		}
		this.subClusters = splits;

		return result;
	}

	public void analyseCluster(Plane2 plane, State2 state) {
		clusterForm = "";
		Vertex2 start = state.getVertices().get(vertices.keySet().iterator().next());
		if (start.getEdge_ids().isEmpty()) {
			clusterForm = "0";
			return;
		}
		HashSet<Integer> keysSeen = new HashSet<Integer>();
		Queue<Vertex2> queue = new LinkedList<Vertex2>();
		queue.add(start);

		while (!queue.isEmpty()) {
			Vertex2 tempVertex = queue.poll();
			if (keysSeen.contains(tempVertex.getUniqueID()))
				continue;
			keysSeen.add(tempVertex.getUniqueID());
			for (Integer edge_id : tempVertex.getEdge_ids()) {
				if (state.getEdges().get(edge_id).getNodes().getFirst().equals(tempVertex.getUniqueID())) {
					if (plane.getVertex_ids().contains(state.getEdges().get(edge_id).getNodes().getSecond()))
						queue.add(state.getVertices().get(state.getEdges().get(edge_id).getNodes().getSecond()));
				} else if (plane.getVertex_ids().contains(state.getEdges().get(edge_id).getNodes().getFirst()))
					queue.add(state.getVertices().get(state.getEdges().get(edge_id).getNodes().getFirst()));
			}
			switch (tempVertex.getDegree()) {
			case 3:
				clusterForm += "c";
				break;
			case 2:
				clusterForm += "b";
				break;
			case 1:
				clusterForm += "a";
				break;
			}
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
	public static ArrayList<Vertex2> findCycle(Vertex2 start, State2 state) {
		if (start.getEdge_ids().isEmpty()) {
			return null;
		}

		Queue<ArrayList<Vertex2>> queue = new LinkedList<ArrayList<Vertex2>>();
		ArrayList<Vertex2> v1 = new ArrayList<Vertex2>();
		v1.add(start);
		queue.add(v1);

		while (!queue.isEmpty()) {
			ArrayList<Vertex2> evalPath = queue.poll();
			Vertex2 evalVertex = evalPath.get(evalPath.size() - 1);
			if (evalVertex.equals(start) && evalPath.size() > 1) {
				return evalPath;
			} else {
				for (Integer edge_id : evalVertex.getEdge_ids()) {
					ArrayList<Vertex2> queuePath = new ArrayList<Vertex2>(evalPath);
					Vertex2 tempVertex = null;
					if (state.getEdges().get(edge_id).getNodes().getFirst().equals(evalVertex.getUniqueID())) {
						tempVertex = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getSecond());
					} else {
						tempVertex = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getFirst());
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

	/**
	 * Creates an ArrayList<Cluster> of clusters found on the given plane
	 * 
	 * @param plane
	 *            to be clustered
	 * @return ArrayList<Cluster> of all found clusters
	 */
	public static ArrayList<Cluster2> clusterPlane(Plane2 plane, State2 state) {
		ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
		ArrayList<Integer> keysSeen = new ArrayList<Integer>();

		for (Integer edge_id : plane.getEdge_ids()) {
			Vertex2 v1 = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getFirst());
			Vertex2 v2 = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getSecond());

			keysSeen.add(v1.getUniqueID());
			keysSeen.add(v2.getUniqueID());

			boolean clustered = false;
			for (Cluster2 c : clusters) {
				if (c.getVertices().containsKey(state.getEdges().get(edge_id).getNodes().getFirst()) || c.getVertices().containsKey(state.getEdges().get(edge_id).getNodes().getSecond())) {
					boolean joined = false;
					if (c.getVertices().containsKey(state.getEdges().get(edge_id).getNodes().getFirst())) {
						for (Cluster2 c2 : clusters) {
							if (c2.equals(c))
								continue;
							if (c2.getVertices().containsKey(state.getEdges().get(edge_id).getNodes().getSecond())) {
								joinClusters(c, c2);
								clusters.remove(c2);
								// c.setClusterType();
								joined = true;
								break;
							}
						}
					} else if (c.getVertices().containsKey(state.getEdges().get(edge_id).getNodes().getSecond())) {
						for (Cluster2 c2 : clusters) {
							if (c2.equals(c))
								continue;
							if (c2.getVertices().containsKey(state.getEdges().get(edge_id).getNodes().getFirst())) {
								joinClusters(c, c2);
								clusters.remove(c2);
								// c.setClusterType();
								joined = true;
								break;
							}
						}
					}
					if (!joined) {
						c.getVertices().putIfAbsent(v1.getUniqueID(), state.getVertices().get(v1.getUniqueID()));
						c.getVertices().putIfAbsent(v2.getUniqueID(), state.getVertices().get(v2.getUniqueID()));
						// c.setClusterType();
						clustered = true;
						break;
					} else {
						clustered = true;
					}
					c.getEdges().put(edge_id, state.getEdges().get(edge_id));
					if (clustered)
						break;
					// c.setClusterType();
				}
			}
			if (!clustered) {
				Cluster2 c = new Cluster2();
				c.getVertices().putIfAbsent(v1.getUniqueID(), state.getVertices().get(v1.getUniqueID()));
				c.getVertices().putIfAbsent(v2.getUniqueID(), state.getVertices().get(v2.getUniqueID()));
				c.getEdges().put(edge_id, state.getEdges().get(edge_id));
				// c.setClusterType();
				clusters.add(c);
			}
		}

		for (Integer key : state.getVertices().keySet()) {
			if (keysSeen.contains(key))
				continue;
			Cluster2 c = new Cluster2();
			c.getVertices().put(state.getVertices().get(key).getUniqueID(), state.getVertices().get(key));
			// c.setClusterType();
			clusters.add(c);
		}
		return clusters;
	}

	public static void joinClusters(Cluster2 c1, Cluster2 c2) {
		c1.getVertices().putAll(c2.getVertices());
		c1.getEdges().putAll(c2.getEdges());
	}

	@Override
	protected Cluster2 clone() {
		HashMap<Integer, Vertex2> clonedVertices = new HashMap<Integer, Vertex2>();
		HashMap<Integer, Edge2> clonedEdges = new HashMap<Integer, Edge2>();
		HashMap<Integer, Integer> clonedStructure = new HashMap<Integer, Integer>();
		for (Vertex2 vertex : vertices.values()) {
			clonedVertices.put(vertex.getUniqueID(), vertex.clone());
		}
		for (Edge2 edge : edges.values()) {
			clonedEdges.put(edge.getUniqueID(), edge.clone());
		}
		for (Integer key : structure.keySet()) {
			clonedStructure.put(key, structure.get(key));
		}
		return new Cluster2(uniqueID, clonedVertices, clonedEdges, clonedStructure, clusterType);
	}

	@Override
	public boolean equals(Object obj) {
		return ((Cluster2) obj).getUniqueID() == uniqueID;
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
