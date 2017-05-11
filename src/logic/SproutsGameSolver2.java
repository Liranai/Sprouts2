package logic;

import java.util.ArrayList;
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
public class SproutsGameSolver2 {

	private Node root;
	@Setter
	private SproutsUI ui;

	public SproutsGameSolver2(State state) {
		root = new Node(state, null);
	}

	public void run() throws InterruptedException {
		Random rand = new Random(System.currentTimeMillis());

		ArrayList<State> childStates = findChildStates(root);
		while (childStates.size() > 0) {
			System.out.println(childStates.size());
			root.setState(childStates.get(0));

			System.out.println(root.getState());
			Thread.sleep(1000);
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
				cluster.analyseCluster();
				System.out.println(cluster.getClusterType() + "\tUS:" + cluster.getUniquenessString());
			}
		}

		children.addAll(findLegalInterClusterChildren(currentState));

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

	public static State modifyStateInPlane(State state, Plane plane, Vertex v1, Vertex v2) {
		State clonedState = state.clone();
		Plane modPlane = clonedState.getPlanes().get(plane.getUniqueID());
		Vertex clonedV1 = modPlane.getVertices().get(v1.getUniqueID());
		Vertex clonedV2 = modPlane.getVertices().get(v2.getUniqueID());
		Vertex extraVertex = new Vertex(modPlane);
		Edge edge1 = new Edge(modPlane, clonedV1, extraVertex);
		Edge edge2 = new Edge(modPlane, clonedV2, extraVertex);

		modPlane.getVertices().put(extraVertex.getUniqueID(), extraVertex);
		modPlane.getEdges().put(edge1.getUniqueID(), edge1);
		modPlane.getEdges().put(edge2.getUniqueID(), edge2);

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
