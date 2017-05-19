package logic;

import java.util.ArrayList;
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
			root.setState(childStates.get(rand.nextInt(childStates.size() - 1)));

			System.out.println(root.getState());
			// Thread.sleep(2000);
			ui.repaint();
			childStates = findChildStates(root);
		}
	}

//@formatter:off
	public static ArrayList<State2> findChildStates(Node2 node) {
		ArrayList<State2> children = new ArrayList<State2>();
		State2 currentState = node.getState();
		
		for(Plane2 plane : currentState.getPlanes().values()){
			plane.setClusters(Cluster2.clusterPlane(plane, currentState));
			for(Cluster2 cluster : plane.getClusters()) {
				cluster.analyseCluster(plane, currentState);
				System.out.println("CF:" + cluster.getClusterForm() + "\tRS: " + cluster.searchCluster(plane,currentState));
			}
		}
		
		children.addAll(findLegalInterClusterChildren(currentState));
		children.addAll(findLegalNooseIntraClusterChildren(currentState));
		
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
					for (int i = 0; i < clusters.size(); i++) {
						children.add(nooseModification(state, plane.getUniqueID(), v1.getUniqueID(), clusters.subList(0, i)));
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
		/*
		for (Plane2 plane : currentState.getPlanes().values()) {
			plane.setClusters(Cluster.clusterPlane(plane));
			for (Cluster cluster : plane.getClusters()) {
				cluster.analyseCluster(plane);
				System.out.println("CF:" + cluster.getClusterForm() + "\tRS: " + cluster.searchCluster(plane));
			}
		}

		children.addAll(findLegalInterClusterChildren(currentState));
		children.addAll(findLegalNooseIntraClusterChildren(currentState));

		return children;

	}

	public static ArrayList<State2> findLegalNooseIntraClusterChildren(State2 state) {
		ArrayList<State2> children = new ArrayList<State>();

		for (Plane plane : state.getPlanes().values()) {
			for (Cluster cluster : plane.getClusters()) {
				for (Vertex v1 : cluster.getVertices().values()) {
					if (v1.getDegree() > 1) {
						continue;
					}
					ArrayList<Cluster> clusters = new ArrayList<Cluster>();
					for (Cluster c : plane.getClusters()) {
						if (c.equals(cluster))
							continue;
						clusters.add(c);
					}
					for (int i = 0; i < clusters.size(); i++) {
						children.add(nooseModification(state, plane, v1, clusters.subList(0, i)));
					}
				}
			}
		}

		return children;

	}

	public static ArrayList<State2> findLegalInterClusterChildren(State2 state) {
		ArrayList<State2> children = new ArrayList<State2>();

		for (Plane plane : state.getPlanes().values()) {
			ArrayList<String> clusterTypes = plane.getClusterTypes();
			for (int i = 0; i < clusterTypes.size(); i++) {
				Cluster c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
				for (int j = i; j < clusterTypes.size(); j++) {
					Cluster c2 = null;
					if (clusterTypes.get(i).equals(clusterTypes.get(j))) {
						ArrayList<Cluster> secundairClusters = plane.getClustersOfStringExcluding(clusterTypes.get(j), c1);
						if (!secundairClusters.isEmpty()) {
							c2 = secundairClusters.get(0);
						}
					} else {
						ArrayList<Cluster> secundairClusters = plane.getClustersOfString(clusterTypes.get(j));
						if (!secundairClusters.isEmpty()) {
							c2 = secundairClusters.get(0);
						}
					}
					if (c2 != null) {
						for (Vertex v1 : c1.getVertices().values()) {
							if (v1.getDegree() > 2)
								continue;
							for (Vertex v2 : c2.getVertices().values()) {
								if (v2.getDegree() > 2)
									continue;
								children.add(modifyStateInPlane(state, plane, v1, v2));
							}
						}
					}
				}
			}
		}
		return children;
	}

	public static State nooseModification(State state, Plane plane, Vertex v1, List<Cluster> include) {
		State clonedState = state.clone();
		Plane clonedPlane = clonedState.getPlanes().get(plane.getUniqueID());
		Plane extraPlane = new Plane();
		Vertex clonedV1 = clonedPlane.getVertices().get(v1.getUniqueID());
		Vertex extraVertex = new Vertex(clonedPlane);
		Edge edge1 = new Edge(clonedV1, extraVertex);
		Edge edge2 = new Edge(extraVertex, clonedV1);

		clonedPlane.getVertices().put(extraVertex.getUniqueID(), extraVertex);
		clonedPlane.getEdges().put(edge1.getUniqueID(), edge1);
		clonedPlane.getEdges().put(edge2.getUniqueID(), edge2);
		extraPlane.getVertices().put(extraVertex.getUniqueID(), extraVertex);
		extraPlane.getVertices().put(clonedV1.getUniqueID(), clonedV1);
		extraPlane.getEdges().put(edge1.getUniqueID(), edge1);
		extraPlane.getEdges().put(edge2.getUniqueID(), edge2);

		clonedV1.getPlanes().put(extraPlane.getUniqueID(), extraPlane);
		extraVertex.getPlanes().put(extraPlane.getUniqueID(), extraPlane);

		extraVertex.setDegree(2);
		clonedV1.increaseDegree(2);
		clonedV1.addEdge(edge1);
		clonedV1.addEdge(edge2);
		extraVertex.addEdge(edge1);
		extraVertex.addEdge(edge2);

		clonedState.getPlanes().put(extraPlane.getUniqueID(), extraPlane);

		for (Cluster cluster : include) {
			for (Vertex vertex : cluster.getVertices().values()) {
				extraPlane.getVertices().put(vertex.getUniqueID(), clonedPlane.getVertices().remove(vertex.getUniqueID()));
				extraPlane.getVertices().get(vertex.getUniqueID()).getPlanes().remove(clonedPlane.getUniqueID());
				extraPlane.getVertices().get(vertex.getUniqueID()).getPlanes().put(extraPlane.getUniqueID(), extraPlane);
			}
			for (Edge edge : cluster.getEdges().values()) {
				extraPlane.getEdges().put(edge.getUniqueID(), clonedPlane.getEdges().remove(edge.getUniqueID()));
			}
		}

		for (Plane statePlane : clonedState.getPlanes().values()) {
			if (statePlane.getVertices().containsKey(clonedV1.getUniqueID())) {
				statePlane.getVertices().get(clonedV1.getUniqueID()).increaseDegree(2);
				statePlane.getVertices().get(clonedV1.getUniqueID()).addEdge(edge1);
				statePlane.getVertices().get(clonedV1.getUniqueID()).addEdge(edge2);
				statePlane.getVertices().get(clonedV1.getUniqueID()).getPlanes().put(extraPlane.getUniqueID(), extraPlane);
			}
		}

		return clonedState;
	}

	public static State2 modifyStateInPlane(State2 state, Plane plane, Vertex v1, Vertex v2) {
		State clonedState = state.clone();
		Plane modPlane = clonedState.getPlanes().get(plane.getUniqueID());
		Vertex clonedV1 = modPlane.getVertices().get(v1.getUniqueID());
		Vertex clonedV2 = modPlane.getVertices().get(v2.getUniqueID());
		Vertex extraVertex = new Vertex(modPlane);
		Edge edge1 = new Edge(clonedV1, extraVertex);
		Edge edge2 = new Edge(clonedV2, extraVertex);

		modPlane.getVertices().put(extraVertex.getUniqueID(), extraVertex);
		modPlane.getEdges().put(edge1.getUniqueID(), edge1);
		modPlane.getEdges().put(edge2.getUniqueID(), edge2);

		for (Plane statePlane : clonedState.getPlanes().values()) {
			if (statePlane.getVertices().containsKey(clonedV1.getUniqueID())) {
				statePlane.getVertices().get(clonedV1.getUniqueID()).increaseDegree(1);
				statePlane.getVertices().get(clonedV1.getUniqueID()).addEdge(edge1);
			}
			if (statePlane.getVertices().containsKey(clonedV2.getUniqueID())) {
				statePlane.getVertices().get(clonedV2.getUniqueID()).increaseDegree(1);
				statePlane.getVertices().get(clonedV2.getUniqueID()).addEdge(edge2);
			}
		}

		extraVertex.setDegree(2);
		clonedV1.increaseDegree(1);
		clonedV2.increaseDegree(1);
		clonedV1.addEdge(edge1);
		clonedV2.addEdge(edge2);
		extraVertex.addEdge(edge1);
		extraVertex.addEdge(edge2);
		return clonedState;
	}*/
}
