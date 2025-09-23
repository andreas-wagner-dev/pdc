package com.example.pdc;

import java.io.IOException;
import java.util.Collection;

/**
 * An abstraction for a single rule to be applied during the check. Each rule
 * has one, and only one, reason to change.
 */
public interface Rule {

	/**
	 * Checks if the given application instance violates this rule.
	 *
	 * @param app The application instance to check.
	 * @return A collection of violations.
	 * @throws IOException If an I/O error occurs.
	 */
	Collection<Violation> check(App app) throws IOException;
}
