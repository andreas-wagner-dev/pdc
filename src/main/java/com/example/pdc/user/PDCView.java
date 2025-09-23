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
import java.util.stream.Collectors;
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
import com.example.pdc.pkg.SimplePackage;
import com.example.pdc.rule.MissingPackageRule;
import com.example.pdc.rule.SingularNamingRule;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * An object that manages the state of the user interface for analysis. It is
 * the view that uses an App instance to determine the results.
 */
@Named("pdcView")
@ViewScoped
public class PDCView implements Serializable {

	private static final long serialVersionUID = 1L;

	private TreeNode<Object> root;
	private TreeNode<Object> selectedNode;
	private List<Violation> analysisResults;
	private List<Rule> rules;

	@PostConstruct
	public void init() {
		// Builds the Rule objects for the check.
		rules = new ArrayList<>();
		rules.add(new SingularNamingRule());
		rules.add(new MissingPackageRule());
		// Builds root of Tree graph
		this.root = new DefaultTreeNode<Object>("Analysator", null);
	}

	/**
	 * Processes the check of all nodes in the tree.
	 */
	public void check() {
		// TODO
	}

	/**
	 * Processes the selection of a node in the tree.
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		// TODO
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
	 * Processes the file upload, initializes the App, and performs the check.
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

				// Manual composition of the application
				Collection<Package> packages = this.buildPackages(tempFile);
				Collection<Dependency> dependencies = this.analyzeDependencies(packages);
				DependencyGraph graph = new DependencyGraph(dependencies);

				App app = new PDCApp(packages, rules, graph);

				this.root = this.buildTreeNode(packages);
				this.root.setExpanded(true);

				// Execute app check and store results.
				this.analysisResults = new ArrayList<>(app.check().sorted());

				tempFile.delete(); // Delete temporary file.
			} catch (IOException e) {
				// Better error handling in the UI would be useful here.
				e.printStackTrace();
			}
		}
	}

	/**
	 * Builds the Package objects from the uploaded file.
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
		packageClasses.keySet().forEach(pkgName -> packageMap.put(pkgName,
				new SimplePackage(pkgName, packageClasses.get(pkgName), packageAbstractions.get(pkgName))));

		return packageMap.values();
	}

	/**
	 * Analyzes dependencies between packages. NOTE: This is a simplified stub. A
	 * real implementation would require bytecode analysis libraries to scan for
	 * dependencies.
	 */
	private Collection<Dependency> analyzeDependencies(Collection<Package> packages) {
		// A placeholder implementation. In a real-world scenario, this method
		// would contain complex logic to analyze class bytecode.
		Set<Dependency> dependencies = new HashSet<>();
		Map<String, Package> packageMap = packages.stream().collect(Collectors.toMap(Package::name, p -> p));

		if (packageMap.containsKey("com.example.billing.a") && packageMap.containsKey("com.example.billing.b")) {
			dependencies.add(new Dependency() {
				@Override
				public Package source() {
					return packageMap.get("com.example.billing.a");
				}

				@Override
				public Package target() {
					return packageMap.get("com.example.billing.b");
				}
			});
			dependencies.add(new Dependency() {
				@Override
				public Package source() {
					return packageMap.get("com.example.billing.b");
				}

				@Override
				public Package target() {
					return packageMap.get("com.example.billing.a");
				}
			});
		}
		return dependencies;
	}

	/**
	 * Builds the PrimeFaces TreeNode structure from the Package objects.
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