package logic;

import java.util.ArrayList;
import java.util.Random;

import gui.SproutsUI;
import lombok.Getter;
import lombok.Setter;
import model.Cluster;
import model.Move;
import model.Node;
import model.Plane;
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
		Random rand = new Random(System.currentTimeMillis());

		ArrayList<Move> moves = null;
		do {
			System.out.println(root.getState());
			ui.repaint();
			// try {
			// Thread.sleep(5000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			for (Plane plane : root.getState().getPlanes().values()) {
				plane.setClusters(Cluster.clusterPlane(plane));
			}
			moves = SproutsGameSolver.getLegalMoves(root.getState());
			if (moves.size() > 0)
				moves.get(rand.nextInt(moves.size())).makeMove(root.getState());
		} while (moves.size() > 0);
	}
}
