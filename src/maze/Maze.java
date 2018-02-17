package maze;

import java.util.HashSet;

import exceptions.NullNodeException;

/**
 * This class represent a maze as a set of nodes connected by a set of
 * undirected edges. its type parameter NodeType allows any type to be used as a node.
 * <br>
 * Null nodes are not allowed.
 */
public class Maze<NodeType> {
	/* Edges of this maze */
	private HashSet<Edge<NodeType>> edges;

	/* Nodes of this maze */
	private HashSet<NodeType> nodes;

	/* Starting and ending nodes of this maze */
	private NodeType start;
	private NodeType end;

	/**
	 * Constructor to create a maze using the nodes that will be identified as
	 * starting and ending point of the maze. If at least one or the two
	 * parameters is null, a NullNodeException is thrown.
	 * 
	 * @param start
	 *            the start of the maze
	 * @param end
	 *            the end of the maze
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 * 
	 */
	public Maze(NodeType start, NodeType end) throws NullNodeException {
		if (start == null || end == null)
			throw new NullNodeException();

		this.edges = new HashSet<>();
		this.nodes = new HashSet<>();

		this.start = start;
		this.end = end;

		/*
		 * The starting and ending nodes are added to the set of all nodes
		 */
		nodes.add(start);
		nodes.add(end);
	}

	/**
	 * This method builds an undirected edge between two nodes, returning true
	 * on success, or false if the edge was already present. If one or both the
	 * nodes passed as parameters do not exist yet in this maze, this method
	 * will add them to the maze before building the edge. If at least one or
	 * the two parameters is null, a NullNodeException is thrown.
	 * 
	 * @param node1
	 *            the first node of the edge
	 * @param node2
	 *            the second node of the edge
	 * 
	 * @return true if the edge was not present already, false otherwise
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public boolean addEdge(NodeType node1, NodeType node2) throws NullNodeException {
		if (node1 == null || node2 == null)
			throw new NullNodeException();
		/* Adding nodes if not already present */
		if (!nodes.contains(node1))
			nodes.add(node1);
		if (!nodes.contains(node2))
			nodes.add(node2);

		/* Adding the edge */
		return edges.add(new Edge<NodeType>(node1, node2));
	}

	/**
	 * This method remove one of the edges of the maze, returning true on
	 * success, or false if the edge was not present. If at least one or the two
	 * parameters is null, a NullNodeException is thrown.
	 * 
	 * @param node1
	 *            the first node of the edge
	 * @param node2
	 *            the second node of the edge
	 * 
	 * @return false if the edge was not present, true on success
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public boolean removeEdge(NodeType node1, NodeType node2) throws NullNodeException {
		if (node1 == null || node2 == null)
			throw new NullNodeException();
		/* Removing the edge */
		return edges.remove(new Edge<NodeType>(node1, node2));
	}

	/**
	 * This method adds a node to the maze, returning true on success, or false
	 * if the node was already present. If null is passed as a parameter, a
	 * NullNodeException is thrown.
	 * 
	 * @param node
	 *            the node to be added
	 * 
	 * @return true if the node was not present already, false otherwise
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public boolean addNode(NodeType node) throws NullNodeException {
		if (node == null)
			throw new NullNodeException();
		return nodes.add(node);
	}

	/**
	 * This method remove one of the nodes of the maze, returning true on
	 * success, or false if the node was not present. If null is passed as a
	 * parameter, a NullNodeException is thrown.
	 * 
	 * @param node
	 *            the node to remove
	 * 
	 * @return false if the node was not present, true on success
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public boolean removeNode(NodeType node) throws NullNodeException {
		if (node == null)
			throw new NullNodeException();
		return edges.remove(node);
	}

	/**
	 * This method checks if an edge between two given nodes exists in this
	 * maze. If at least one or the two parameters is null, a NullNodeException
	 * is thrown.
	 * 
	 * @param node1
	 *            the first node of the edge
	 * @param node2
	 *            the second node of the edge
	 * 
	 * @return true if the edge is present in the maze, false otherwise
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public boolean containsEdge(NodeType node1, NodeType node2) throws NullNodeException {
		if (node1 == null || node2 == null)
			throw new NullNodeException();
		/*
		 * Note that new Edge(node1, node2).equals(new Edge(node2, node1)) is
		 * true
		 */
		return edges.contains(new Edge<NodeType>(node1, node2));
	}

	/**
	 * This method checks if a node exists in this maze. If null is passed as a
	 * parameter, a NullNodeException is thrown.
	 * 
	 * @param node
	 *            the node to check the presence of
	 * 
	 * @return true if the node is present in the maze, false otherwise
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public boolean containsNode(NodeType node) throws NullNodeException {
		if (node == null)
			throw new NullNodeException();
		return nodes.contains(node);
	}

	/**
	 * This method returns all the nodes that are neighbours to a given node. In
	 * other words, it returns any node such that there is an edge in this maze
	 * between it and the given node.If null is passed as a parameter, a
	 * NullNodeException is thrown.
	 * 
	 * @param node
	 *            the node to return the neighbours of
	 * 
	 * @return a set containing the neighbours of the given node
	 * 
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public HashSet<NodeType> neighbours(NodeType node) throws NullNodeException {
		if (node == null)
			throw new NullNodeException();
		HashSet<NodeType> neighbours = new HashSet<>();

		/*
		 * Adding to the output set every node that has an edge with the given
		 * node
		 */
		for (NodeType otherNode : nodes) {
			if (edges.contains(new Edge<NodeType>(node, otherNode)))
				neighbours.add(otherNode);
		}
		return neighbours;
	}

	
	/**
	 * Returns a shallow copy of the set of nodes in this maze
	 * 
	 * @return the set of nodes in this maze
	 */
	@SuppressWarnings("unchecked")
	public HashSet<NodeType> getNodes() {
		return (HashSet<NodeType>) nodes.clone();
	}

	/**
	 * Returns a shallow copy of the set of edges in this maze
	 * 
	 * @return the set of edges in this maze
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Edge<NodeType>> getEdges() {
		return (HashSet<Edge<NodeType>>) edges.clone();
	}

	/**
	 * Returns the starting node in this maze
	 * 
	 * @return the starting node in this maze
	 */
	public NodeType getStart() {
		return start;
	}

	/**
	 * Returns the ending node in this maze
	 * 
	 * @return the ending node in this maze
	 */
	public NodeType getEnd() {
		return end;
	}

	@Override
	public String toString() {
		String output = "";

		for (Edge<NodeType> e : edges) {
			output += (e.getNode1().toString() + " " + e.getNode2().toString() + "\n");
		}

		return output;
	}
}
