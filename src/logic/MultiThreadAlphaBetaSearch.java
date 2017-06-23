package logic;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import lombok.Getter;
import model.Cluster2;
import model.Node2;
import model.Plane2;
import model.State2;
import model.Vertex2;

@Getter
public class MultiThreadAlphaBetaSearch implements Callable<Integer> {

	private Node2 root;
	private int value;
	private long nodes_explored;

	public MultiThreadAlphaBetaSearch(Node2 root) {
		this.root = root;
		nodes_explored = 1;
	}

	@Override
	public Integer call() throws Exception {
		return alphaBeta(root, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
	}

	public Integer alphaBeta(Node2 node, int depth, int alpha, int beta, boolean min_max) {
		nodes_explored++;
		// TODO: Optional, output
		if (node.isTerminal()) {
			if (min_max)
				return -1;
			return 1;
		} else {
			if (min_max) {
				int value = Integer.MIN_VALUE;
				Node2 child = null;

				searchloop: for (Plane2 plane : node.getState().getPlanes().values()) {
					plane.setClusters(Cluster2.clusterPlane(plane, node.getState()));
					for (Cluster2 cluster : plane.getClusters()) {
						cluster.analyseCluster(plane, node.getState());
						cluster.setClusterComplexForm(cluster.searchCluster(plane, node.getState()));
					}
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
										State2 childState = SproutsGameSolver2.modifyStateInPlane(node.getState(), plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID());
										child = new Node2(childState, node);
										value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
										alpha = Math.max(alpha, value);
										if (beta <= alpha)
											break searchloop;
									}
								}
							}
						}

						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
						for (Vertex2 v1 : c1.getVertices().values()) {
							if (v1.getDegree() > 1) {
								continue;
							}
							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
							for (Cluster2 c : plane.getClusters()) {
								if (c.equals(c1))
									continue;
								clusters.add(c);
							}
							if (clusters.isEmpty()) {
								State2 childState = SproutsGameSolver2.nooseModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), null);
								child = new Node2(childState, node);
								value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
								alpha = Math.max(alpha, value);
								if (beta <= alpha)
									break searchloop;
							}
							for (int n = 0; n < clusters.size() * 2; n++) {
								String binary = Integer.toBinaryString(n);
								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
								for (int j = 0; j < binary.length(); j++) {
									if (binary.charAt(j) == '1')
										clusterSublist.add(clusters.get(j));
								}
								State2 childState = SproutsGameSolver2.nooseModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), clusterSublist);
								child = new Node2(childState, node);
								value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
								alpha = Math.max(alpha, value);
								if (beta <= alpha)
									break searchloop;
							}
						}

						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
						for (Vertex2 v1 : c1.getVertices().values()) {
							if (v1.getDegree() > 2) {
								continue;
							}
							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
							for (Cluster2 c : plane.getClusters()) {
								if (c.equals(c1))
									continue;
								clusters.add(c);
							}
							ArrayList<Vertex2> candidateVertices = new ArrayList<Vertex2>();
							for (Vertex2 v : c1.getVertices().values()) {
								if (v.equals(v1))
									continue;
								if (v.getDegree() > 2)
									continue;
								candidateVertices.add(v);
							}
							if (clusters.isEmpty()) {
								for (Vertex2 v2 : candidateVertices) {
									State2 childState = SproutsGameSolver2.loopModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), null, c1.getVertices());
									child = new Node2(childState, node);
									value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
									alpha = Math.max(alpha, value);
									if (beta <= alpha)
										break searchloop;
								}
							}
							for (int n = 0; n < clusters.size() * 2; n++) {
								String binary = Integer.toBinaryString(n);
								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
								for (int j = 0; j < binary.length(); j++) {
									if (binary.charAt(j) == '1')
										clusterSublist.add(clusters.get(j));
								}
								for (Vertex2 v2 : candidateVertices) {
									State2 childState = SproutsGameSolver2.loopModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), clusterSublist, c1.getVertices());
									child = new Node2(childState, node);
									value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
									alpha = Math.max(alpha, value);
									if (beta <= alpha)
										break searchloop;
								}
							}
						}
					}
				}
				return value;
			} else {
				int value = Integer.MAX_VALUE;
				Node2 child = null;

				searchloop: for (Plane2 plane : node.getState().getPlanes().values()) {
					plane.setClusters(Cluster2.clusterPlane(plane, node.getState()));
					for (Cluster2 cluster : plane.getClusters()) {
						cluster.analyseCluster(plane, node.getState());
						cluster.setClusterComplexForm(cluster.searchCluster(plane, node.getState()));
					}
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
										State2 childState = SproutsGameSolver2.modifyStateInPlane(node.getState(), plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID());
										child = new Node2(childState, node);
										value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
										beta = Math.min(beta, value);
										if (beta <= alpha)
											break searchloop;
									}
								}
							}
						}

						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
						for (Vertex2 v1 : c1.getVertices().values()) {
							if (v1.getDegree() > 1) {
								continue;
							}
							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
							for (Cluster2 c : plane.getClusters()) {
								if (c.equals(c1))
									continue;
								clusters.add(c);
							}
							if (clusters.isEmpty()) {
								State2 childState = SproutsGameSolver2.nooseModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), null);
								child = new Node2(childState, node);
								value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
								beta = Math.min(beta, value);
								if (beta <= alpha)
									break searchloop;
							}
							for (int n = 0; n < clusters.size() * 2; n++) {
								String binary = Integer.toBinaryString(n);
								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
								for (int j = 0; j < binary.length(); j++) {
									if (binary.charAt(j) == '1')
										clusterSublist.add(clusters.get(j));
								}
								State2 childState = SproutsGameSolver2.nooseModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), clusterSublist);
								child = new Node2(childState, node);
								value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
								beta = Math.min(beta, value);
								if (beta <= alpha)
									break searchloop;
							}
						}

						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
						for (Vertex2 v1 : c1.getVertices().values()) {
							if (v1.getDegree() > 2) {
								continue;
							}
							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
							for (Cluster2 c : plane.getClusters()) {
								if (c.equals(c1))
									continue;
								clusters.add(c);
							}
							ArrayList<Vertex2> candidateVertices = new ArrayList<Vertex2>();
							for (Vertex2 v : c1.getVertices().values()) {
								if (v.equals(v1))
									continue;
								if (v.getDegree() > 2)
									continue;
								candidateVertices.add(v);
							}
							if (clusters.isEmpty()) {
								for (Vertex2 v2 : candidateVertices) {
									State2 childState = SproutsGameSolver2.loopModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), null, c1.getVertices());
									child = new Node2(childState, node);
									value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
									beta = Math.min(beta, value);
									if (beta <= alpha)
										break searchloop;
								}
							}
							for (int n = 0; n < clusters.size() * 2; n++) {
								String binary = Integer.toBinaryString(n);
								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
								for (int j = 0; j < binary.length(); j++) {
									if (binary.charAt(j) == '1')
										clusterSublist.add(clusters.get(j));
								}
								for (Vertex2 v2 : candidateVertices) {
									State2 childState = SproutsGameSolver2.loopModification(node.getState(), plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), clusterSublist, c1.getVertices());
									child = new Node2(childState, node);
									value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
									beta = Math.min(beta, value);
									if (beta <= alpha)
										break searchloop;
								}
							}
						}
					}
				}
				return value;
			}
		}
	}
}
