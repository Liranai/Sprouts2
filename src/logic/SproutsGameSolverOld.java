package logic;

import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;
import model.Cluster;
import model.Edge;
import model.Move;
import model.Node;
import model.Plane;
import model.State;
import model.Vertex;

@Getter
public class SproutsGameSolverOld implements Runnable {

	private Node root;

	public SproutsGameSolverOld(State state) {
		root = new Node(state, null);
	}

	@Override
	public void run() {

		Random rand = new Random(System.currentTimeMillis());

		for (int i = 0; i < 100; i++) {
			try {
				System.out.println(root.getState());
				// System.out.println("INTER-CLUSTER-MOVE:" +
				// SproutsGameSolver.getInterClusterMoves(root.getState()).size());
				for (Plane plane : root.getState().getPlanes().values()) {
					plane.setClusters(Cluster.clusterPlane(plane));
				}
				ArrayList<Move> stuff = getLegalMoves(root.getState());
				// for (Cluster c : root.getState().getClusters()) {
				// System.out.print("C:" + c.getClusterType() + " ");
				// }
				// System.out.println("\nMoves:" + stuff.size());
				// System.out.println("Vertices: " +
				// root.getState().getVertices().size());
				stuff.get(rand.nextInt(stuff.size())).makeMove(root.getState());

				// if (i >= 6)
				// try {
				// Thread.sleep(2500);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} catch (IllegalArgumentException e) {
				for (Vertex v : root.getState().getVertices()) {
					// System.out.println(v.getDegree());
				}
				break;
			}
			// System.out.println("MOVES: " + stuff.size() + " VERTICES: " +
			// root.getState().getVertices().size());
			// Move move = stuff.get(rand.nextInt(stuff.size()));
			// move.makeMove(root.getState());
		}
	}

	public static ArrayList<Move> getLegalMoves(State state) {
		ArrayList<Move> moves = new ArrayList<Move>();
		for (Plane plane : state.getPlanes().values()) {
			ArrayList<Cluster> clusters = Cluster.clusterPlane(plane);

			// for (Cluster cluster : clusters) {
			// cluster.setClusterType();
			// System.out.print("C:" + cluster.getClusterType() + " ");
			// }

			// System.out.println("CLUSTERS: " + clusters.size());
			for (Cluster cluster : clusters) {
				cluster.oldAnalyseCluster();
				System.out.println(cluster.getClusterType() + "\tUS:" + cluster.getClusterForm());
				// ArrayList<Vertex> path = null;
				// for (Vertex v : cluster.getVertices().values()) {
				// path = cluster.findCycle(v);
				// if (path != null)
				// break;
				// }
				// System.out.println("PATH = " + path);
				// if (path != null) {
				// System.out.println("Cycle of length: " + path.size());
				// }
			}

			// System.out.println(plane);
			ArrayList<Vertex> tempVertices = new ArrayList<Vertex>(plane.getVertices().values());
			for (int i = 0; i < tempVertices.size(); i++) {
				Vertex vertex = tempVertices.get(i);
				// System.out.println("v:" + vertex);
				for (int j = i; j < tempVertices.size(); j++) {
					Vertex v2 = tempVertices.get(j);
					// System.out.println("v2:" + v2);
					if ((vertex.equals(v2) && vertex.getDegree() > 1) || (!vertex.equals(v2) && (vertex.getDegree() > 2) || v2.getDegree() > 2))
						continue;
					else {
						moves.add(new Move(vertex, v2, plane, plane));
					}
				}
			}
		}
		return moves;
	}

