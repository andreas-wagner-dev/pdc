package com.example.pdc.rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.pdc.App;
import com.example.pdc.Package;
import com.example.pdc.Priority;
import com.example.pdc.Violation;

/**
 * A rule that checks if a package exists for every abstraction.
 */
public final class MissingPackageRule implements com.example.pdc.Rule {

	@Override
	public Collection<Violation> check(App app) throws IOException {
		final Collection<Violation> violations = new ArrayList<>();

		final List<String> packageNames = app.packages().stream().map(Package::name).collect(Collectors.toList());

		for (final Package pkg : app.packages()) {
			for (final Class<?> abstraction : pkg.abstractions()) {
				final String expectedPackage = pkg.name() + "." + abstraction.getSimpleName().toLowerCase();
				if (!packageNames.contains(expectedPackage)) {
					violations.add(new Violation(pkg.name(),
							String.format("Expected package '%s' for abstraction '%s' is missing.", expectedPackage,
									abstraction.getSimpleName()),
							Priority.HIGH));
				}
			}
		}

		return violations;
	}
}
