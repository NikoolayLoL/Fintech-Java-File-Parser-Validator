package test;

import parser.GenericFileParser;
import annotations.*;
import exceptions.ParsingException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GenericFileParserTest {

    @FileSource(delimiter = ",")
    static class TestClass {
        @Column(index = 0, name = "id")
        private int id;
        
        @Column(index = 1, name = "name")
        private String name;
        
        @Column(index = 2, name = "value")
        private double value;

        public int getId() { return id; }
        public String getName() { return name; }
        public double getValue() { return value; }
    }

    public static void main(String[] args) {
        System.out.println("=== GenericFileParser Tests ===\n");
        
        int passed = 0;
        int total = 0;
        
        total++; if (testParseValidFile()) passed++;
        total++; if (testParseEmptyLines()) passed++;
        total++; if (testParseMissingFileSourceAnnotation()) passed++;
        total++; if (testParseInvalidNumber()) passed++;
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passed + "/" + total);
        System.out.println("Coverage: " + (passed * 100 / total) + "%");
    }

    private static boolean testParseValidFile() {
        System.out.print("testParseValidFile... ");
        try {
            File tempFile = File.createTempFile("test", ".csv");
            tempFile.deleteOnExit();
            
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("1,John,100.5\n2,Jane,200.75\n3,Bob,300.25\n");
            }

            GenericFileParser<TestClass> parser = new GenericFileParser<>();
            List<TestClass> results = parser.parse(tempFile.getAbsolutePath(), TestClass.class);

            if (results.size() == 3 &&
                results.get(0).getId() == 1 &&
                results.get(0).getName().equals("John") &&
                results.get(0).getValue() == 100.5) {
                System.out.println("PASS");
                return true;
            }
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            return false;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testParseEmptyLines() {
        System.out.print("testParseEmptyLines... ");
        try {
            File tempFile = File.createTempFile("test", ".csv");
            tempFile.deleteOnExit();
            
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("1,John,100.5\n\n2,Jane,200.75\n");
            }

            GenericFileParser<TestClass> parser = new GenericFileParser<>();
            List<TestClass> results = parser.parse(tempFile.getAbsolutePath(), TestClass.class);

            if (results.size() == 2) {
                System.out.println("PASS");
                return true;
            }
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            return false;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testParseMissingFileSourceAnnotation() {
        System.out.print("testParseMissingFileSourceAnnotation... ");
        
        class NoAnnotation {
            @Column(index = 0, name = "id")
            private int id;
        }

        GenericFileParser<NoAnnotation> parser = new GenericFileParser<>();
        try {
            parser.parse("dummy.txt", NoAnnotation.class);
            System.out.println("FAIL: Should have thrown ParsingException");
            return false;
        } catch (ParsingException e) {
            System.out.println("PASS");
            return true;
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            return false;
        }
    }

    private static boolean testParseInvalidNumber() {
        System.out.print("testParseInvalidNumber... ");
        try {
            File tempFile = File.createTempFile("test", ".csv");
            tempFile.deleteOnExit();
            
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("1,John,invalid\n");
            }

            GenericFileParser<TestClass> parser = new GenericFileParser<>();
            parser.parse(tempFile.getAbsolutePath(), TestClass.class);
            System.out.println("FAIL: Should have thrown ParsingException");
            return false;
        } catch (ParsingException e) {
            System.out.println("PASS");
            return true;
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            return false;
        }
    }
}
