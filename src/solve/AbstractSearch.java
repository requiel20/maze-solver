package solve;

import java.util.ArrayList;

import maze.Maze;

/**
 * This class represents an abstract search algorithm able to solve a maze.<br>
 * <br>
 * It contains a static enumeration type SolveMode, to represent whether one or
 * all solutions should be returned By the solving algorithm.<br>
 * <br>
 * The class itself contains fields to keep the maze to be solved and the
 * corresponding SolveMode.<br>
 * <br>
 * The contract of this class only require the extending subclasses to implement
 * the abstract solve() method with an algorithm returning a list of solutions.
 * <br>
 */
public abstract class AbstractSearch<NodeType> {
	protected SolveMode solveMode;
	protected Maze<NodeType> maze;

	/**
	 * Constructor that has to be called by the extending classes to set the
	 * maze to be solved and the corresponding SolveMode.
	 * 
	 * @param maze
	 *            the maze to be solved
	 * 
	 * @param solveMode
	 *            This can be one of AbstractSearch.SolveMode.ONE_SOLUTION (one
	 *            solution should be returned) or
	 *            AbstractSearch.SolveMode.ALL_SOLUTIONS (all solutions should
	 *            be returned)
	 */
	public AbstractSearch(Maze<NodeType> maze, SolveMode solveMode) {
		this.maze = maze;
		this.solveMode = solveMode;
	}

	/**
	 * This abstract method should be implemented by extending non-abstract
	 * classes with the implementation of a search algorithm capable of solving
	 * a maze.
	 * 
	 * @return the solution(s) to the maze
	 */
	public abstract ArrayList<ArrayList<NodeType>> solve();

	/**
	 * Returns the solve mode of this algorithm instance
	 * 
	 * @return the solve mode of this algorithm instance
	 * 
	 * @see SolveMode
	 */
	public SolveMode getSolveMode() {
		return solveMode;
	}

	/**
	 * Enumeration type to represent the two possible solve modes.
	 * <ul>
	 * <li>ONE_SOLUTION indicates that only one solution should be returned</li>
	 * <li>ALL_SOLUTIONs indicates that every possible simple (without loops)
	 * solution should be returned</li>
	 * </ul>
	 */
	public static enum SolveMode {
		ONE_SOLUTION, ALL_SOLUTIONS;
	}
}
