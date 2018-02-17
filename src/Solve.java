import java.io.File;
import java.util.ArrayList;

import exceptions.IllegalFileException;
import maze.Maze;
import maze.Node;
import solve.AbstractSearch;
import solve.AbstractSearch.SolveMode;
import solve.BidirectionalSearch;
import solve.DFS;
import utils.Parser;

public class Solve {
	/*
	 * This main method is the entry point to executing the system
	 */
	public static void main(String[] args) throws IllegalFileException {
		double nanoSecondsPassed;
		nanoSecondsPassed = System.nanoTime();

		/* Change this variable to change the maze to execute */
		final String INPUT_FILE = "myMaze1.txt";

		/*
		 * Change this to SolveMode.ONE_SOLUTION to find only one solution or to
		 * SolveMode.ALL_SOLUTIONS to find all solutions
		 */
		final SolveMode SOLVE_MODE = SolveMode.ONE_SOLUTION;

		/*
		 * Turn this variable to true to use DFS when finding one solution. By
		 * default bidirectional search is used instead, as it is faster.
		 */
		final boolean useDFSForOneSolution = false;

		Maze<Node> maze;

		/*
		 * AbstractSearch is a superclass extended by all the algorithm
		 * implementing class so that different ones can be chosen at runtime
		 */
		AbstractSearch<Node> solver;
		Parser parser = new Parser();

		ArrayList<ArrayList<Node>> solutions = new ArrayList<>();

		maze = parser.parse(new File(INPUT_FILE));

		/*
		 * Solving with different algorithms depending on how many solution are
		 * wanted, as bidirectional search is the fastest for finding one
		 * solution and it also returns the shortest path, but depth first
		 * search is the only one that implements ALL_SOLUTION SolveMode
		 */

		if (SOLVE_MODE == SolveMode.ONE_SOLUTION && !useDFSForOneSolution) {
			solver = new BidirectionalSearch<Node>(maze, SOLVE_MODE);
		} else {
			solver = new DFS<Node>(maze, SOLVE_MODE);
		}

		System.out
				.println("Solving in mode: " + SOLVE_MODE.name() + ", using algorithm: " + solver.getClass().getSimpleName());

		solutions = solver.solve();

		if (!solutions.isEmpty()) {
			System.out.println("Solution(s): " + solutions);

		} else {
			System.out.println("\n\nThe maze has no solution");
		}

		nanoSecondsPassed = System.nanoTime() - nanoSecondsPassed;

		System.out.println("Elapsed time: " + nanoSecondsPassed / 1000000000 + " seconds");

	}

}
