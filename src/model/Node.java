package model;

import java.util.Vector;

import lombok.Getter;

@Getter
public class Node {

	private Node parent;
	private State state;
	private int value;
	private Vector<Node> children;

	public Node(State state, Node parent) {
		this.state = state;
		this.parent = parent;
		value = 0;
		children = new Vector<Node>();
	}

	public void addChild(Node child) {
		children.add(child);
	}
}
