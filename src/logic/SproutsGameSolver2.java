package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import gui.SproutsUI2;
import lombok.Getter;
import lombok.Setter;
import model.Cluster2;
import model.Edge2;
import model.Node2;
import model.Plane2;
import model.State2;
import model.Vertex2;

@Getter
public class SproutsGameSolver2 {

	private Node2 root;
	@Setter
	private SproutsUI2 ui;

	public SproutsGameSolver2(State2 state) {
		root = new Node2(state, null);
	}

	public void run() throws InterruptedException {
		Random rand = new Random(System.currentTimeMillis());

		ArrayList<State2> childStates = findChildStates(root);
		while (childStates.size() > 0) {
			root.setState(childStates.get(rand.nextInt(childStates.size())));

			System.out.println(root.getState());
			// Thread.sleep(2000);
			ui.repaint();
			childStates = findChildStates(root);
		}
		System.out.println("FINAL STATE:\n" + root.getState());
		ui.repaint();
	}

	public static ArrayList<State2> findChildStates(Node2 node) {
		ArrayList<State2> children = new ArrayList<State2>();
		State2 currentState = node.getState();

		for (Plane2 plane : currentState.getPlanes().values()) {
			plane.setClusters(Cluster2.clusterPlane(plane, currentState));
			// System.out.println("");
			for (Cluster2 cluster : plane.getClusters()) {
				cluster.analyseCluster(plane, currentState);

				StringBuilder bldr = new StringBuilder();
				for (Vertex2 v : cluster.getVertices().values())
					bldr.append(v.toString(currentState) + " || ");
				for (Edge2 e : cluster.getEdges().values()) {
					bldr.append(e.toString() + " ");
					bldr.append(e.getPlane_ids() + " |:| ");
				}

				System.out.println("Plane: " + plane.getUniqueID() + " | " + plane.getVertex_ids() + " " + bldr.toString());
				// System.out.println("CF:" + cluster.getClusterForm() + "\tRS:
				// " + cluster.searchCluster(plane, currentState));
			}
		}

		children.addAll(findLegalInterClusterChildren(currentState));
		children.addAll(findLegalNooseIntraClusterChildren(currentState));
		children.addAll(findLegalLoopIntraClusterChildren(currentState));

		return children;
	}

	public static ArrayList<State2> findLegalInterClusterChildren(State2 state) {
		ArrayList<State2> children = new ArrayList<State2>();

		for (Plane2 plane : state.getPlanes().values()) {
			ArrayList<String> clusterTypes = plane.getClusterTypes();
			for (int i = 0; i < clusterTypes.size(); i++) {
				Cluster2 c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
				for (int j = i; j < clusterTypes.size(); j++) {
					Cluster2 c2 = null;
					if (clusterTypes.get(i).equals(clusterTypes.get(j))) {
						ArrayList<Cluster2> secundairClusters = plane.getClustersOfStringExcluding(clusterTypes.get(j), c1);
						if (!secundairClusters.isEmpty()) {
							c2 = secundairClusters.get(0);
						}
					} else {
						ArrayList<Cluster2> secundairClusters = plane.getClustersOfString(clusterTypes.get(j));
						if (!secundairClusters.isEmpty()) {
							c2 = secundairClusters.get(0);
						}
					}
					if (c2 != null) {
						for (Vertex2 v1 : c1.getVertices().values()) {
							if (v1.getDegree() > 2)
								continue;
							for (Vertex2 v2 : c2.getVertices().values()) {
								if (v2.getDegree() > 2)
									continue;
								children.add(modifyStateInPlane(state, plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID()));
							}
						}
					}
				}
			}
		}
		return children;
	}

	public static ArrayList<State2> findLegalNooseIntraClusterChildren(State2 state) {
		ArrayList<State2> children = new ArrayList<State2>();

		for (Plane2 plane : state.getPlanes().values()) {
			for (Cluster2 cluster : plane.getClusters()) {
				for (Vertex2 v1 : cluster.getVertices().values()) {
					if (v1.getDegree() > 1) {
						continue;
					}
					ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
					for (Cluster2 c : plane.getClusters()) {
						if (c.equals(cluster))
							continue;
						clusters.add(c);
					}
					for (int i = 0; i < clusters.size() * 2; i++) {
						String binary = Integer.toBinaryString(i);
						ArrayList<Cluster2> clusterSubList = new ArrayList<Cluster2>();
						for (int j = 0; j < binary.length(); j++) {
							if (binary.charAt(j) == '1')
								clusterSubList.add(clusters.get(j));
						}
						children.add(nooseModification(state, plane.getUniqueID(), v1.getUniqueID(), clusterSubList));
					}

				}
			}
		}

		return children;
	}

