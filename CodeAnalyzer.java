import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class CodeAnalyzer {

    private static int classCount = 0;
    private static int mainCount = 0;
    private static int importCount = 0;
    private static int indentLevel = 0;
    private static int ifCount = 0;
    private static int switchCount = 0;
    private static boolean hasDefault = false;
    private static boolean inCase = false;
    private static int missingBreaks = 0;
    private static int caseIndent = 0;

    public static void main(String[] args) {
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        Scanner input = new Scanner(System.in);
        System.out.print("Enter filename: ");
        String filename = input.nextLine();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
            int lineNumber = 1;
            int emptyLines = 0;
            String currentLine;

            while ((currentLine = fileReader.readLine()) != null) {
                if (currentLine.isEmpty() || currentLine.matches("^\\s*//.*")) {
                    emptyLines++;
                } else {
                    analyzeLine(currentLine, lineNumber + emptyLines);
                    lineNumber++;
                }
                System.out.println(currentLine);
            }

            checkFinalErrors();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void analyzeLine(String line, int lineNum) {
        checkLineLength(line, lineNum);

        if (line.trim().endsWith("}")) {
            handleClosingBrace();
        }

        if (line.matches("\\s*import\\s+.*")) {
            checkImport(line, lineNum);
        } else if (line.matches("\\s*class\\s+\\w+.*")) {
            checkClass(line, lineNum);
        } else if (line.matches("\\s*public\\s+static\\s+void\\s+main.*")) {
            checkMainMethod(line, lineNum);
        } else if (line.matches("\\s*if\\s*\\(.*")) {
            checkIfStatement(line, lineNum);
        } else if (line.matches("\\s*switch\\s*\\(.*")) {
            checkSwitchStatement(line, lineNum);
        } else if (line.matches("\\s*case\\s+.*") || line.matches("\\s*default\\s*:.*")) {
            checkCaseStatement(line, lineNum);
        } else if (line.matches("\\s*for\\s*\\(.*") || line.matches("\\s*while\\s*\\(.*")) {
            checkLoop(line, lineNum);
        }

        verifyIndentation(line, lineNum);

        if (line.contains("{") || line.matches("\\s*case\\s+.*") || line.matches("\\s*default\\s*:.*")) {
            indentLevel += 4;
        }
    }

    private static void checkLineLength(String line, int lineNum) {
        if (line.length() > 80) {
            System.out.println("\033[33mLine " + lineNum + " exceeds 80 characters");
            suggestLineBreak(line, lineNum);
        }
    }

    private static void suggestLineBreak(String longLine, int lineNum) {
        // Implementation for breaking long lines
    }

    private static void handleClosingBrace() {
        if (inCase && caseIndent == indentLevel - 4) {
            indentLevel -= 4;
            inCase = false;
        }
        indentLevel -= 4;
    }

    private static void checkImport(String line, int lineNum) {
        importCount++;
        if (!line.endsWith(";")) {
            System.out.println("\033[31mMissing semicolon in import at line " + lineNum);
        }
    }

    private static void checkClass(String line, int lineNum) {
        classCount++;
        String className = line.split("\\s+")[1].split("[{]")[0].trim();
        if (!className.matches("[A-Z][a-zA-Z0-9]*")) {
            System.out.println("\033[31mInvalid class name at line " + lineNum);
        }
    }

    private static void checkMainMethod(String line, int lineNum) {
        mainCount++;
        if (!line.matches("\\s*public\\s+static\\s+void\\s+main\\s*\\(String\\[\\]\\s+args\\s*\\)\\s*\\{?")) {
            System.out.println("\033[31mInvalid main method format at line " + lineNum);
        }
    }

    private static void checkIfStatement(String line, int lineNum) {
        ifCount++;
        if (!line.matches("\\s*if\\s*\\(.*\\)\\s*\\{?")) {
            System.out.println("\033[31mInvalid if statement format at line " + lineNum);
        }
    }

    private static void checkSwitchStatement(String line, int lineNum) {
        switchCount++;
        hasDefault = true;
        if (!line.matches("\\s*switch\\s*\\(.*\\)\\s*\\{?")) {
            System.out.println("\033[31mInvalid switch statement format at line " + lineNum);
        }
    }

    private static void checkCaseStatement(String line, int lineNum) {
        caseIndent = line.length() - line.trim().length();
        if (missingBreaks > 0) {
            System.out.println("\033[31mMissing break in previous case at line " + (lineNum - 1));
        }

        inCase = true;
        missingBreaks++;

        if (switchCount <= 0) {
            System.out.println("\033[31mCase without switch at line " + lineNum);
        }

        if (line.matches("\\s*default\\s*:.*")) {
            hasDefault = false;
            switchCount--;
        }
    }

    private static void checkLoop(String line, int lineNum) {
        if (line.contains("for") && !line.matches("\\s*for\\s*\\(.*;.*;.*\\)\\s*\\{?")) {
            System.out.println("\033[31mInvalid for loop format at line " + lineNum);
        } else if (line.contains("while") && !line.matches("\\s*while\\s*\\(.*\\)\\s*\\{?")) {
            System.out.println("\033[31mInvalid while loop format at line " + lineNum);
        }
    }

    private static void verifyIndentation(String line, int lineNum) {
        int expectedSpaces = indentLevel;
        int actualSpaces = line.length() - line.trim().length();

        if (actualSpaces != expectedSpaces) {
            System.out.println("\033[31mIncorrect indentation at line " + lineNum +
                    " (expected " + expectedSpaces + " spaces, found " + actualSpaces + ")");
        }
    }

    private static void checkFinalErrors() {
        if (classCount == 0) {
            System.out.println("\033[31mNo class found in file");
        }
        if (mainCount == 0) {
            System.out.println("\033[31mNo main method found");
        }
        if (indentLevel != 0) {
            System.out.println("\033[31mUnbalanced braces in file");
        }
    }
}