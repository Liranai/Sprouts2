
import gui.SproutsUI;
import logic.SproutsGameSolver2;
import model.State;

public class SproutsMain {

	public static void main(String[] args) {

		State state = new State(10);
		// System.out.println(state);
		SproutsGameSolver2 solver = new SproutsGameSolver2(state);
		SproutsUI ui = new SproutsUI(state, solver.getRoot());
		solver.setUi(ui);
		try {
			solver.run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
