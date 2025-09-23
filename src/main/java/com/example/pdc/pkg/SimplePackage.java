package com.example.pdc.pkg;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.pdc.Package;

/**
 * An immutable object that represents a simple, non-recursive package.
 */
public final class SimplePackage implements Package {
	private final String name;
	private final Set<Class<?>> classes;
	private final Set<Class<?>> abstractions;

	public SimplePackage(String name, Set<Class<?>> classes, Set<Class<?>> abstractions) {
		this.name = Objects.requireNonNull(name);
		this.classes = Objects.requireNonNull(classes);
		this.abstractions = Objects.requireNonNull(abstractions);
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public Collection<Class<?>> classes() {
		return Collections.unmodifiableSet(this.classes);
	}

	@Override
	public Collection<Class<?>> abstractions() {
		return Collections.unmodifiableSet(this.abstractions);
	}

	@Override
	public Collection<Class<?>> realizations(String abstractionName) {
		return this.classes.stream()
				.filter(c -> !c.isInterface() && !java.lang.reflect.Modifier.isAbstract(c.getModifiers())
						&& c.getSuperclass() != null && c.getSuperclass().getSimpleName().equals(abstractionName)
						|| java.util.Arrays.stream(c.getInterfaces())
								.anyMatch(i -> i.getSimpleName().equals(abstractionName)))
				.collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SimplePackage that = (SimplePackage) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
