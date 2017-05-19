package model;

import java.util.HashSet;

import lombok.Getter;

@Getter
public class Edge2 {

	private int uniqueID;
	private Pair<Integer, Integer> nodes;
	private HashSet<Integer> plane_ids;

	public Edge2(int id, int v1_id, int v2_id, int... plane_ids) {
		uniqueID = id;
		nodes = new Pair<Integer, Integer>(v1_id, v2_id);
		this.plane_ids = new HashSet<Integer>();
		for (int i : plane_ids) {
			this.plane_ids.add(i);
		}
	}

	public Edge2(int id, Pair<Integer, Integer> nodes, HashSet<Integer> plane_ids) {
		this.uniqueID = id;
		this.nodes = nodes;
		this.plane_ids = plane_ids;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Edge2 clone() {
		return new Edge2(uniqueID, new Pair<Integer, Integer>(nodes.getFirst(), nodes.getSecond()), (HashSet<Integer>) plane_ids.clone());
	}

	@Override
	public String toString() {
		String str = "";
		str += "ID:" + uniqueID;
		str += " [V1:" + nodes.getFirst() + " V2:" + nodes.getSecond() + "] ";
		str += super.toString();
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		return ((Edge2) obj).getUniqueID() == uniqueID;
	}

	@Override
	public int hashCode() {
		return uniqueID + "Edge".hashCode();
	}
}
