package com.example.pdc.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.file.UploadedFile;

import com.example.pdc.App;
import com.example.pdc.Dependency;
import com.example.pdc.Package;
import com.example.pdc.Rule;
import com.example.pdc.Violation;
import com.example.pdc.app.PDCApp;
import com.example.pdc.graph.DependencyGraph;
import com.example.pdc.rule.MissingPackageRule;
import com.example.pdc.rule.SingularNamingRule;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Ein Objekt, das den Zustand der Benutzeroberfläche für die Analyse verwaltet.
 * Es ist die Ansicht, die eine App-Instanz verwendet, um die Ergebnisse zu
 * ermitteln.
 */
@Named("pdcView2")
@ViewScoped
public class PDCView2 implements Serializable {

	private static final long serialVersionUID = 1L;

	private TreeNode<Object> root;
	private TreeNode<Object> selectedNode;
	private List<Violation> analysisResults;

	@PostConstruct
	public void init() {
		this.root = new DefaultTreeNode<Object>("Analysator", null);
	}

	public TreeNode<Object> getRoot() {
		return this.root;
	}

	public void setSelectedNode(TreeNode<Object> selectedNode) {
		this.selectedNode = selectedNode;
	}

	public TreeNode<Object> getSelectedNode() {
		return this.selectedNode;
	}

	public List<Violation> getAnalysisResults() {
		return this.analysisResults;
	}

	/**
	 * Verarbeitet die Auswahl eines Knotens im Baum.
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		// ... (Logik zum Anzeigen von Ergebnissen für den ausgewählten Knoten)
	}

	/**
	 * Verarbeitet den Dateiupload, initialisiert die App und führt die Prüfung
	 * durch.
	 */
	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		if (uploadedFile != null) {
			try {
				Path tempPath = Files.createTempFile("uploaded-", ".tmp");
				try (InputStream input = uploadedFile.getInputStream();
						FileOutputStream output = new FileOutputStream(tempPath.toFile())) {
					IOUtils.copy(input, output);
				}
				File tempFile = tempPath.toFile();

				// Manuelle Komposition der Anwendung
				Collection<Package> packages = this.buildPackages(tempFile);
				DependencyGraph graph = this.buildDependencyGraph(packages);
				Collection<Rule> rules = this.buildRules();
				App app = new PDCApp(packages, rules, graph);

				this.root = this.buildTreeNode(packages);
				this.root.setExpanded(true);

				// App-Prüfung ausführen und Ergebnisse speichern.
				this.analysisResults = new ArrayList<>(app.check().sorted());

				tempFile.delete(); // Temporäre Datei löschen.
			} catch (IOException e) {
				// Bessere Fehlerbehandlung in der UI wäre hier sinnvoll.
				e.printStackTrace();
			}
		}
	}

	/**
	 * Baut die Package-Objekte aus der hochgeladenen Datei.
	 */
	private Collection<Package> buildPackages(File file) throws IOException {
		Map<String, Set<Class<?>>> packageClasses = new HashMap<>();
		Map<String, Set<Class<?>>> packageAbstractions = new HashMap<>();

		try (ZipFile zipFile = new ZipFile(file)) {
			java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					String className = entry.getName().replace('/', '.').replace(".class", "");
					try {
						Class<?> clazz = Class.forName(className);
						String packageName = clazz.getPackage().getName();
						packageClasses.computeIfAbsent(packageName, k -> new HashSet<>()).add(clazz);
						if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
							packageAbstractions.computeIfAbsent(packageName, k -> new HashSet<>()).add(clazz);
						}
					} catch (ClassNotFoundException ignored) {
						// ignore
					}
				}
			}
		}

		Map<String, Package> packageMap = new HashMap<>();
//		packageClasses.keySet().forEach(pkgName -> packageMap.put(pkgName, new SimplePackage(pkgName,
//				packageClasses.get(pkgName), packageAbstractions.get(pkgName), new ArrayList<>())));

		// TODO: Dependencies aufbauen
		packageMap.values().forEach(pkg -> {
			// Hier müsste die Logik zur Analyse der Abhängigkeiten der Klassen
			// in pkg.classes() stehen und die `dependencies`-Liste befüllt werden.
			// Dies ist ein komplexer Schritt, der eine tiefere Code-Analyse erfordert.
		});

		return packageMap.values();
	}

	/**
	 * Baut den Abhängigkeitsgraphen der Pakete.
	 */
	private DependencyGraph buildDependencyGraph(Collection<Package> packages) {
		Collection<Dependency> dependencies = new ArrayList<>();
		// Logik, um Abhängigkeiten zu finden und Dependency-Objekte zu erstellen, wäre
		// hier.
		// Für dieses Beispiel lassen wir dies leer, da die Komplexität der
		// statischen Codeanalyse den Rahmen sprengen würde.
		return new DependencyGraph(dependencies);
	}

	/**
	 * Baut die Regel-Objekte für die Prüfung.
	 */
	private Collection<Rule> buildRules() {
		Collection<Rule> rules = new ArrayList<>();
		rules.add(new SingularNamingRule());
		rules.add(new MissingPackageRule());
		// Weitere Regeln könnten hier hinzugefügt werden.
		return rules;
	}

	/**
	 * Baut die PrimeFaces TreeNode<Object>-Struktur aus den Package-Objekten.
	 */
	private TreeNode<Object> buildTreeNode(Collection<Package> packages) {
		Map<String, TreeNode<Object>> treeNodes = new HashMap<>();
		TreeNode<Object> rootNode = new DefaultTreeNode<Object>("root", null);

		packages.stream().sorted((p1, p2) -> p1.name().compareTo(p2.name())).forEach(pkg -> {
			String[] parts = pkg.name().split("\\.");
			TreeNode<Object> currentNode = rootNode;
			String currentPath = "";

			for (String part : parts) {
				if (currentPath.isEmpty()) {
					currentPath = part;
				} else {
					currentPath += "." + part;
				}
				if (!treeNodes.containsKey(currentPath)) {
					TreeNode<Object> newNode = new DefaultTreeNode<Object>(part, currentNode);
					treeNodes.put(currentPath, newNode);
				}
				currentNode = treeNodes.get(currentPath);
			}
		});

		return rootNode;
	}

}
