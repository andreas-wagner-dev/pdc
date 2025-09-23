package com.example.pdc.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.example.pdc.Graph;
import com.example.pdc.Package;

/**
 * Ein gerichteter Graph, der die Hierarchie der Java-Pakete (Namespaces) eines
 * Projekts abbildet.
 */
public final class NamespaceGraph implements Graph {
	private final Set<String> nodes;
	private final Set<String[]> edges;

	/**
	 * Erstellt einen NamespaceGraph aus einer Sammlung von Paketen.
	 *
	 * @param allPackages Eine Sammlung aller Pakete.
	 */
	public NamespaceGraph(Collection<Package> allPackages) {
		this.nodes = new HashSet<>();
		this.edges = new HashSet<>();
		this.buildGraph(allPackages);
	}

	private void buildGraph(Collection<Package> allPackages) {
		// Knoten sind die eindeutigen Paketnamen
		allPackages.forEach(pkg -> this.nodes.add(pkg.name()));

		// Kanten sind die parent-child Beziehungen der Pakete
		for (final String packageName : this.nodes) {
			String[] parts = packageName.split("\\.");
			if (parts.length > 1) {
				String parentName = String.join(".", java.util.Arrays.copyOf(parts, parts.length - 1));
				this.nodes.add(parentName); // Stellt sicher, dass der Parent-Knoten existiert
				this.edges.add(new String[] {
						parentName, packageName
				});
			}
		}
	}

	@Override
	public Collection<String> nodes() {
		return Collections.unmodifiableSet(this.nodes);
	}

	@Override
	public Collection<String[]> edges() {
		return Collections.unmodifiableSet(this.edges);
	}
}
