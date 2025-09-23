package com.example.pdc;

/**
 * An abstraction for a single dependency between two packages. It's a concrete
 * object in the PDC domain.
 */
public interface Dependency {

	/**
	 * The source package of the dependency.
	 *
	 * @return The source package object.
	 */
	Package source();

	/**
	 * The target package of the dependency.
	 *
	 * @return The target package object.
	 */
	Package target();
}