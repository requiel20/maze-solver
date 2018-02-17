package solve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import exceptions.NullNodeException;
import maze.Maze;

/**
 * This class represents an implementation of the bidirectional search algorithm
 * that can be used to solve a maze. The two searches are started one from the
 * start and one from the end of the maze, both using breadth first search. The
 * code used to implement BFS is contained in an homonym inner class. <br>
 * One of the two instances of this inner class that are created is the
 * "checker" and the other is not. <br>
 * <br>
 * This two instances proceed one BFS basic step each, but if one of the two
 * reach a queue length higher than the other, the control is passed to the
 * other search direction until the two queue sizes do not get back to a smaller
 * difference. <br>
 * In this way the execution speed is improved by exploring smaller sets of
 * nodes when possible. <br>
 * 2.5 times is used as a reasonable branching factor for a maze, so one search
 * is skipped if its queue is more than 2.5 times longer than the one of the
 * other search. <br>
 * <br>
 * The two instances of the BFS inner class have different roles.<br>
 * The "non-checker" instance will update a shared HashSet called "horizon" with
 * the set of nodes it visits in each iteration. <br>
 * On the other hand, the "checker" instance will check if any of the nodes
 * discovered during the last step is in the horizon. <br>
 * If so, this node is called a "connection node" and a solution can be found
 * backtracking from that node in both directions and joining the paths. <br>
 * <br>
 * The connection node and a flag representing whether the search is ended or
 * not are encapsulated in an instance of an inner class called BidSearchResult.
 * <br>
 * The checker instance always check from connection nodes, even if its queue
 * was too long to make it do its step. <br>
 * <br>
 * Backtracking from a connection node to the start of a side of the search is
 * possible because visited nodes are encapsulated in an instance of an inner
 * class, called BFSNode. <br>
 * Each instance of this BFSNode inner class contains a pointer to a node and an
 * integer "distance" representing how far from the start of the search that
 * node is. For this reason, by iteratively choosing the minimum-distance
 * neighbour from the last node backtracked, is possible to find the shortest
 * path to the start of the search.
 */
public class BidirectionalSearch<NodeType> extends AbstractSearch<NodeType> {

	/**
	 * This field contains the estimated branching factor for a maze <br>
	 * <br>
	 * In this way each BFS instance will not do its step its queue is more than
	 * this value times longer than the one of the other BFS instance <br>
	 */
	public static final double ESTIMATED_BRANCHING_FACTOR = 2.5;

	/* The set which contains the nodes visited in the last iteration */
	private HashSet<NodeType> horizon;

	/* Two instances of the inner class BFS used to search from both sides */
	private BFS bfsStart, bfsEnd;

	/**
	 * This constructor will create an instance that will search the given maze
	 * for the quickest solution.<br>
	 * <br>
	 * Note that, as this implementation do not support searching for all
	 * solutions, trying to solve a maze after passing
	 * AbstractSearch.SolveMode.ALL_SOLUTIONS to this constructor will result in
	 * an UnsupportedOperationException<br>
	 * 
	 * @param maze
	 *            the maze to be searched for solutions
	 * 
	 * @param solveMode
	 *            This can be one of AbstractSearch.SolveMode.ONE_SOLUTION (one,
	 *            optimal solution is returned) or
	 *            AbstractSearch.SolveMode.ALL_SOLUTIONS (the solve method will
	 *            throw an UnsupportedOperationException)
	 */
	public BidirectionalSearch(Maze<NodeType> maze, SolveMode solveMode) {
		super(maze, solveMode);
		horizon = new HashSet<NodeType>();
		/*
		 * Is actually irrelevant which of the two instances is the checker
		 * (true passed as a parameter)
		 */
		bfsStart = new BFS(maze.getStart(), true);
		bfsEnd = new BFS(maze.getEnd(), false);
	}

