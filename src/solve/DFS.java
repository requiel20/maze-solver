package solve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import exceptions.NullNodeException;
import maze.Maze;

/**
 * This class implements a DFS recursive algorithm that can solve a maze. The
 * nodes chosen to be expanded are always the most far away from the start, and
 * the algorithm backtracks only when going forward is no longer possible<br>
 * <br>
 * The type parameter NodeType allows this implementation not to require any particular
 * representation of a node. Correct implementations of hashCode and equals
 * methods are everything that this class needs to work with any type of node.
 * <br>
 * The colouring needed not to repeat the same node is implemented by
 * encapsulating nodes in an inner type, DFSNode. This class contains a pointer
 * to the node and a colour parameter from the enumeration type Color, which is
 * also enclosed in this class. <br>
 * <br>
 * This type contains three colours: WHITE, GREY and BLACK, which respectively
 * mean "Node never encountered", "Node encountered but not fully explored" and
 * "Node encountered and fully explored (all the accessible subgraph have been
 * completely expanded)".<br>
 * <br>
 * To find one non-optimal solution, the oneSolutionStep method recursively
 * calls itself on the neighbours of every encountered node, starting from the
 * beginning of the maze.<br>
 * It returns if it runs out of unexplored nodes or if it finds a solution.<br>
 * In the first case, an empty ArrayList is returned, while in the latter the
 * so-found solution is returned.<br>
 * The nodes are coloured as GREY as soon as they are encountered, and only
 * WHITE nodes are selected to be visited. Moreover, the nodes are marked as
 * BLACK if they are fully expanded.<br>
 * <br>
 * To find all solutions, the allSolutionStep method use a similar approach, but
 * there are two main differences:
 * <ul>
 * <li>first of all, the search does not stop if a solution is found;</li>
 * <li>and second, the nodes are marked as GREY as soon as they are encountered
 * to avoid loops, and only WHITE nodes are selected to be expanded, just as for
 * the search to one solution. But, when a node runs out of neighbours, it is
 * marked as BLACK only if all of its neighbours are black, or if it has no
 * neighbours at all. In this way any path that leads to a dead end is not
 * expanded again in next iterations.</li>
 * </ul>
 */
public class DFS<NodeType> extends AbstractSearch<NodeType> {

	/*
	 * To keep trace of the colours, the nodes have to be encapsulated in a
	 * DFSNode instance. This HashMap will contains these instances.
	 * 
	 * The nodes are added while searching, when they are needed.
	 */
	private HashMap<Integer, DFSNode> DFSNodes;
	
	/**
	 * Constructor that initialise a new instance of a maze solver using the
	 * recursive DFS algorithm
	 * 
	 * @param maze
	 *            The maze to solve
	 * @param solveMode
	 *            This can be one of AbstractSearch.SolveMode.ONE_SOLUTION (one
	 *            solution is returned) or
	 *            AbstractSearch.SolveMode.ALL_SOLUTIONS (all possible solutions
	 *            are returned)
	 */
	public DFS(Maze<NodeType> maze, SolveMode solveMode) {
		super(maze, solveMode);
		this.DFSNodes = new HashMap<>();
	}

	/**
	 * Find solutions to the maze. The maximum number of solutions returned
	 * depends on the solve mode chosen:
	 * <ul>
	 * <li>AbstractSearch.SolveMode.ONE_SOLUTION: one solution is returned;</li>
	 * <li>AbstractSearch.SolveMode.ALL_SOLUTIONS: all possible solutions are
	 * returned.</li>
	 * </ul>
	 * 
	 * @return paths in the maze (from start to solution) or an empty list if no
	 *         solution is found.
	 */
	@Override
	public ArrayList<ArrayList<NodeType>> solve() {
		if (solveMode == SolveMode.ONE_SOLUTION) {
			ArrayList<ArrayList<NodeType>> result = new ArrayList<>();
			result.add(oneSolutionStep(maze.getStart()));
			return result;
		} else {
			return allSolutionsStep(maze.getStart());
		}
	}

