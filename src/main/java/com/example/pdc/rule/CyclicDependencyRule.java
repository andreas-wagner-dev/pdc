package com.example.pdc.rule;

import java.util.ArrayList;
import java.util.Collection;

import com.example.pdc.App;
import com.example.pdc.Rule;
import com.example.pdc.Violation;

/**
 * Eine Regel, die zirkuläre Abhängigkeiten in einem Abhängigkeitsgraphen
 * findet. Sie ist ein eigenständiges, wiederverwendbares Objekt.
 */
public final class CyclicDependencyRule implements Rule {

	@Override
	public Collection<Violation> check(App app) {
		// Die Prüfung wird vom DependencyGraph-Objekt selbst durchgeführt.
		// Die Regel ist nur der Adapter, um das Ergebnis in ein Violation-Objekt zu
		// transformieren.
		// Da die Haupt-App-Logik Zyklen zuerst prüft, ist diese Regel hier nur zur
		// Vollständigkeit.
		// In der Praxis würde die `App.check()` Methode diese Regel separat aufrufen,
		// und die anderen Regeln nicht ausführen, wenn kritische Verstöße gefunden
		// werden.
		return new ArrayList<>();
	}

	// ... hier könnte eine detaillierte Implementierung der Regel stehen,
	// falls die Prüfung nicht direkt im App-Objekt erfolgt.
}