	/**
	 * Search the maze for the shortest path to the end.
	 * 
	 * @return the shortest solution to the maze
	 * 
	 * @throws UnsupportedOperationException
	 *             if AbstractSearch.SolveMode.ALL_SOLUTIONS was passed when
	 *             instantiating this class
	 */
	@Override
	public ArrayList<ArrayList<NodeType>> solve() {
		/* Variable for a single path to solution */
		ArrayList<NodeType> path = new ArrayList<>();

		/*
		 * Variable for returning the result, as the AbstractSearch contract
		 * requires an array of arrays
		 */
		ArrayList<ArrayList<NodeType>> paths = new ArrayList<>();

		/* Variables for the semipaths returned by each BFS */
		ArrayList<NodeType> pathStart = null;
		ArrayList<NodeType> pathEnd = null;

		/* Variables for the results returned by each BFS */
		BidSearchResult resultChecker = new BidSearchResult(false, null);
		BidSearchResult resultNotChecker = new BidSearchResult(false, null);

		if (this.solveMode == SolveMode.ONE_SOLUTION) {
			/*
			 * The search continues until one of the following is true:
			 * 
			 * - The checker reports that the search ended and he has found a
			 * connection node
			 * 
			 * - Both the checker and the non-checker report that the search
			 * ended, and the checker has not found a connection node. This
			 * means that both BFSs ran out of nodes to visit.
			 */
			while (!(resultChecker.ended && resultChecker.connectionNode != null)
					&& !(resultChecker.ended && resultNotChecker.ended)) {

				/*
				 * Each instances do a step only if its queue is not empty and
				 * its not much bigger than the other BFS's queue
				 */
				if (!bfsEnd.queue.isEmpty()
						&& bfsEnd.queue.size() < ESTIMATED_BRANCHING_FACTOR * (bfsStart.queue.size() + 1))
					resultNotChecker = bfsEnd.step();

				if (!bfsStart.queue.isEmpty()
						&& bfsStart.queue.size() < ESTIMATED_BRANCHING_FACTOR * (bfsEnd.queue.size() + 1)) {
					resultChecker = bfsStart.step();
				} else {
					/*
					 * However, the checker always checks for connection nodes,
					 * even if it did not do any step
					 */
					resultChecker = bfsStart.check();
				}

			}

			/*
			 * If a connection node is found the two semipaths have to be merged
			 */
			if (resultChecker.connectionNode != null) {
				/* Backtracking */
				pathStart = bfsStart.backtrack(resultChecker.connectionNode);
				pathEnd = bfsEnd.backtrack(resultChecker.connectionNode);

				/* Merging */
				pathStart.remove(0);
				Collections.reverse(pathStart);
				path.addAll(pathStart);
				path.addAll(pathEnd);
				paths.add(path);
			}
		} else {
			throw new UnsupportedOperationException();
		}

		return paths;
	}

	/* Finds the neighbours using the maze neighbours method */
	private HashSet<NodeType> nextStates(NodeType currentState) throws NullNodeException {
		return maze.neighbours(currentState);
	}

	/*
	 * This inner class represent a Breadth First Search instance to search half
	 * of the maze. When instantiating this class a flag "isChecker" must be
	 * provided.
	 * 
	 * - If true is given, the resulting instance is a "checker" and will check
	 * if each encountered node is in the horizon
	 * 
	 * - If false is given, the resulting instance is a "non-checker" and will
	 * update the horizon with nodes encountered in each iteration.
	 */
	private class BFS {
		/*
		 * Attribute to keep trace of the visited nodes. It will also be used to
		 * backtrack from the connection node to the start of the search. The
		 * key for this HashMap is the hashCode of BFSNode.node, as the distance
		 * is irrelevant in this case for equality purposes.
		 * 
		 * This is not an hash set because in the backtrack() method the
		 * recorded distance for a BFSNode must be retrieved using only a
		 * reference to the inner node, which is not possible using an HashSet.
		 */
		private HashMap<Integer, BFSNode> visited;

