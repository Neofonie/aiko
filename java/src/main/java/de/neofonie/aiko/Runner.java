/*
The MIT License (MIT)

Copyright (c) 2016 Neofonie GmbH

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package de.neofonie.aiko;

import de.neofonie.aiko.yaml.Group;
import de.neofonie.aiko.yaml.TestCase;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * This runner executes all test from the test configuration.
 */
public class Runner {

    private final static Options AIKO_OPTIONS = getOptions();
    private final static String HEADER = "Neofonie Aiko - Test your REST interface";

    /**
     * Total number of tests run.
     */
    public static int TOTAL_TEST_COUNTER = 0;

    /**
     * Number of failed tests.
     */
    public static int FAILED_TEST_COUNTER = 0;

    /**
     * Starts all tests and returns an exit code. If all tests were successful 0 is returned.
     * If the arguments where not parseable it returns 1. If one or more tests failed or an exception occurred it
     * returns 2.
     *
     * @param args command line arguments that specify the configuration file
     * @return 0 - all tests successful | 1 - args not parseable | 2 - at least one test failed or exception
     */
    public static int executeAikoTests(final String... args) {
        System.out.println(HEADER);

        try {
            return startTests(args);
        } catch (ParseException e) {
            HelpFormatter showHelp = new HelpFormatter();
            showHelp.printHelp("java -jar aiko.jar ", "Neofonie Aiko", AIKO_OPTIONS, "");
            System.out.println("Parse Error: " + e.getMessage());
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Parses the given arguments, starts all tests and returns an exit code. If all tests were successful 0 is returned
     * otherwise 2.
     *
     * @param args command line arguments that specify the configuration file
     * @return 0 - all tests successful | 2 - at least one test failed
     * @throws ParseException
     * @throws IOException
     */
    private static int startTests(final String... args) throws ParseException, IOException {
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(AIKO_OPTIONS, args);
        final String configurationFile = cmd.getOptionValue("f");
        final Context context = new Context(System.getProperty("user.dir"), configurationFile);
        int exitCode = 0;

        System.out.println(context);
        System.out.println("Starting tests");
        if (!Runner.runAllTests(context)) {
            exitCode = 2;
        }

        return exitCode;
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption(Option.builder("f").required().hasArg().desc("the YAML file to test").build());

        return options;
    }

    private static boolean runAllTests(final Context context) {
        boolean result = true;

        for (Group group : context.getTestGroups()) {
            System.out.print("Group: '" + group.getName() + "': \n");

            try {
                result = runTestsInGroup(context, group);
            } catch (IOException e) {
                System.out.println("[ERROR] Test execution failed. Reason: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            System.out.println("\n");
        }

        if (FAILED_TEST_COUNTER > 0) {
            printFailedTestCounter();
        }

        return result;
    }

    private static void printFailedTestCounter() {
        System.out.println("\n\n");
        System.out.println("***********************");
        System.out.println("[ERROR] " + FAILED_TEST_COUNTER + " Test(s) failed.");
        System.out.println("***********************");
    }

    private static boolean runTestsInGroup(final Context context, final Group group) throws IOException {
        boolean result = true;

        for (TestCase test : group.getTests()) {
            if (!(new Engine(context, group, test)).executeTest()) {
                result = false;
                FAILED_TEST_COUNTER++;
                break;
            }
        }

        return result;
    }
}
