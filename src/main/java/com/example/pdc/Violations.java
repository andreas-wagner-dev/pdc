package com.example.pdc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A final object that represents a collection of rule violations. It ensures
 * immutability and provides a sorted view.
 */
public final class Violations implements Iterable<Violation> {

	private final List<Violation> violations;

	public Violations() {
		this(new ArrayList<>());
	}

	public Violations(Collection<Violation> violations) {
		this.violations = new ArrayList<>(Objects.requireNonNull(violations));
	}

	public void add(Violation violation) {
		this.violations.add(Objects.requireNonNull(violation));
	}

	public void addAll(Collection<Violation> newViolations) {
		this.violations.addAll(Objects.requireNonNull(newViolations));
	}

	public Collection<Violation> sorted() {
		Collections.sort(this.violations);
		return Collections.unmodifiableList(this.violations);
	}

	@Override
	public Iterator<Violation> iterator() {
		return this.violations.iterator();
	}

	public boolean isEmpty() {
		return this.violations.isEmpty();
	}

	public int size() {
		return this.violations.size();
	}
}
