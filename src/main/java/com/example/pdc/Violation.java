package com.example.pdc;

import java.util.Objects;

/**
 * A final object that represents a single violation of a rule. It is immutable
 * and contains all necessary information.
 */
public final class Violation implements Comparable<Violation> {
	private final String location;
	private final String message;
	private final Priority priority;

	public Violation(String location, String message, Priority priority) {
		this.location = Objects.requireNonNull(location);
		this.message = Objects.requireNonNull(message);
		this.priority = Objects.requireNonNull(priority);
	}

	public String location() {
		return this.location;
	}

	public String message() {
		return this.message;
	}

	public Priority priority() {
		return this.priority;
	}

	@Override
	public int compareTo(Violation other) {
		return Integer.compare(this.priority.ordinal(), other.priority.ordinal());
	}

	@Override
	public String toString() {
		return String.format("[%s] %s: %s", this.priority.name(), this.location, this.message);
	}
}