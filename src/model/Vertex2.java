package model;

import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Vertex2 {

	private int uniqueID;
	@Setter
	private int degree;
	private HashSet<Integer> edge_ids;
	private HashSet<Integer> plane_ids;

	@Setter
	private boolean original = false;

	public Vertex2(int id, int... plane_ids) {
		degree = 0;
		uniqueID = id;
		edge_ids = new HashSet<Integer>();
		this.plane_ids = new HashSet<Integer>();
		for (int i : plane_ids) {
			this.plane_ids.add(i);
		}
	}

	public Vertex2(int id, int degree, boolean original, HashSet<Integer> edges, HashSet<Integer> planes) {
		this.uniqueID = id;
		this.degree = degree;
		this.original = original;
		this.edge_ids = edges;
		this.plane_ids = planes;
	}

	public boolean increaseDegree(int n) {
		if (degree + n <= 3) {
			degree += n;
			return true;
		}
		return false;
	}

	public void addEdge(int... edge_ids) {
		for (int i : edge_ids) {
			this.edge_ids.add(i);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Vertex2 clone() {
		return new Vertex2(uniqueID, degree, original, (HashSet<Integer>) edge_ids.clone(), (HashSet<Integer>) plane_ids.clone());
	}

	public String toString(State2 state) {
		String str = "ID:" + uniqueID;
		str += " D:" + degree;
		for (Integer plane_id : plane_ids) {
			str += " P:" + plane_id;
		}
		for (Integer edge_id : edge_ids) {
			str += " E|" + state.getEdges().get(edge_id).toString();
		}
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		return ((Vertex2) obj).getUniqueID() == uniqueID;
	}

	@Override
	public int hashCode() {
		return uniqueID + "Vertex".hashCode();
	}
}
