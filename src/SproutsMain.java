
import gui.SproutsUI2;
import logic.SproutsGameSolver2;
import model.State2;

public class SproutsMain {

	public static void main(String[] args) {

		State2 state = new State2(3);
		// System.out.println(state);
		SproutsGameSolver2 solver = new SproutsGameSolver2(state);
		SproutsUI2 ui = new SproutsUI2(state, solver.getRoot());
		solver.setUi(ui);
		try {
			solver.run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
