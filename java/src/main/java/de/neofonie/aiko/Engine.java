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

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import java.io.IOException;

import de.neofonie.aiko.yaml.*;

import static de.neofonie.aiko.Runner.TOTAL_TEST_COUNTER;

/**
 * This engine runs one test and retries execution if specified in the test case.
 */
public class Engine {

    /**
     * The context is used to expand the body content, if a file is referenced.
     */
    private final Context context;

    /**
     * The test case is run against this domain, e.g. http://localhost or https://some.domain:8443
     */
    private final String domain;

    /**
     * This test case is executed.
     */
    private final TestCase testCase;

    /**
     * Creates a new instance with the given arguments.
     *
     * @param context The context is used to expand the body content, if a file is referenced.
     * @param group The group is needed for the domain.
     * @param testCase This test case is executed.
     */
    public Engine(final Context context, final Group group, final TestCase testCase) {
        this.context = context;
        this.domain = group.getDomain().replaceAll("/$", "");
        this.testCase = testCase;
    }

    /**
     * Executes a test with a configured retry strategy.
     *
     * @return true - successful | false - failed
     */
    public boolean executeTest() throws IOException {
        boolean result = false;
        TOTAL_TEST_COUNTER++;

        final RetryStrategy retryStrategy = getRetryStrategy(testCase.getRetry());
        while (retryStrategy.isWithinRetryCount()) {
            retryStrategy.printRetryNumber();

            if (result = performTest()) {
                break;
            } else {
                wait(retryStrategy.getRetryDelay());
            }

            retryStrategy.increaseCurrentRetryCount();
        }

        return result;
    }

    private RetryStrategy getRetryStrategy(final Retry retry) {
        if (retry == null) {
            return new RetryStrategy();
        } else {
            return new RetryStrategy(retry.getCount(), retry.getDelay());
        }
    }

    private boolean performTest() throws IOException {
        System.out.println("\n\t" + TOTAL_TEST_COUNTER + ". Running '" + testCase.getName() + "'...");

        final RequestDefinition requestDefinition = testCase.getRequest();
        final ResponseDefinition responseDefinition = testCase.getResponse();

        if (requestDefinition == null || requestDefinition.isInvalid()) {
            System.out.println("\t[ERROR] No complete requestDefinition description found.\nRequestDefinition: " + requestDefinition);
            return false;
        }

        if (responseDefinition == null || responseDefinition.isInvalid()) {
            System.out.println("\t[ERROR] No complete response description found.\nResponseDefinition: " + responseDefinition);
            return false;
        }

        ClientResponse response = null;
        try {
            response = requestDefinition.performRequest(domain, context);

            printResponseData(response);

            if (responseDefinition.doesNotMatchResponse(response, context)) {
                return false;
            }

            System.out.println("\tSuccess");
            return true;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (ClientHandlerException che) {
                    System.out.println("Could not close connection. Reason: " + che.getMessage());
                }
            }
        }
    }

    private void printResponseData(final ClientResponse response) {
        if (response != null) {
            System.out.println("\n\t\tResponse");
            System.out.println("\t\t\tHTTP status: " + response.getStatus());
            System.out.println("\t\t\tHeaders:");
            response.getHeaders().keySet().stream().forEach((header) -> {
                response.getHeaders().get(header).stream().forEach((value) -> {
                    System.out.println("\t\t\t  " + header + " -> " + value);
                });
            });
        }
    }

    private void wait(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            System.out.println("Problem during retry wait: " + ie.getMessage());
        }
    }
}
