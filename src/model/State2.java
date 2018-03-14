package model;

import java.util.HashMap;

import lombok.Getter;

@Getter
public class State2 {

	private HashMap<Integer, Plane2> planes;
	private HashMap<Integer, Vertex2> vertices;
	private HashMap<Integer, Edge2> edges;

	private int num_of_vertices, num_of_edges, num_of_planes;
	public static int SPROUTS = 0;

	public State2(int numberOfVertices) {
		SPROUTS = numberOfVertices;
		planes = new HashMap<Integer, Plane2>();
		vertices = new HashMap<Integer, Vertex2>();
		edges = new HashMap<Integer, Edge2>();

		num_of_vertices = 0;
		num_of_edges = 0;
		num_of_planes = 0;

		Plane2 plane = addPlane();
		for (int i = 0; i < numberOfVertices; i++) {
			Vertex2 vertex = addVertex(plane.getUniqueID());
			vertex.setOriginal(true);
		}
	}

	public boolean isEqualTo(State2 state) {

		for (Plane2 plane : state.getPlanes().values()) {
			for (Cluster2 cluster : plane.getClusters()) {
				boolean hasEqual = false;
				equivalence: for (Plane2 p2 : planes.values()) {
					if (!(plane.getVertex_ids().size() == p2.getVertex_ids().size()))
						continue;

					for (Cluster2 c2 : p2.getClusters()) {
						if (Cluster2.checkIsomorphism(cluster.clone(), c2.clone())) {
							// return true;
							hasEqual = true;
							break equivalence;
						}
					}
				}
				if (!hasEqual) {
					return false;
				}
			}
		}
		return true;
	}

	public String getStringRepresentation() {
		StringBuilder bldr = new StringBuilder();
		for (Plane2 plane : planes.values()) {
			plane.setClusters(Cluster2.clusterPlane(plane, this));
			for (Cluster2 cluster : plane.getClusters()) {
				for (Vertex2 vertex : cluster.getVertices().values()) {
					bldr.append(vertex.getUniqueID() + ",");
				}
				bldr.append(".");
			}
			bldr.append("}");
		}
		bldr.append("!");
		return bldr.toString();
	}

	public String getNewStringRepresentation() {
		StringBuilder bldr = new StringBuilder();
		for (Plane2 plane : planes.values()) {
			int open_nodes = 0;
			checkloop: for (Integer vertex_id : plane.getVertex_ids()) {
				if (vertices.get(vertex_id).getDegree() < 2) {
					open_nodes += 2;
					break checkloop;
				}
				if (vertices.get(vertex_id).getDegree() < 3) {
					open_nodes++;
					if (open_nodes > 1) {
						break checkloop;
					}
				}
			}
			if (open_nodes < 2) {
				continue;
			}

			plane.setClusters(Cluster2.clusterPlane(plane, this));
			for (Cluster2 cluster : plane.getClusters()) {
				boolean print = false;
				for (Vertex2 vertex : cluster.getVertices().values()) {
					if (vertex.getDegree() == 3)
						continue;
					if (vertex.getDegree() == 2) {
						bldr.append(vertex.getUniqueID() + ",");
					} else {
						bldr.append(vertex.getDegree() + ",");
					}
					print = true;
				}
				if (print)
					bldr.append(".");
			}
			bldr.append("}");
		}
		bldr.append("!");
		return bldr.toString();
	}

	public State2(int num_of_vertices, int num_of_edges, int num_of_planes, HashMap<Integer, Vertex2> vertices, HashMap<Integer, Edge2> edges, HashMap<Integer, Plane2> planes) {
		this.num_of_vertices = num_of_vertices;
		this.num_of_edges = num_of_edges;
		this.num_of_planes = num_of_planes;
		this.vertices = vertices;
		this.edges = edges;
		this.planes = planes;
	}

	/**
	 * Creates a vertex and puts it in plane with UniqueID plane_ids
	 * 
	 * @param plane_ids
	 *            Planes vertex is put in
	 * @return vertex created
	 */
	public Vertex2 addVertex(int... plane_ids) {
		num_of_vertices++;
		Vertex2 extraVertex = new Vertex2(num_of_vertices, plane_ids);
		vertices.put(num_of_vertices, extraVertex);
		for (int i : plane_ids) {
			planes.get(i).getVertex_ids().add(extraVertex.getUniqueID());
		}
		return extraVertex;
	}

	/**
	 * Creates edge connecting vertex v1 and v2, indicated by v1_id and v2_id,
	 * put in plane with UniqueID plane_id
	 * 
	 * @param plane_id
	 * @param v1_id
	 * @param v2_id
	 * @return edge created
	 */
	public Edge2 addEdge(int v1_id, int v2_id, int... plane_ids) {
		num_of_edges++;
		Edge2 extraEdge = new Edge2(num_of_edges, v1_id, v2_id, plane_ids);
		edges.put(num_of_edges, extraEdge);
		for (int i : plane_ids) {
			planes.get(i).getEdge_ids().add(extraEdge.getUniqueID());
		}
		return extraEdge;
	}

	public Plane2 addPlane() {
		num_of_planes++;
		Plane2 extraPlane = new Plane2(num_of_planes);
		planes.put(num_of_planes, extraPlane);
		return extraPlane;
	}

	public boolean isTerminal() {
		for (Plane2 plane : planes.values()) {
			int open_nodes = 0;
			for (Integer vertex_id : plane.getVertex_ids()) {
				if (vertices.get(vertex_id).getDegree() < 2)
					return false;
				if (vertices.get(vertex_id).getDegree() < 3) {
					if (open_nodes > 0) {
						return false;
					}
					open_nodes++;
				}
			}
		}
		return true;
	}

	@Override
	public State2 clone() {
		HashMap<Integer, Vertex2> clonedVertices = new HashMap<Integer, Vertex2>();
		for (Vertex2 vertex : vertices.values()) {
			clonedVertices.put(vertex.getUniqueID(), vertex.clone());
		}
		HashMap<Integer, Edge2> clonedEdges = new HashMap<Integer, Edge2>();
		for (Edge2 edge : edges.values()) {
			clonedEdges.put(edge.getUniqueID(), edge.clone());
		}
		HashMap<Integer, Plane2> clonedPlanes = new HashMap<Integer, Plane2>();
		for (Plane2 plane : planes.values()) {
			clonedPlanes.put(plane.getUniqueID(), plane.clone());
		}
		return new State2(num_of_vertices, num_of_edges, num_of_planes, clonedVertices, clonedEdges, clonedPlanes);
	}

	public String toString() {
		String str = "";
		// for (Vertex2 vertex : vertices.values()) {
		// str += "V|" + vertex.toString(this) + " ID:" + vertex.getUniqueID() +
		// " || ";
		// }
		// str += "\n";
		for (Edge2 edge : edges.values()) {
			str += "E|v1:" + edge.getNodes().getFirst() + " E|v2:" + edge.getNodes().getSecond() + " || ";
		}
		str += "\n";
		for (Plane2 plane : planes.values()) {
			str += "P|vs:" + plane.getUniqueID() + " V:" + plane.getVertex_ids() + " || E:" + plane.getEdge_ids();
		}
		return str;
	}
}
