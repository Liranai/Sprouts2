package model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Node {

	private Node parent;
	@Setter
	private State state;
	private int value;
	private ArrayList<Node> children;

	public Node(State state, Node parent) {
		this.state = state;
		this.parent = parent;
		value = 0;
		children = new ArrayList<Node>();
	}

	public void addChild(Node child) {
		children.add(child);
	}
}
