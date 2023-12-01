package com.yourorganization.maven_sample;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class VoidVisitorComplete {

    // private static final String SOURCE_CODE_FILE_PATH =
    //     "/u/clc5uy/Downloads/javaparser-maven/src/main/resources/TestSourceCode.java";
    
    // private static final String ERROR_LINE_NUMS_FILE_PATH =
    //     "/u/clc5uy/Downloads/javaparser-maven/src/main/resources/TestErrorLineNumOutput.txt";

    public static void main(String[] args) throws Exception {
        /* ARGS
            1. Error line num txt file folder
            2. Source code folder
        */
        File dir = new File(args[0]);
        File[] directoryListing = dir.listFiles();
        for (File child : directoryListing) {
            File error_line_nums_file = child;
            String error_line_nums_file_name = error_line_nums_file.getName();
            // System.out.println(error_line_nums_file.getName());

            /*------------------------------------------------------------------------------------------------
                SOURCE CODE PARSER
            ------------------------------------------------------------------------------------------------ */
            
            // COMPILATION UNIT
            // System.out.println(args[1] + "/" + error_line_nums_file_name.split("%")[0]);
            CompilationUnit cu = StaticJavaParser.parse(Files.newInputStream(Paths.get(args[1] + "/" + error_line_nums_file_name.split("%")[0])));

            // METHOD NAME COLLECTOR
            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);
            
            // LINE NUMBER COLLECTOR
            List<String> methodLineNumbers = new ArrayList<String>();
            VoidVisitor<List<String>> methodLineNumberCollector = new MethodLineNumberCollector();
            methodLineNumberCollector.visit(cu, methodLineNumbers);

            // ADD METHOD NAMES AND LINE NUMS TO TREEMAP
            // KEY: STARTING LINE NUMBER
            // VALUE: METHOD NAME
            TreeMap<Integer, String> methodNameLineMap = new TreeMap<Integer, String>();
            for(int i = 0; i < methodNames.size(); i++){
                // System.out.println(methodNames.get(i) + " : " + methodLineNumbers.get(i));
                methodNameLineMap.put(Integer.parseInt(methodLineNumbers.get(i)), methodNames.get(i));
            }

            // PRINT TREEMAP OF METHOD NAMES AND LINE NUMBERS
            // System.out.println("METHOD NAME AND LINE NUMBERS");
            // methodNameLineMap.entrySet().forEach(entrySet -> System.out.println(entrySet.getKey() + " : " +  entrySet.getValue()));
            // System.out.println();

            List<String> classNames = new ArrayList<String>();
            VoidVisitor<List<String>> classNameCollector = new ClassNameCollector();
            classNameCollector.visit(cu, classNames);

            List<String> classLineNums= new ArrayList<String>();
            VoidVisitor<List<String>> classLineNumberCollector = new ClassLineNumberCollector();
            classLineNumberCollector.visit(cu, classLineNums);
            
            // ADD CLASS NAMES AND LINE NUMS TO TREE MAP
            // KEY: STARTING LINE NUMBER
            // VALUE: METHOD NAME
            TreeMap<Integer, String> classNameLineMap = new TreeMap<Integer, String>();
            for(int i = 0; i < classNames.size(); i++){
                // System.out.println(classNames.get(i) + " : " + classLineNums.get(i));
                classNameLineMap.put(Integer.parseInt(classLineNums.get(i)), classNames.get(i));
            }

            // PRINT LINKED HASH MAP OF METHOD NAMES AND LINE NUMBERS
            // System.out.println("CLASS NAME AND LINE NUMBERS");
            // classNameLineMap.entrySet().forEach(entrySet -> System.out.println(entrySet.getKey() + " : " +  entrySet.getValue()));
            // System.out.println();

            /*------------------------------------------------------------------------------------------------
                HANDLING INPUT
            ------------------------------------------------------------------------------------------------ */ 
            // LIST TO HOLD LINE NUMBERS OF ERRORS FROM INPUT FILE (PMD OUTPUT)
            ArrayList<Integer> errorLineNums = new ArrayList<Integer>();
            
            // READ INPUT FILE AND ADD LINE NUMBERS TO errorLineNums
            try {
                Scanner scanner = new Scanner(error_line_nums_file);
                // System.out.println("ERROR LINE NUMBERS");
                while (scanner.hasNextLine()) {
                    int lineVal = Integer.parseInt(scanner.nextLine());
                    // System.out.println(lineVal);
                    errorLineNums.add(lineVal);
                }
                scanner.close();
                // System.out.println();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //System.out.println(errorLineNums);

            // System.out.println("CLASS NAMES AND METHODS CONTAINING ERRORS");
            List<Entry> methodsContainingErrors = new ArrayList<Entry>();
            errorLineNums.forEach(lineNum -> {
                Entry methodEntry = methodNameLineMap.floorEntry(lineNum);
                int entryKey = (int)methodEntry.getKey();
                Entry classEntry = classNameLineMap.floorEntry(entryKey);
                String className = classEntry.getValue().toString();
                System.out.println(className + ":" + methodEntry.getValue().toString());
            });
        }
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }

    private static class MethodLineNumberCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(Integer.toString(md.getRange().get().begin.line));
        }
    }

    private static class ClassNameCollector extends VoidVisitorAdapter<List<String>>{
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {
            super.visit(n, collector);
            collector.add(n.getNameAsString());
        }
    }

    private static class ClassLineNumberCollector extends VoidVisitorAdapter<List<String>>{
        
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {
            super.visit(n, collector);
            collector.add(Integer.toString(n.getRange().get().begin.line));
        }
    }
}