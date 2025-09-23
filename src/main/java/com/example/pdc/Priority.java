package com.example.pdc;

/**
 * Stellt die Priorität eines gefundenen Problems dar. Die Priorität spiegelt
 * die Schwere des Design-Smells wider.
 */
public enum Priority {

	CRITICAL("Critical"),

	HIGH("High"), // Schwerwiegende Verstöße, die sofort behoben werden sollten.

	MEDIUM("Medium"), // Signifikante Verstöße, die über kurz oder lang zu Problemen führen.

	LOW("Low"); // Kleinere Probleme oder "Gedankenfutter".

	private String level;

	private Priority(String level) {
		this.level = level;
	}

	public String level() {
		return level;
	}
}