package com.example.pdc.rule;

import java.util.List;
import java.util.Objects;

/**
 * Ein Objekt, das für die Generierung von detaillierten Lösungsvorschlägen für
 * zirkuläre Abhängigkeiten zuständig ist.
 */
public final class CycleSolutionGenerator {

	private final List<String> cyclePath;

	public CycleSolutionGenerator(List<String> path) {
		this.cyclePath = Objects.requireNonNull(path);
	}

	public String getDescription() {
		return "Zirkuläre Abhängigkeit erkannt: " + String.join(" → ", this.cyclePath) + "\n  Lösungen:\n  1. "
				+ this.solution1() + "\n  2. " + this.solution2() + "\n  3. " + this.solution3();
	}

	private String solution1() {
		// Beispiel: Extrahiere eine gemeinsame Schnittstelle
		String pkg1 = this.cyclePath.get(0);
		String pkg2 = this.cyclePath.get(1);
		return String.format("Extrahieren Sie eine gemeinsame Schnittstelle aus %s und %s.", pkg1, pkg2);
	}

	private String solution2() {
		// Beispiel: Verwende Dependency Injection
		String pkg1 = this.cyclePath.get(0);
		String pkg2 = this.cyclePath.get(1);
		return String.format("Nutzen Sie Dependency Injection, um %s von der direkten Abhängigkeit zu %s zu lösen.",
				pkg1, pkg2);
	}

	private String solution3() {
		// Beispiel: Restrukturiere Pakete
		return "Restrukturieren Sie die Pakete nach Domänenkontexten, um die Abhängigkeiten aufzulösen.";
	}
}