		/* Queue for storing the nodes that have to be visited next */
		private LinkedList<BFSNode> queue;

		/* Node to start the search from */
		private NodeType startNode;

		/* Flag to represent if this instance is a checker or not */
		private boolean isChecker;

		/*
		 * Because at every iteration the control has to be passed to the other
		 * BFS instance, the neighbours of every visited node are stored in
		 * another queue, instead of adding them to the current one. In this way
		 * the end of the iteration can be detected (when the current queue has
		 * no elements left)
		 */
		private LinkedList<BFSNode> nextQueue;

		/*
		 * Constructor to instantiate a search starting from the given startNode
		 * and with the given isChecker flag
		 */
		public BFS(NodeType startNode, boolean isChecker) {
			queue = new LinkedList<>();
			nextQueue = new LinkedList<>();
			visited = new HashMap<>();

			queue.addLast(new BFSNode(startNode, 0));

			visited.put(startNode.hashCode(), new BFSNode(startNode, 0));

			this.isChecker = isChecker;
			this.startNode = startNode;
		}

		/* Fundamental step of BFS while searching for a solution */
		@SuppressWarnings("unchecked")
		private BidSearchResult step() {
			/*
			 * Variable to check if the current node is found in the horizon.
			 * This can happen if the horizon is updated with this node after
			 * the non checker part has examined it already as a neighbour.
			 * 
			 * (As the two instances are given control one time each, the
			 * horizon can not be more than one step ahead.)
			 */
			boolean currentNodeFound = false;

			/* The result of this step of the search */
			BidSearchResult result = new BidSearchResult(false, null);

			while (!queue.isEmpty()) {
				BFSNode currentNode = queue.removeFirst();

				/* The checker checks if it is in the horizon */
				if (isChecker && horizon.contains(currentNode.node)) {
					result.ended = true;
					result.connectionNode = currentNode;
					currentNodeFound = true;
				}

				/*
				 * If the current node is not in the horizon (or if this
				 * instance is no checker)
				 */
				if (!currentNodeFound) {

					/* Getting the neighbours of the current node */
					HashSet<NodeType> neighbours = null;
					try {
						neighbours = maze.neighbours(currentNode.node);
					} catch (NullNodeException e1) {
						e1.printStackTrace();
					}

					/* Iterating over the neighbours */
					Iterator<NodeType> neighboursIterator = neighbours.iterator();

					/*
					 * Stopping if a solution is found, or if there are no more
					 * neighbours
					 */
					while (neighboursIterator.hasNext() && result.connectionNode == null) {

						NodeType node = neighboursIterator.next();

						/*
						 * Creating a bfsNode so that backtracking is possible
						 * (to create solution path)
						 */
						BFSNode bfsNode = new BFSNode(node, currentNode.distance + 1);

						/* If not already visited */
						if (!visited.containsKey(bfsNode.hashCode())) {

							/*
							 * If checker, check if the node is in the horizon.
							 * If so update the returned result and the search
							 * is ended.
							 */
							if (isChecker && horizon.contains(node)) {
								result.ended = true;
								result.connectionNode = new BFSNode(node, currentNode.distance + 1);
							} else {
								/*
								 * As this is an ONE_SOLUTION instance, the node
								 * do not need to be added in the queue if found
								 * as the search will stop.
								 */
								nextQueue.addLast(new BFSNode(node, currentNode.distance + 1));
							}

							/*
							 * Add the node to the set of visited nodes
							 */
							visited.put(bfsNode.hashCode(), bfsNode);
						}
					}
				}
			}

			/* The non-checker updates the horizon */
			if (!isChecker) {
				horizon.clear();
				for (BFSNode node : nextQueue) {
					horizon.add(node.node);
				}
			}
			/*
			 * If result.ended is true, this is a checker instance that has
			 * found a connection node and the search has to end.
			 * 
			 * If result.eneded is false no connection nodes have been found
			 * and, both if this instance was a checker or not, the other
			 * condition to end the search is that both queues have to be empty.
			 * So that is the value returned in result.ended
			 */
			if (!result.ended)
				result.ended = nextQueue.isEmpty();

			/* Updates the queue */
			queue = (LinkedList<BidirectionalSearch<NodeType>.BFSNode>) nextQueue.clone();
			nextQueue.clear();

			return result;
		}