	/*
	 * Fundamental DFS recursive step to find one solution.
	 */
	private ArrayList<NodeType> oneSolutionStep(NodeType currentNode) {
		/* Variable to store a solution path */
		ArrayList<NodeType> path = new ArrayList<>();

		/*
		 * Getting (or adding if not present) the DFSNode corresponding to the
		 * current node
		 */
		DFSNode currentDFSNode = getDFSNode(currentNode);

		/* Getting the neighbours of the current node */
		HashSet<NodeType> neighbours = null;
		try {
			neighbours = maze.neighbours(currentNode);
		} catch (NullNodeException e1) {
			e1.printStackTrace();
		}

		/* Iterating over the neighbours */
		Iterator<NodeType> neighboursIterator = neighbours.iterator();

		/*
		 * Stopping if a solution is found, or if there are no more neighbours
		 */
		while (neighboursIterator.hasNext() && path.isEmpty()) {
			NodeType neighbour = neighboursIterator.next();

			/*
			 * Getting (or adding if not present) the DFSNode corresponding to
			 * the neighbour
			 */
			DFSNode DFSNeighbour = getDFSNode(neighbour);

			/* Nodes are grey-coloured as soon as they are encountered */
			currentDFSNode.colorGrey();

			/* Expanding only white neighbours, to avoid loops */
			if (DFSNeighbour.isWhite()) {
				/* If a solution is found is added to the path */
				if (neighbour.equals(maze.getEnd()))
					path.add(neighbour);
				else {
					/* Otherwise, this method calls himself on the neighbour */
					path = oneSolutionStep(neighbour);

				}
			}
		}

		/*
		 * When all the possible neighbours are explored, colour this node black
		 */
		currentDFSNode.colorBlack();

		/* If a solution path is being returned, add this node to it */
		if (path.size() != 0)
			path.add(0, currentNode);

		return path;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<ArrayList<NodeType>> allSolutionsStep(NodeType currentNode) {

		/* Variable to store a list of solution paths */
		ArrayList<ArrayList<NodeType>> paths = new ArrayList<>();

		/* Variable to store a solution path */
		ArrayList<NodeType> path = new ArrayList<>();

		/*
		 * Getting (or adding if not present) the DFSNode corresponding to the
		 * current node
		 */
		DFSNode currentDFSNode = getDFSNode(currentNode);

		/* Nodes are grey-coloured as soon as they are encountered */
		currentDFSNode.colorGrey();

		/*
		 * Variable to count how many neighbours are black after returning from
		 * this method's call on them.
		 * 
		 * If this matches the numbers of neighbour - 1 (as the node we came
		 * from to reach this one is of course unreachable as it was grey
		 * already), and no solutions path are being returned, this node can be
		 * marked as black as well, as it can not lead to any solution.
		 */
		int blackNeighbours = 0;

		/* Getting the neighbours of the current node */
		HashSet<NodeType> neighbours = null;
		try {
			neighbours = maze.neighbours(currentNode);
		} catch (NullNodeException e1) {
			e1.printStackTrace();
		}

		/* Iterating over the neighbours */
		Iterator<NodeType> neighboursIterator = neighbours.iterator();

		/* The search does not stop until it runs out of solutions */
		while (neighboursIterator.hasNext()) {
			NodeType neighbour = neighboursIterator.next();

			/*
			 * Getting (or adding if not present) the DFSNode corresponding to
			 * the neighbour
			 */
			DFSNode DFSNeighbour = getDFSNode(neighbour);

			/* Expanding only white neighbours, to avoid loops */
			if (DFSNeighbour.isWhite()) {
				/*
				 * If a solution is found, a new path is added to the list of
				 * paths to be returned
				 */
				if (neighbour.equals(maze.getEnd())) {
					path.add(neighbour);
					paths.add((ArrayList<NodeType>) path.clone());
					path.clear();
				} else {
					/* Otherwise, this method calls itself on the neighbour */
					paths.addAll(allSolutionsStep(neighbour));

					/*
					 * If the neighbour is black after returning from the call,
					 * add 1 to the count of black neighbours
					 */
					if (DFSNeighbour.isBlack())
						blackNeighbours++;
				}
			}
		}

		/* Check if this node is a dead end, if so it is coloured black */
		if (paths.isEmpty() && blackNeighbours == neighbours.size() - 1) {
			currentDFSNode.colorBlack();
		} else
			currentDFSNode.colorWhite();

		if (paths.size() != 0) {
			for (ArrayList<NodeType> returningPath : paths)
				returningPath.add(0, currentNode);
		}

		return paths;
	}

	/*
	 * Method to get the corresponding DFSNode from a NodeType instance. If this is the
	 * first time this node is encountered, this method will add a new DFSNode
	 * instance to the HasMap, colouring it white
	 */
	private DFSNode getDFSNode(NodeType node) {
		if (DFSNodes.containsKey(node.hashCode()))
			return DFSNodes.get(node.hashCode());
		else {
			/* DFSNode instances are created with the white colour */
			DFSNode newNode = new DFSNode(node);
			DFSNodes.put(node.hashCode(), newNode);
			return newNode;
		}
	}

	/*
	 * Inner class to store the pointer to a node, among with its colour. It
	 * also contains methods to check and change colour
	 */
	private class DFSNode {
		public NodeType node;
		public Color color;

		public DFSNode(NodeType node) {
			this.node = node;
			this.color = Color.WHITE;
		}

		public void colorWhite() {
			this.color = Color.WHITE;
		}

		public void colorGrey() {
			this.color = Color.GREY;
		}

		public void colorBlack() {
			this.color = Color.BLACK;
		}

		public boolean isWhite() {
			return this.color == Color.WHITE;
		}

		@SuppressWarnings("unused")
		public boolean isGrey() {
			return this.color == Color.GREY;
		}

		public boolean isBlack() {
			return this.color == Color.BLACK;
		}

		/*
		 * The hashCode and equals methods calls the one of the enclosed node,
		 * so that equals node can be recognised
		 */
		@Override
		public int hashCode() {
			return node.hashCode();
		}

		/*
		 * And the equals method just check for the inner node equality as well,
		 * not for the colour equality
		 */
		@Override
		public boolean equals(Object obj) {

			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			DFSNode other = (DFSNode) obj;
			if (!node.equals(other.node))
				return false;
			return true;
		}
	}

	private static enum Color {
		WHITE, GREY, BLACK;
	}
}
