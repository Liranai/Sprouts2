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

//@formatter:off
	/**
	 * Runs through all Planes EVERY TIME to collect all clusters TODO: This can
	 * probably be optimized
	 * 
	 * @return
	 */
	/*public HashSet<Cluster> getClusters() {
		HashSet<Cluster> allClusters = new HashSet<Cluster>();
		for (Plane2 plane : planes.values()) {
			allClusters.addAll(plane.getClusters());
		}
		return allClusters;
	}

	/**
	 * Runs through all Planes EVERY TIME to collect all vertices TODO: This can
	 * probably be optimized
	 * 
	 * @return
	 */
	/*public HashSet<Vertex2> getVertices() {
		HashSet<Vertex2> allVertices = new HashSet<Vertex2>();
		for (Plane2 plane : planes.values()) {
			allVertices.addAll(plane.getVertices().values());
		}
		return allVertices;
	}

	/**
	 * Runs through all Planes EVERY TIME to collect all edges TODO: This can
	 * probably be optimized
	 * 
	 * @return
	 */
	/*public HashSet<Edge2> getEdges() {
		HashSet<Edge2> allEdges = new HashSet<Edge2>();
		for (Plane2 plane : planes.values()) {
			allEdges.addAll(plane.getEdges().values());
		}
		return allEdges;
	}*/
	//@formatter:on

	public int getNumberOfVertices() {
		return num_of_vertices;
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
		for (Vertex2 vertex : vertices.values()) {
			str += "V|" + vertex.toString(this) + " ID:" + vertex.getUniqueID() + " || ";
		}
		str += "\n";
		for (Edge2 edge : edges.values()) {
			str += "E|v1:" + edge.getNodes().getFirst() + " E|v2:" + edge.getNodes().getSecond() + " || ";
		}
		str += "\n";
		for (Plane2 plane : planes.values()) {
			str += "P|vs:" + plane.getUniqueID() + " " + plane.getVertex_ids() + " || ";
		}
		return str;
	}
}