	public static ArrayList<State2> findLegalLoopIntraClusterChildren(State2 state) {
		ArrayList<State2> children = new ArrayList<State2>();
		for (Plane2 plane : state.getPlanes().values()) {
			for (Cluster2 cluster : plane.getClusters()) {
				for (Vertex2 v1 : cluster.getVertices().values()) {
					if (v1.getDegree() > 2) {
						continue;
					}
					ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
					for (Cluster2 c : plane.getClusters()) {
						if (c.equals(cluster))
							continue;
						clusters.add(c);
					}
					ArrayList<Vertex2> candidateVertices = new ArrayList<Vertex2>();
					for (Vertex2 v : cluster.getVertices().values()) {
						if (v.equals(v1))
							continue;
						if (v.getDegree() > 2)
							continue;
						candidateVertices.add(v);
					}
					for (int i = 0; i < clusters.size() * 2; i++) {
						String binary = Integer.toBinaryString(i);
						ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
						for (int j = 0; j < binary.length(); j++) {
							if (binary.charAt(j) == '1')
								clusterSublist.add(clusters.get(j));
						}
						for (Vertex2 v2 : candidateVertices) {
							children.add(loopModification(state, plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), clusterSublist, cluster.getVertices()));
						}
					}
				}
			}
		}
		return children;
	}

	public static State2 modifyStateInPlane(State2 old_state, int plane_id, int v1_id, int v2_id) {
		State2 clonedState = old_state.clone();
		Vertex2 clonedV1 = clonedState.getVertices().get(v1_id);
		Vertex2 clonedV2 = clonedState.getVertices().get(v2_id);
		Vertex2 extraVertex = clonedState.addVertex(plane_id);
		Edge2 edge1 = clonedState.addEdge(v1_id, extraVertex.getUniqueID(), plane_id);
		Edge2 edge2 = clonedState.addEdge(v2_id, extraVertex.getUniqueID(), plane_id);

		extraVertex.setDegree(2);
		clonedV1.increaseDegree(1);
		clonedV2.increaseDegree(1);
		clonedV1.addEdge(edge1.getUniqueID());
		clonedV2.addEdge(edge2.getUniqueID());
		extraVertex.addEdge(edge1.getUniqueID());
		extraVertex.addEdge(edge2.getUniqueID());
		return clonedState;
	}

	public static State2 nooseModification(State2 old_state, int plane_id, int v1_id, List<Cluster2> include) {
		State2 clonedState = old_state.clone();
		Plane2 clonedPlane = clonedState.getPlanes().get(plane_id);
		Plane2 extraPlane = clonedState.addPlane();
		Vertex2 clonedV1 = clonedState.getVertices().get(v1_id);
		Vertex2 extraVertex = clonedState.addVertex(plane_id, extraPlane.getUniqueID());
		Edge2 edge1 = clonedState.addEdge(v1_id, extraVertex.getUniqueID(), plane_id, extraPlane.getUniqueID());
		Edge2 edge2 = clonedState.addEdge(extraVertex.getUniqueID(), v1_id, plane_id, extraPlane.getUniqueID());

		extraPlane.getVertex_ids().add(clonedV1.getUniqueID());
		clonedV1.getPlane_ids().add(extraPlane.getUniqueID());
		clonedPlane.getVertex_ids().add(clonedV1.getUniqueID());

		extraVertex.setDegree(2);
		clonedV1.increaseDegree(2);
		clonedV1.addEdge(edge1.getUniqueID());
		clonedV1.addEdge(edge2.getUniqueID());
		extraVertex.addEdge(edge1.getUniqueID());
		extraVertex.addEdge(edge2.getUniqueID());

		for (Cluster2 cluster : include) {
			for (Vertex2 vertex : cluster.getVertices().values()) {
				extraPlane.getVertex_ids().add(vertex.getUniqueID());
				clonedPlane.getVertex_ids().remove(vertex.getUniqueID());
				clonedState.getVertices().get(vertex.getUniqueID()).getPlane_ids().add(extraPlane.getUniqueID());
				clonedState.getVertices().get(vertex.getUniqueID()).getPlane_ids().remove(clonedPlane.getUniqueID());
			}
			for (Edge2 edge : cluster.getEdges().values()) {
				extraPlane.getEdge_ids().add(edge.getUniqueID());
				clonedPlane.getEdge_ids().remove(edge.getUniqueID());
				clonedState.getEdges().get(edge.getUniqueID()).getPlane_ids().add(extraPlane.getUniqueID());
				clonedState.getEdges().get(edge.getUniqueID()).getPlane_ids().remove(clonedPlane.getUniqueID());
			}
		}

		return clonedState;
	}

	public static State2 loopModification(State2 old_state, int plane_id, int v1_id, int v2_id, List<Cluster2> include, HashMap<Integer, Vertex2> clusterVertices) {
		State2 clonedState = old_state.clone();
		Plane2 clonedPlane = clonedState.getPlanes().get(plane_id);
		Plane2 extraPlane = clonedState.addPlane();
		Vertex2 clonedV1 = clonedState.getVertices().get(v1_id);
		Vertex2 clonedV2 = clonedState.getVertices().get(v2_id);
		Vertex2 extraVertex = clonedState.addVertex(plane_id, extraPlane.getUniqueID());
		Edge2 edge1 = clonedState.addEdge(v1_id, extraVertex.getUniqueID(), plane_id, extraPlane.getUniqueID());
		Edge2 edge2 = clonedState.addEdge(v2_id, extraVertex.getUniqueID(), plane_id, extraPlane.getUniqueID());

		extraPlane.getVertex_ids().add(clonedV1.getUniqueID());
		extraPlane.getVertex_ids().add(clonedV2.getUniqueID());
		clonedV1.getPlane_ids().add(extraPlane.getUniqueID());
		clonedV2.getPlane_ids().add(extraPlane.getUniqueID());
		clonedPlane.getVertex_ids().add(clonedV1.getUniqueID());
		clonedPlane.getVertex_ids().add(clonedV2.getUniqueID());

		extraVertex.setDegree(2);
		clonedV1.increaseDegree(1);
		clonedV2.increaseDegree(1);
		clonedV1.addEdge(edge1.getUniqueID());
		clonedV2.addEdge(edge2.getUniqueID());
		extraVertex.addEdge(edge1.getUniqueID());
		extraVertex.addEdge(edge2.getUniqueID());

		ArrayList<Vertex2> cycle = Cluster2.findCycle(extraVertex, clonedState);
		for (Vertex2 vertex : clusterVertices.values()) {
			if (cycle.contains(vertex))
				continue;
			extraPlane.getVertex_ids().add(vertex.getUniqueID());
			clonedPlane.getVertex_ids().remove(vertex.getUniqueID());
			clonedState.getVertices().get(vertex.getUniqueID()).getPlane_ids().add(extraPlane.getUniqueID());
			clonedState.getVertices().get(vertex.getUniqueID()).getPlane_ids().remove(clonedPlane.getUniqueID());
		}

		for (Cluster2 cluster : include) {
			for (Vertex2 vertex : cluster.getVertices().values()) {
				extraPlane.getVertex_ids().add(vertex.getUniqueID());
				clonedPlane.getVertex_ids().remove(vertex.getUniqueID());
				clonedState.getVertices().get(vertex.getUniqueID()).getPlane_ids().add(extraPlane.getUniqueID());
				clonedState.getVertices().get(vertex.getUniqueID()).getPlane_ids().remove(clonedPlane.getUniqueID());
			}
			for (Edge2 edge : cluster.getEdges().values()) {
				extraPlane.getEdge_ids().add(edge.getUniqueID());
				clonedPlane.getEdge_ids().remove(edge.getUniqueID());
				clonedState.getEdges().get(edge.getUniqueID()).getPlane_ids().add(extraPlane.getUniqueID());
				clonedState.getEdges().get(edge.getUniqueID()).getPlane_ids().remove(clonedPlane.getUniqueID());
			}
		}

		return clonedState;
	}
}