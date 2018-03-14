package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Cluster2 {

	private static int clusters = 0;

	private HashMap<Integer, Vertex2> vertices;
	private HashMap<Integer, Edge2> edges;
	private HashMap<Integer, Integer> structure;

	@Setter
	private String clusterComplexForm = "";
	private String clusterForm = "";
	private ArrayList<String> subClusters;
	private int uniqueID;

	public Cluster2() {
		this.vertices = new HashMap<Integer, Vertex2>();
		this.edges = new HashMap<Integer, Edge2>();
		this.structure = new HashMap<Integer, Integer>();

		uniqueID = acquireID();
	}

	public Cluster2(int id, HashMap<Integer, Vertex2> vertices, HashMap<Integer, Edge2> edges, HashMap<Integer, Integer> structure) {
		this.uniqueID = id;
		this.vertices = vertices;
		this.edges = edges;
		this.structure = structure;
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
			if (state.getVertices().get(v.getUniqueID()).getDegree() == 0) {
				return "0";
			}
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
	 * Returns a boolean whether or not the two given clusters are isomorph
	 * WARNING: make sure the clusters are cloned.
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean checkIsomorphism(Cluster2 c1, Cluster2 c2) {
		// Check number of Vertices and Edges
		if (!(c1.getVertices().size() == c2.getVertices().size() && c1.getEdges().size() == c2.getEdges().size())) {
			return false;
		}

		// Check degree sequence
		int[] degrees = new int[] { 0, 0, 0, 0 };
		for (Vertex2 v1 : c1.getVertices().values()) {
			degrees[v1.getDegree()]++;
		}
		for (Vertex2 v2 : c2.getVertices().values()) {
			degrees[v2.getDegree()]--;
		}
		for (int i : degrees) {
			if (i > 0) {
				// System.out.println("uneven DEGREE sequence");
				return false;
			}
		}

		if (c1.getEdges().size() == 0) {
			return true;
		}

		// Check edge sequence
		HashMap<Pair<Integer, Integer>, Integer> edgeDegrees = new HashMap<Pair<Integer, Integer>, Integer>();
		ArrayList<Pair<Edge2, Pair<Integer, Integer>>> links1 = new ArrayList<Pair<Edge2, Pair<Integer, Integer>>>();
		for (Edge2 e1 : c1.getEdges().values()) {
			Vertex2 v1 = c1.getVertices().get(e1.getNodes().getFirst());
			Vertex2 v2 = c1.getVertices().get(e1.getNodes().getSecond());

			Pair<Integer, Integer> endDegrees = new Pair<Integer, Integer>(Math.min(v1.getDegree(), v2.getDegree()), Math.max(v1.getDegree(), v2.getDegree()));
			if (edgeDegrees.containsKey(endDegrees)) {
				edgeDegrees.put(endDegrees, edgeDegrees.get(endDegrees) + 1);
			} else {
				edgeDegrees.put(endDegrees, 1);
			}
			links1.add(new Pair<Edge2, Pair<Integer, Integer>>(e1, endDegrees));
		}
		// Save least common edge
		Pair<Integer, Integer> selected = null;
		int value = Integer.MAX_VALUE;

		for (Pair<Integer, Integer> key : edgeDegrees.keySet()) {
			if (edgeDegrees.get(key) < value) {
				value = edgeDegrees.get(key);
				selected = key;
			}
		}

		ArrayList<Pair<Edge2, Pair<Integer, Integer>>> links2 = new ArrayList<Pair<Edge2, Pair<Integer, Integer>>>();
		for (Edge2 e2 : c2.getEdges().values()) {
			Vertex2 v1 = c2.getVertices().get(e2.getNodes().getFirst());
			Vertex2 v2 = c2.getVertices().get(e2.getNodes().getSecond());

			Pair<Integer, Integer> endDegrees = new Pair<Integer, Integer>(Math.min(v1.getDegree(), v2.getDegree()), Math.max(v1.getDegree(), v2.getDegree()));
			if (edgeDegrees.containsKey(endDegrees)) {
				int d = edgeDegrees.get(endDegrees);
				if (d <= 0) {
					// System.out.println("uneven unsure");
					return false;
				}
				edgeDegrees.put(endDegrees, d - 1);
			} else {
				// System.out.println("Clusters have different edges");
				return false;
			}
			links2.add(new Pair<Edge2, Pair<Integer, Integer>>(e2, endDegrees));
		}
		for (Integer i : edgeDegrees.values()) {
			if (i > 0) {
				// System.out.println("uneven EDGE sequence");
				return false;
			}
		}

		// Remove base structures/0-degree vertices

		// Remove an edge on both clusters
		ArrayList<Edge2> e1s = new ArrayList<Edge2>();
		ArrayList<Edge2> e2s = new ArrayList<Edge2>();

		Edge2 e1 = null;
		for (Pair<Edge2, Pair<Integer, Integer>> item : links1) {
			if (item.getSecond().equals(selected)) {
				e1 = item.getFirst();
				e1s.add(e1);
			}
		}
		Edge2 e2 = null;
		for (Pair<Edge2, Pair<Integer, Integer>> item : links2) {
			if (item.getSecond().equals(selected)) {
				e2 = item.getFirst();
				e2s.add(e2);
			}
		}

		for (Edge2 e1n : e1s) {
			for (Edge2 e2n : e2s) {
				Cluster2 c1n = c1.clone();
				Cluster2 c2n = c2.clone();
				c1n.getEdges().remove(e1n.getUniqueID());
				c2n.getEdges().remove(e2n.getUniqueID());
				if (checkIsomorphism(c1n, c2n))
					return true;
			}
		}

		// System.out.println("We got there");
		return false;
		// return checkIsomorphism(c1, c2);
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

	public static ArrayList<Object[]> findCycleObj(Vertex2 start, State2 state) {
		if (start.getEdge_ids().isEmpty()) {
			return null;
		}

		Queue<ArrayList<Object[]>> queue = new LinkedList<ArrayList<Object[]>>();
		ArrayList<Object[]> v1 = new ArrayList<Object[]>();
		v1.add(new Object[] { start, null });
		queue.add(v1);

		while (!queue.isEmpty()) {
			ArrayList<Object[]> evalPath = queue.poll();
			Vertex2 evalVertex = (Vertex2) evalPath.get(evalPath.size() - 1)[0];
			if (evalVertex.equals(start) && evalPath.size() > 1) {
				return evalPath;
			} else {
				for (Integer edge_id : evalVertex.getEdge_ids()) {
					ArrayList<Object[]> queuePath = new ArrayList<Object[]>(evalPath);
					Vertex2 tempVertex = null;
					if (state.getEdges().get(edge_id).getNodes().getFirst().equals(evalVertex.getUniqueID())) {
						tempVertex = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getSecond());
					} else {
						tempVertex = state.getVertices().get(state.getEdges().get(edge_id).getNodes().getFirst());
					}
					if (evalPath.size() > 1) {
						if (!queuePath.get(queuePath.size() - 2)[0].equals(tempVertex)) {
							queuePath.add(new Object[] { tempVertex, edge_id });
							queue.add(queuePath);
						}
					} else {
						queuePath.add(new Object[] { tempVertex, edge_id });
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
				if (c.getVertices().containsKey(v1.getUniqueID()) || c.getVertices().containsKey(v2.getUniqueID())) {
					boolean joined = false;
					if (c.getVertices().containsKey(v1.getUniqueID())) {
						for (Cluster2 c2 : clusters) {
							if (c2.equals(c))
								continue;
							if (c2.getVertices().containsKey(v2.getUniqueID())) {
								joinClusters(c, c2);
								clusters.remove(c2);
								// c.setClusterType();
								joined = true;
								break;
							}
						}
					} else if (c.getVertices().containsKey(v2.getUniqueID())) {
						for (Cluster2 c2 : clusters) {
							if (c2.equals(c))
								continue;
							if (c2.getVertices().containsKey(v1.getUniqueID())) {
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
						c.getEdges().put(edge_id, state.getEdges().get(edge_id));
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
			if (!state.getVertices().get(key).getPlane_ids().contains(plane.getUniqueID()))
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
		return new Cluster2(uniqueID, clonedVertices, clonedEdges, clonedStructure);
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
