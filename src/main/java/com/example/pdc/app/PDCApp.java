package com.example.pdc.app;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.example.pdc.App;
import com.example.pdc.Package;
import com.example.pdc.Priority;
import com.example.pdc.Rule;
import com.example.pdc.Violation;
import com.example.pdc.Violations;
import com.example.pdc.graph.DependencyGraph;
import com.example.pdc.rule.CycleSolutionGenerator;

/**
 * An object that represents the PDC application. It is the central domain
 * object of the application.
 */
public final class PDCApp implements App {
	private final Collection<Package> allPackages;
	private final Collection<Rule> allRules;
	private final DependencyGraph dependencyGraph;

	/**
	 * Creates a PDCApp. Manual dependency injection happens here.
	 *
	 * @param packages All packages to be checked.
	 * @param rules    All rules to be applied.
	 * @param graph    The dependency graph.
	 */
	public PDCApp(Collection<Package> packages, Collection<Rule> rules, DependencyGraph graph) {
		this.allPackages = Objects.requireNonNull(packages);
		this.allRules = Objects.requireNonNull(rules);
		this.dependencyGraph = Objects.requireNonNull(graph);
	}

	@Override
	public Violations check() throws IOException {
		final Violations violations = new Violations();

		// 1. Find critical cycle errors first (highest priority).
		final Collection<java.util.List<String>> cycles = this.dependencyGraph.findCycles();
		if (!cycles.isEmpty()) {
			for (final java.util.List<String> cyclePath : cycles) {
				violations.add(new Violation(String.join(" → ", cyclePath),
						new CycleSolutionGenerator(cyclePath).getDescription(), Priority.CRITICAL));
			}
			// Abort further checks in case of critical errors.
			return violations;
		}

		// 2. Apply all other rules.
		for (final Rule rule : this.allRules) {
			violations.addAll(rule.check(this));
		}

		return violations;
	}

	/**
	 * Returns all packages of the application.
	 */
	@Override
	public Collection<Package> packages() {
		return Collections.unmodifiableCollection(this.allPackages);
	}
}
