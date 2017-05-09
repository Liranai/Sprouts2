package logic;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import lombok.Getter;
import model.Cluster;
import model.Move;
import model.Move.MoveType;
import model.Node;
import model.Plane;
import model.State;
import model.Vertex;

@Getter
public class SproutsGameSolver implements Runnable {

	private Node root;

	public SproutsGameSolver(State state) {
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
				Vector<Move> stuff = getLegalMoves(root.getState());
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

	public static Vector<Move> getLegalMoves(State state) {
		Vector<Move> moves = new Vector<Move>();
		for (Plane plane : state.getPlanes().values()) {
			Vector<Cluster> clusters = Cluster.clusterPlane(plane);

			// for (Cluster cluster : clusters) {
			// cluster.setClusterType();
			// System.out.print("C:" + cluster.getClusterType() + " ");
			// }

			System.out.println("CLUSTERS: " + clusters.size());
			for (Cluster cluster : clusters) {
				cluster.analyseCluster();
				System.out.println(cluster.getClusterType() + " UF:" + cluster.getUniquenessFactor() + "\tUS:" + cluster.getUniquenessString());
			}

			// System.out.println(plane);
			Vector<Vertex> tempVertices = new Vector<Vertex>(plane.getVertices().values());
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

	// public static Vector<Move> getIntraClusterMoves(State state) {
	// Vector<Move> moves = new Vector<Move>();
	// for (Plane plane : state.getPlanes().values()) {
	//
	// for (Cluster cluster: plane.getClusters()) {
	// cluster.analyseCluster();
	// }
	//
	//
	// }
	// }

	public static Vector<Move> getInterClusterMoves(State state) {
		Vector<Move> moves = new Vector<Move>();
		for (Plane plane : state.getPlanes().values()) {

			for (Cluster cluster : plane.getClusters()) {
				cluster.analyseCluster();
			}

			for (int i = 0; i < 5; i++) {
				HashMap<Long, Cluster> vc1 = plane.getAllClustersByType(i);
				if (vc1.isEmpty())
					continue;
				else {
					for (Cluster c1 : vc1.values()) {
						System.out.print("\nCT1: " + c1.getUniquenessFactor());
						for (int j = i; j < 5; j++) {
							HashMap<Long, Cluster> vc2 = plane.getAllClustersByTypeExcluding(j, c1);
							if (vc2.isEmpty())
								continue;
							else {
								for (Cluster c2 : vc2.values()) {
									// if
									// (vc1.containsKey(c2.getUniquenessFactor())
									// && c2.getUniquenessFactor() != 0) {
									// continue;
									// }
									System.out.print(" CT2: " + c2.getUniquenessFactor());
									for (Vertex v1 : c1.getVertices().values()) {
										if (v1.getDegree() > 2)
											continue;
										for (Vertex v2 : c2.getVertices().values()) {
											if (v2.getDegree() > 2)
												continue;
											else {
												moves.add(new Move(v1, v2, plane, MoveType.InterCluster));
											}
											if (j == 0 || j == 1 || j == 3)
												break;
										}
										if (i == 0 || i == 1 || i == 3)
											break;
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.print("\n");
		return moves;
	}

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