		/*
		 * Returns the path from the given connectionNode to the start of this
		 * half of the search.
		 */
		public ArrayList<NodeType> backtrack(BFSNode connectionNode) {
			/* To store neighbours */
			HashSet<NodeType> nextStates = new HashSet<>();

			/* Path to be returned */
			ArrayList<NodeType> path = new ArrayList<>();

			/* To keep trace of the next node chosen to fill the path */
			NodeType lastNode = connectionNode.node;
			path.add(lastNode);

			/*
			 * Iteratively search for the least distance neighbour of the
			 * current node, as that is the node that will more quickly get to
			 * the start of the search.
			 */
			while (!lastNode.equals(startNode)) {
				int minDistance = -1;
				NodeType minDistanceNode = null;
				/* Get the neighbours of the current node */
				try {
					nextStates = nextStates(lastNode);
				} catch (NullNodeException e) {
					e.printStackTrace();
				}

				/* Get the least distance one */
				for (NodeType nextState : nextStates) {
					if (visited.containsKey(nextState.hashCode())
							&& (minDistance == -1 || visited.get(nextState.hashCode()).distance < minDistance)) {
						minDistanceNode = visited.get(nextState.hashCode()).node;
						minDistance = visited.get(nextState.hashCode()).distance;
					}
				}

				/* Add it to the path */
				path.add(minDistanceNode);
				lastNode = minDistanceNode;
			}

			return path;
		}

		/*
		 * A method to check if the search has to end. It is used if step() its
		 * not called because one BFS instance's queue is too much longer than
		 * the other's one.
		 */
		private BidSearchResult check() {
			/*
			 * If this method is called by the non-checker, it will check
			 * whether its queue is empty
			 */
			BidSearchResult result = new BidSearchResult(nextQueue.isEmpty(), null);

			/*
			 * If this method is called by the checker it will check whether a
			 * connection node can be found
			 */
			if (isChecker) {
				for (BFSNode node : queue) {
					if (horizon.contains(node.node)) {
						result.ended = true;
						result.connectionNode = node;
					}
				}
			}

			return result;
		}
	}

	/*
	 * Class to encapsulate a node and a "distance" integer, for backtracking
	 * purposes. Two BFSNode encapsulating the same node are equals to this
	 * implementation, so that the system can distinguish a visited node without
	 * having to depend on its distance
	 */
	private class BFSNode {
		NodeType node;
		int distance;

		public BFSNode(NodeType node, int distance) {
			this.node = node;
			this.distance = distance;
		}

		/* The equals checks only for this.node equality */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			BFSNode other = (BFSNode) obj;
			if (!node.equals(other.node))
				return false;
			return true;
		}

		/* The hashCode is this.node.hashCode() */
		@Override
		public int hashCode() {
			return node.hashCode();
		}

		@Override
		public String toString() {
			return node.toString() + " distance: " + distance;
		}
	}

	/* Class to represent a result of a BFS result */
	private class BidSearchResult {
		/* Attribute to check if the search has to be ended */
		private boolean ended;

		/* Eventual connection node found */
		private BFSNode connectionNode;

		public BidSearchResult(boolean ended, BFSNode connectionNode) {
			this.ended = ended;
			this.connectionNode = connectionNode;
		}

		@Override
		public String toString() {
			return (ended ? "ended " : "not ended ") + "connectionNode: " + connectionNode;
		}

	}
}
