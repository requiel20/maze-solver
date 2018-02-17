package maze;

import exceptions.NullNodeException;

/**
 * This class represent an undirected edge between two nodes in a graph.<br>
 * As it is undirected, its hashCode and equals methods have been implemented
 * such that an edge from the node A to the node B is considered equals to an
 * edge from the node B to the node A.<br>
 * Its type parameter NodeType allows type can be used as a node. It does not allow
 * null nodes.<br>
 */
public class Edge<NodeType> {
	private NodeType node1;
	private NodeType node2;

	/**
	 * Basic constructor to create an edge between two nodes. If at least one or
	 * the two parameters is null, a NullNodeException is thrown.
	 * 
	 * @param node1
	 *            the first node of the edge
	 * @param node2
	 *            the second node of the edge
	 * @throws NullNodeException
	 *             if null is passed as a parameter
	 */
	public Edge(NodeType node1, NodeType node2) throws NullNodeException {
		if (node1 == null || node2 == null)
			throw new NullNodeException();
		this.node1 = node1;
		this.node2 = node2;
	}

	/**
	 * Returns the first node of this edge
	 * 
	 * @return the first node of this edge
	 */
	public NodeType getNode1() {
		return node1;
	}

	/**
	 * Returns the second node of this edge
	 * 
	 * @return the second node of this edge
	 */
	public NodeType getNode2() {
		return node2;
	}

	/**
	 * On top of what specified in Object.hashCode, this method will
	 * return the same value for an edge from the node A to the node B and
	 * another edge from the node B to the node A.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result1 = 1;
		result1 = prime * result1 + ((node1 == null) ? 0 : node1.hashCode());
		result1 = prime * result1 + ((node2 == null) ? 0 : node2.hashCode());

		int result2 = 1;
		result2 = prime * result2 + ((node2 == null) ? 0 : node2.hashCode());
		result2 = prime * result2 + ((node1 == null) ? 0 : node1.hashCode());
		
		return result1 + result2;
	}

	/**
	 * On top of what specified in Object.equals, for this method to
	 * return true is enough that the two edges contain the same two nodes,
	 * regardless of which attributes is assigned to them.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge<?> other = (Edge<?>) obj;
		if ((this.node1.equals(other.node1) && this.node2.equals(other.node2))
				|| (this.node2.equals(other.node1) && this.node1.equals(other.node2)))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Edge between " + node1 + " and " + node2;
	}
}
