package com.example.projectdnd;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom JUnit 5 extension implementing {@link TestWatcher} and {@link BeforeEachCallback}
 * to print formatted, color-coded console reports for every executed test case.
 * Tracks sequential test numbers and logs input parameters, expected outcomes, and actual results.
 */
public class CustomReportWatcher implements TestWatcher, BeforeEachCallback {

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    // מונה אוטומטי שמספר את הטסטים ברצף לאורך כל הריצה
    private static final AtomicInteger testCounter = new AtomicInteger(0);

    private static final ThreadLocal<String> currentInput = new ThreadLocal<>();
    private static final ThreadLocal<String> currentExpected = new ThreadLocal<>();
    private static final ThreadLocal<String> currentActual = new ThreadLocal<>();

    // רץ אוטומטית לפני כל טסט ומדפיס כותרת פתיחה ממוספרת
    /**
     * Invoked automatically before each test method to increment the global test counter
     * and print a cyan banner with the test number, class name, and display name.
     *
     * @param context the current JUnit extension context
     */
    @Override
    public void beforeEach(ExtensionContext context) {
        int currentNumber = testCounter.incrementAndGet();
        String className = context.getRequiredTestClass().getSimpleName();
        String testName = context.getDisplayName();

        System.out.println("\n" + CYAN + "============================================================");
        System.out.println("|  TEST #" + currentNumber + "  |  " + className + " -> " + testName);
        System.out.println("============================================================" + RESET);
    }

    /**
     * Records the input, expected output, and actual output for the currently executing test thread
     * so they can be displayed in the summary box when the test finishes.
     *
     * @param input    description of the test input or scenario
     * @param expected description of the expected result
     * @param actual   description of the actual result obtained
     */
    public static void recordDetails(Object input, Object expected, Object actual) {
        currentInput.set(String.valueOf(input));
        currentExpected.set(String.valueOf(expected));
        currentActual.set(String.valueOf(actual));
    }

    /**
     * Invoked when a test succeeds; prints a green PASSED summary box and clears thread-local state.
     *
     * @param context the current JUnit extension context
     */
    @Override
    public void testSuccessful(ExtensionContext context) {
        printBox(GREEN, "PASSED", context.getDisplayName(), null);
        clear();
    }

    /**
     * Invoked when a test fails; prints a red FAILED summary box including the failure message
     * and clears thread-local state.
     *
     * @param context the current JUnit extension context
     * @param cause   the throwable that caused the test failure
     */
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        printBox(RED, "FAILED", context.getDisplayName(), cause.getMessage());
        clear();
    }

    /**
     * Formats and prints a bordered console summary box with test status, recorded details, and optional error message.
     *
     * @param color    ANSI color escape code for the output
     * @param status   status label (e.g., "PASSED" or "FAILED")
     * @param testName display name of the executed test
     * @param errorMsg error message if the test failed, or {@code null} if it passed
     */
    private void printBox(String color, String status, String testName, String errorMsg) {
        System.out.println(color + "************************************************************");
        System.out.println("* STATUS   : " + status + " - " + testName);
        if (currentInput.get() != null) {
            System.out.println("* INPUT    : " + currentInput.get());
            System.out.println("* EXPECTED : " + currentExpected.get());
            System.out.println("* ACTUAL   : " + currentActual.get());
        }
        if (errorMsg != null) {
            System.out.println("* ERROR    : " + errorMsg);
        }
        System.out.println("************************************************************" + RESET);
    }

    /**
     * Clears the thread-local storage for input, expected, and actual test details after a test completes.
     */
    private void clear() {
        currentInput.remove();
        currentExpected.remove();
        currentActual.remove();
    }
}