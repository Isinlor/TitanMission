/**
 * Simple unit test framework.
 *
 * Your test class should extend this class.
 * Then you can write your own tests in the main method of your class.
 * A single test should look like this:
 * it("should do this (explanation of what you are testing)", () -> {
 *     boolean testCondition = true;
 *     assertTrue(testCondition, "explanation of your assertion");
 * });
 *
 * Good luck!
 *
 * @author Tomasz Darmetko
 */
abstract class SimpleUnitTest {

    /**
     * This method is just a placeholder explaining to you what to do.
     * You should overwrite it in your own test.
     *
     * @param args No arguments foreseen.
     */
    public static void main(String[] args) {

        it("should do this (explanation of what you are testing)", () -> {
            boolean testCondition = true;
            assertTrue(testCondition, "explanation of your assertion");
        });

        System.out.print("\n\n" +
            "The line above is an example of a running test.\n" +
            "Your test class should extend SimpleUnitTest class.\n" +
            "Then you can write your own tests in the main method.\n" +
            "A single test should look like this:\n" +
            "it(\"should do this (explanation of what you are testing)\", () -> {\n" +
            "    boolean testCondition = true;\n" +
            "    assertTrue(testCondition, \"explanation of your assertion\");\n" +
            "});"
        );

    }

    /**
     * This method allows to display that a single test scenario succeeded or failed.
     *
     * @param expectation here you can explain what is your test testing
     * @param runnable this runnable will be executed; if it throws exception you will be notified
     */
    protected static void it(String expectation, Runnable runnable) {

        try {

            runnable.run();
            System.out.println("✔ It " + expectation);

        } catch(AssertionFailed e) {

            System.out.println("✘ It " + expectation);
            System.out.println();

            if(e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("Assertion failed!");
                e.printStackTrace();
            }

            System.out.println();
            System.exit(-1);

        } catch(Exception e) {

            System.out.println("✘ It " + expectation);
            System.out.println();
            System.out.println("Exception thrown: ");
            e.printStackTrace();
            System.exit(-1);

        }

    }

    /**
     * This method allows to assert that certain condition is true.
     *
     * @param condition The condition that must be true.
     * @param explanation Explanation of the assertion.
     *
     * @throws AssertionFailed This exception with explanation of the assertion is thrown if condition is false.
     */
    protected static void assertTrue(boolean condition, String explanation) throws RuntimeException {
        if(!condition) throw new AssertionFailed("Assertion failed: \n" + explanation);
    }

    /**
     * This method allows to assert that certain condition is true.
     *
     * @param condition The condition that must be true.
     *
     * @throws AssertionFailed This exception is thrown if condition is false.
     */
    protected static void assertTrue(boolean condition) throws RuntimeException {
        if(!condition) throw new AssertionFailed();
    }

    /**
     * Exception indicating failed assertion.
     */
    static private class AssertionFailed extends RuntimeException {
        public AssertionFailed() {
        }
        public AssertionFailed(String s) {
            super(s);
        }
    }

}