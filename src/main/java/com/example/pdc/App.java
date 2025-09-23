package com.example.pdc;

import java.io.IOException;
import java.util.Collection;

/**
 * An abstraction for the entire PDC application. It defines the core capability
 * of the app: to perform a check.
 */
public interface App {

	/**
	 * Performs a package design check.
	 *
	 * @return A collection of all violations found.
	 * @throws IOException If a file I/O error occurs.
	 */
	Violations check() throws IOException;

	/**
	 * 
	 * @return A collection of all packages found
	 */
	Collection<Package> packages();
}
