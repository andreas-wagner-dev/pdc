package com.example.pdc;

import java.util.Collection;

/**
 * An abstraction for a package in the codebase. It's a key domain object in the
 * PDC application.
 */
public interface Package {

	/**
	 * The name of the package (e.g., com.example.app).
	 *
	 * @return The package name.
	 */
	String name();

	/**
	 * The classes contained within this package.
	 *
	 * @return A collection of classes.
	 */
	Collection<Class<?>> classes();

	/**
	 * The abstractions (interfaces, abstract classes) within this package.
	 *
	 * @return A collection of abstractions.
	 */
	Collection<Class<?>> abstractions();

	/**
	 * The classes that are implementations of the package's abstractions.
	 *
	 * @param abstractionName The name of the abstraction.
	 * @return A collection of implementing classes.
	 */
	Collection<Class<?>> realizations(String abstractionName);
}
