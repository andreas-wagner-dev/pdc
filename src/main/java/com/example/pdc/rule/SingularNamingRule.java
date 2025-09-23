package com.example.pdc.rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.pdc.App;
import com.example.pdc.Package;
import com.example.pdc.Priority;
import com.example.pdc.Violation;

/**
 * A rule that checks for plural forms in abstraction names.
 */
public final class SingularNamingRule implements com.example.pdc.Rule {

	private static final String PLURAL_SUFFIX = "s";
	private static final List<String> EXCEPTIONS = java.util.Arrays.asList("ISOS");

	@Override
	public Collection<Violation> check(App app) throws IOException {
		final Collection<Violation> violations = new ArrayList<>();

		for (final Package pkg : app.packages()) {
			for (final Class<?> abstraction : pkg.abstractions()) {
				final String name = abstraction.getSimpleName();
				if (name.endsWith(PLURAL_SUFFIX) && !EXCEPTIONS.contains(name)) {
					violations.add(new Violation(pkg.name(),
							String.format("Abstraction name '%s' is plural. Use a singular form instead.", name),
							Priority.HIGH));
				}
			}
		}

		return violations;
	}
}