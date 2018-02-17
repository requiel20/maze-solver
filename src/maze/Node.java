package maze;

/**
 * This class represents a Node in a maze. It can be used to parametrise the
 * generic classes in this project. This class contains an integer ID parameter
 * to distinguish nodes, so that two nodes with the same ID are equals and have
 * the same hash code.
 */
public class Node {
	private int ID;
	
	/**
	 * Constructor to create a node with a given ID
	 * 
	 * @param ID the ID of the node
	 */
	public Node(int ID) {
		this.ID = ID;
	}

	/**
	 * Returns the ID of this node
	 * 
	 * @return the ID of this node
	 */
	public int getID() {
		return ID;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (ID != other.ID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "" + ID;
	}
}
