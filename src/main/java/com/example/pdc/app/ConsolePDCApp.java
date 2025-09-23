package com.example.pdc.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Objects;

import com.example.pdc.App;
import com.example.pdc.Package;
import com.example.pdc.Violation;
import com.example.pdc.Violations;

/**
 * A decorator for the PDCApp that prints the check results to a console. This
 * object adds logging functionality.
 */
public final class ConsolePDCApp implements App {
	private final App origin;
	private final PrintStream out;

	public ConsolePDCApp(App app, OutputStream outputStream) {
		this.origin = Objects.requireNonNull(app);
		this.out = new PrintStream(Objects.requireNonNull(outputStream));
	}

	@Override
	public Violations check() throws IOException {
		this.out.println("Starting Package Design Check...");
		final Violations violations = this.origin.check();

		if (violations.isEmpty()) {
			this.out.println("Check passed. No violations found.");
		} else {
			this.out.println("Found " + violations.size() + " violations:");
			for (final Violation violation : violations.sorted()) {
				this.out.println(violation.toString());
			}
		}

		this.out.println("PDC finished.");
		return violations;
	}

	@Override
	public Collection<Package> packages() {
		// TODO Auto-generated method stub
		return null;
	}
}
