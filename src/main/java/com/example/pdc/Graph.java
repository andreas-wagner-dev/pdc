package com.example.pdc;

/**
* An abstraction for a graph.
* Defines the fundamental operations to represent the structure
* of a graph.
*/
import java.util.Collection;

public interface Graph {

	/**
	 * Returns all nodes of the graph.
	 *
	 * @return A collection of node IDs (e.g., strings).
	 */
	Collection<String> nodes();

	/**
	 * Returns all edges of the graph. An edge is a pair of node IDs.
	 *
	 * @return A collection of edges as a string array.
	 */
	Collection<String[]> edges();
}
