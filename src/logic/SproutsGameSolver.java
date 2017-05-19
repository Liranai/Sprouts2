package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gui.SproutsUI;
import lombok.Getter;
import lombok.Setter;
import model.Cluster;
import model.Edge;
import model.Node;
import model.Plane;
import model.State;
import model.Vertex;

@Getter
public class SproutsGameSolver {

	private Node root;
	@Setter
	private SproutsUI ui;

	public SproutsGameSolver(State state) {
		root = new Node(state, null);
	}

	public void run() throws InterruptedException {
		Random rand = new Random(System.currentTimeMillis());

		ArrayList<State> childStates = findChildStates(root);
		while (childStates.size() > 0) {
			root.setState(childStates.get(rand.nextInt(childStates.size() - 1)));

			System.out.println(root.getState());
			Thread.sleep(2000);
			ui.repaint();
			childStates = findChildStates(root);
		}
	}

	public static ArrayList<State> findChildStates(Node node) {
		ArrayList<State> children = new ArrayList<State>();
		State currentState = node.getState();

		for (Plane plane : currentState.getPlanes().values()) {
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

	public static ArrayList<State> findLegalNooseIntraClusterChildren(State state) {
		ArrayList<State> children = new ArrayList<State>();

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

	public static ArrayList<State> findLegalInterClusterChildren(State state) {
		ArrayList<State> children = new ArrayList<State>();

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

	public static State modifyStateInPlane(State state, Plane plane, Vertex v1, Vertex v2) {
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
	}
}
