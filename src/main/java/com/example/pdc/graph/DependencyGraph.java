package com.example.pdc.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.pdc.Dependency;
import com.example.pdc.Graph;

/**
 * Represents a directed graph that maps the dependencies between packages or
 * classes. This implementation finds circular dependencies.
 */
public final class DependencyGraph implements Graph {
	private final Map<String, Set<String>> adjacencyList;

	/**
	 * Constructs a DependencyGraph from a collection of Dependency objects. This is
	 * the preferred object-oriented way to build the graph.
	 *
	 * @param dependencies The collection of dependencies.
	 */
	public DependencyGraph(Collection<Dependency> dependencies) {
		this.adjacencyList = new HashMap<>();
		Objects.requireNonNull(dependencies).forEach(dep -> {
			this.adjacencyList.computeIfAbsent(dep.source().name(), k -> new HashSet<>()).add(dep.target().name());
			this.adjacencyList.computeIfAbsent(dep.target().name(), k -> new HashSet<>());
		});
	}

	/**
	 * Finds all circular dependencies in the graph. Uses a recursive depth-first
	 * search (DFS) algorithm.
	 *
	 * @return A collection of cycles, where each cycle is a list of node IDs.
	 */
	public Collection<List<String>> findCycles() {
		final Set<String> visiting = new HashSet<>();
		final Set<String> visited = new HashSet<>();
		final Collection<List<String>> cycles = new ArrayList<>();

		for (final String node : this.adjacencyList.keySet()) {
			if (!visited.contains(node)) {
				this.dfs(node, visiting, visited, new ArrayList<>(), cycles);
			}
		}
		return cycles;
	}

	private void dfs(String node, Set<String> visiting, Set<String> visited, List<String> path,
			Collection<List<String>> cycles) {
		visiting.add(node);
		path.add(node);

		for (final String neighbor : this.adjacencyList.getOrDefault(node, Collections.emptySet())) {
			if (visiting.contains(neighbor)) {
				final int cycleStart = path.indexOf(neighbor);
				final List<String> cycle = new ArrayList<>(path.subList(cycleStart, path.size()));
				cycle.add(neighbor);
				cycles.add(cycle);
			} else if (!visited.contains(neighbor)) {
				this.dfs(neighbor, visiting, visited, path, cycles);
			}
		}
		visiting.remove(node);
		visited.add(node);
		path.remove(path.size() - 1);
	}

	@Override
	public Collection<String> nodes() {
		return Collections.unmodifiableSet(this.adjacencyList.keySet());
	}

	@Override
	public Collection<String[]> edges() {
		return this.adjacencyList.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(target -> new String[] {
						entry.getKey(), target
				})).collect(Collectors.toList());
	}
}
