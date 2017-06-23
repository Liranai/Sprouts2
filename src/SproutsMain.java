
import java.io.IOException;

import gui.SproutsUI2;
import logic.SproutsGameSolver2;
import model.State2;

public class SproutsMain {

	public static void main(String[] args) {

		State2 state = new State2(2);
		// // System.out.println(state);
		// Node2 root = new Node2(state, null);
		// new MultiThreadAlphaBetaSearch(root).run();
		//
		SproutsGameSolver2 solver = new SproutsGameSolver2(state, false);
		SproutsUI2 ui = new SproutsUI2(state, solver.getRoot());
		solver.setUi(ui);
		try {
			solver.run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
