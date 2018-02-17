# maze-solver
A simple maze solver that use DFS or bidirectional BFS.

## Input file
The maze must be provided in a txt file and represented by an undirected graph. Each node of the graph is represented by an integer ID, and the txt file must contain a line for every edge.

Each line must contain, in this order:

1. An integer, corresponding to the ID of one of the nodes of the edge
2. A whitespace
3. An integer, corresponding to the ID of the other node of the edge

Empty lines are allowed.

The start of the maze must be represented by the node of ID 0, and the end of the maze by the node of ID 1. Nodes themselves do not need to be represented, a node will be created the first time it appears in the file as part of an edge.

## Running the solver
The main class is Solver.java The path to the file containing the maze to solve has to be specified in the constant INPUT_FILE.

The variable SOLVE_MODE has two possible values:

* SolveMode.ONE_SOLUTION
* SolveMode.ALL_SOLUTIONS

the first option makes the solver find a single optimal solution, while the second all possible distinct solutions. Bidirectional BFS is used in the first case, while DFS in the latter.