	public static ArrayList<State> getInterClusterStates(State state) {
		ArrayList<State> childStates = new ArrayList<State>();

		for (Plane plane : state.getPlanes().values()) {
			for (Cluster cluster : plane.getClusters()) {
				cluster.oldAnalyseCluster();
			}
			ArrayList<String> types = plane.getClusterTypes();

			for (int i = 0; i < types.size(); i++) {
				String type = types.get(i);
				ArrayList<Cluster> typeClusters = plane.getClustersOfString(type);
				if (typeClusters.isEmpty()) {
					System.err.println("No clusters found of type " + type);
					continue;
				}
				System.out.println(types);
				Cluster c1 = typeClusters.get(0);
				for (int j = i; j < types.size(); j++) {
					String type2 = types.get(j);
					ArrayList<Cluster> type2Clusters = plane.getClustersOfStringExcluding(type2, c1);
					if (type2Clusters.isEmpty())
						continue;
					Cluster c2 = type2Clusters.get(0);
					for (Vertex v1 : c1.getVertices().values()) {
						if (v1.getDegree() > 2)
							continue;
						for (Vertex v2 : c2.getVertices().values()) {
							if (v2.getDegree() > 2)
								continue;

							State child = state.clone();
							Plane modPlane = child.getPlanes().get(plane.getUniqueID());

							Vertex newVertex = new Vertex(modPlane);
							newVertex.setDegree(2);
							Edge edge1 = new Edge(modPlane.getVertices().get(v1.getUniqueID()), newVertex);
							Edge edge2 = new Edge(modPlane.getVertices().get(v2.getUniqueID()), newVertex);
							modPlane.getEdges().put(edge1.getUniqueID(), edge1);
							modPlane.getEdges().put(edge2.getUniqueID(), edge2);
							newVertex.addEdge(edge1);
							newVertex.addEdge(edge2);

							modPlane.getVertices().put(newVertex.getUniqueID(), newVertex);
							modPlane.getVertices().get(v1.getUniqueID()).addEdge(edge1);
							modPlane.getVertices().get(v1.getUniqueID()).increaseDegree(1);
							modPlane.getVertices().get(v2.getUniqueID()).addEdge(edge2);
							modPlane.getVertices().get(v2.getUniqueID()).increaseDegree(1);
							childStates.add(child);
						}
					}
				}
			}
		}
		return childStates;
	}

	// public static ArrayList<Move> getInterClusterMoves(State state) {
	// ArrayList<Move> moves = new ArrayList<Move>();
	// for (Plane plane : state.getPlanes().values()) {
	//
	// for (Cluster cluster : plane.getClusters()) {
	// cluster.analyseCluster();
	// }
	//
	// for (int i = 0; i < 5; i++) {
	// HashMap<String, Cluster> vc1 = plane.getAllClustersByType(i);
	// if (vc1.isEmpty())
	// continue;
	// else {
	// for (Cluster c1 : vc1.values()) {
	// System.out.print("\nCT1: " + c1.getUniquenessString());
	// for (int j = i; j < 5; j++) {
	// HashMap<String, Cluster> vc2 = plane.getAllClustersByTypeExcluding(j,
	// c1);
	// if (vc2.isEmpty())
	// continue;
	// else {
	// for (Cluster c2 : vc2.values()) {
	// // if
	// // (vc1.containsKey(c2.getUniquenessFactor())
	// // && c2.getUniquenessFactor() != 0) {
	// // continue;
	// // }
	// System.out.print(" CT2: " + c2.getUniquenessString());
	// for (Vertex v1 : c1.getVertices().values()) {
	// if (v1.getDegree() > 2)
	// continue;
	// for (Vertex v2 : c2.getVertices().values()) {
	// if (v2.getDegree() > 2)
	// continue;
	// else {
	// moves.add(new Move(v1, v2, plane, MoveType.InterCluster));
	// }
	// if (j == 0 || j == 1 || j == 3)
	// break;
	// }
	// if (i == 0 || i == 1 || i == 3)
	// break;
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// System.out.print("\n");
	// return moves;
	// }

	private boolean isTerminalState(State state) {
		for (Plane plane : state.getPlanes().values()) {
			int openVertices = 0;
			for (Vertex v : plane.getVertices().values()) {
				if (v.getDegree() < 3)
					openVertices++;
			}
			if (openVertices > 1)
				return false;
		}
		return true;
	}
}
