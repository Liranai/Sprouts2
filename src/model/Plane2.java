package model;

import java.util.ArrayList;
import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Plane2 {

	private HashSet<Integer> vertex_ids;
	private HashSet<Integer> edge_ids;
	@Setter
	private ArrayList<Cluster2> clusters;

	private int uniqueID;

	public Plane2(int id) {
		uniqueID = id;
		this.vertex_ids = new HashSet<Integer>();
		this.edge_ids = new HashSet<Integer>();
		this.clusters = new ArrayList<Cluster2>();
	}

	public Plane2(int id, HashSet<Integer> vertices, HashSet<Integer> edges) {
		this.uniqueID = id;
		this.vertex_ids = vertices;
		this.edge_ids = edges;
	}

	public ArrayList<String> getClusterTypes() {
		ArrayList<String> types = new ArrayList<String>();
		for (Cluster2 cluster : clusters) {
			if (!types.contains(cluster.getClusterForm()))
				types.add(cluster.getClusterForm());
		}
		return types;
	}

	public ArrayList<Cluster2> getClustersOfString(String id) {
		ArrayList<Cluster2> tempClusters = new ArrayList<Cluster2>();
		for (Cluster2 cluster : clusters) {
			if (cluster.getClusterForm().equals(id))
				tempClusters.add(cluster);
		}
		return tempClusters;
	}

	public ArrayList<Cluster2> getClustersOfStringExcluding(String id, Cluster2 exclude) {
		ArrayList<Cluster2> tempClusters = new ArrayList<Cluster2>();
		for (Cluster2 cluster : clusters) {
			if (cluster.equals(exclude))
				continue;
			if (cluster.getClusterForm().equals(id))
				tempClusters.add(cluster);
		}
		return tempClusters;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Plane2 clone() {
		return new Plane2(uniqueID, (HashSet<Integer>) vertex_ids.clone(), (HashSet<Integer>) edge_ids.clone());
	}

	@Override
	public boolean equals(Object obj) {
		return ((Plane2) obj).getUniqueID() == uniqueID;
	}

	@Override
	public int hashCode() {
		return uniqueID + "Plane".hashCode();
	}
}
