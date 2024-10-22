package org.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateCodeDetector {
  private final Map<String, String> methodHashMap = new HashMap<>(); // To store hash of method bodies and their locations

  public void scanProject(String projectRoot) throws IOException {
    SourceRoot sourceRoot = new SourceRoot(Paths.get(projectRoot));

    // Parse the codebase
    sourceRoot.tryToParse();

    // Iterate through each Java file
    for (CompilationUnit cu : sourceRoot.getCompilationUnits()) {
      // For each method in the file
      List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
      for (MethodDeclaration method : methods) {
        String methodBody = method.getBody().map(Object::toString).orElse("");

        // Create a hash or unique identifier for the method body
        String methodHash = String.valueOf(methodBody.hashCode());

        // Check for duplicates
        if (methodHashMap.containsKey(methodHash)) {
          System.out.println("Duplicate method found at " + method.getBegin().get());
          System.out.println("Original method located at " + methodHashMap.get(methodHash));
        } else {
          // Store the method location using the hash as the key
          methodHashMap.put(methodHash, method.getBegin().get().toString());
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.out.println("Please provide the path to the project root");
      return;
    }

    DuplicateCodeDetector detector = new DuplicateCodeDetector();
    detector.scanProject(args[0]); // Specify the root path of the project
  }
}