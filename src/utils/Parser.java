package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exceptions.IllegalFileException;
import exceptions.NullNodeException;
import maze.Maze;
import maze.Node;

/**
 * This class represent a parser to read a maze from a file. The class type of
 * the nodes in the maze will be maze.Node<br>
 * <br>
 * The maze must be provided in a txt file and represented by an undirected
 * graph. Each node of the graph is represented by an integer ID, and the txt
 * file must contain a line for every edge.<br>
 * <br>
 * Each line must contain, in this order:<br>
 * <ol>
 * <li>An integer, corresponding to the ID of one of the nodes of the edge</li>
 * <li>A whitespace</li>
 * <li>An integer, corresponding to the ID of the other node of the edge</li>
 * </ol>
 * Empty lines are allowed.<br>
 * <br>
 * The start of the maze must be represented by the node of ID 0, and the end of
 * the maze by the node of ID 1.<br>
 * <br>
 * Nodes themselves do not need to be represented, a node will be created the
 * first time it appears in the file as part of an edge.<br>
 * <br>
 */
public class Parser {

	/**
	 * Parse the given file and returns the corresponding Maze instance. If the
	 * file does not exist or is in a wrong format, an IllegalFileException is
	 * thrown instead.
	 * 
	 * @param file
	 *            the file to be parsed
	 * 
	 * @return the maze represented by the file
	 * 
	 * @throws IllegalFileException
	 *             if the file does not exist or is bad formatted
	 */
	public Maze<Node> parse(File file) throws IllegalFileException {
		/* Variable for the maze to return */
		Maze<Node> maze = null;
		try {
			maze = new Maze<>(new Node(0), new Node(1));
		} catch (NullNodeException e1) {
		}

		/* String variable for each line of the file */
		String line;

		/* Instantiate the file reader and opening the file */
		BufferedReader in = null;

		if (!file.exists() || file.isDirectory())
			throw new IllegalFileException();

		try {
			in = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		/*
		 * Until new lines are present, try to parse them and create the
		 * corresponding edges (and nodes)
		 */
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				/* Allows empty lines, they are just skipped */
				if (!line.equals("")) {
					int id1, id2;

					/* Split the line using the whitespace */
					String[] tokens = line.split(" ");

					/*
					 * If there are not exactly two tokens, the exception is
					 * thrown
					 */
					if (tokens.length != 2)
						throw new IllegalFileException();

					try {
						id1 = Integer.parseInt(tokens[0]);
						id2 = Integer.parseInt(tokens[1]);
					} catch (NumberFormatException e) {
						/*
						 * If a token is other than an integer, the exception is
						 * thrown
						 */
						throw new IllegalFileException();
					}
					try {
						/*
						 * The addEdge method will add nodes as well, if they
						 * are not already present
						 */
						maze.addEdge(new Node(id1), new Node(id2));
					} catch (NullNodeException e) {
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return maze;
	}
}
