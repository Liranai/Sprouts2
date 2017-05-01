package logic;

import java.util.Vector;

import gui.SproutsUI;
import lombok.Getter;
import lombok.Setter;
import model.Move;
import model.Node;
import model.State;

@Getter
public class SproutsGameSolver2 {

	private Node root;
	@Setter
	private SproutsUI ui;

	public SproutsGameSolver2(State state) {
		root = new Node(state, null);
	}

	public void run() {
		Vector<Move> moves = null;
		do {
			System.out.println(root.getState());
			ui.repaint();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			moves = SproutsGameSolver.getLegalMoves(root.getState());
			if (moves.size() > 0)
				moves.get(0).makeMove(root.getState());
		} while (moves.size() > 0);
	}
}
