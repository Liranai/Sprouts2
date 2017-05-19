package model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Node2 {

	private Node2 parent;
	@Setter
	private State2 state;
	private int value;
	private ArrayList<Node2> children;

	public Node2(State2 state, Node2 parent) {
		this.state = state;
		this.parent = parent;
		value = 0;
		children = new ArrayList<Node2>();
	}

	public void addChild(Node2 child) {
		children.add(child);
	}
}
