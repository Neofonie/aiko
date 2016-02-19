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

import org.apache.commons.lang3.Validate;

/**
 * This retry strategy ensures that all retries are executed correctly.
 */
public class RetryStrategy {

    /**
     * Retry count.
     */
    private final int retryCount;

    /**
     * Retry delay in milliseconds.
     */
    private final int retryDelay;

    /**
     * Current retry count.
     */
    private int currentRetryCount;

    /**
     * Creates a strategy with no retry.
     */
    public RetryStrategy() {
        this(0,0);
    }

    /**
     * Creates an instance with the given arguments.
     *
     * @param retryCount Retry count.
     * @param retryDelay Retry delay in milliseconds.
     */
    public RetryStrategy(final int  retryCount, final int  retryDelay) {
        Validate.isTrue(retryCount > -1, "Retry count has to be a positive number - given value %d.", retryCount);
        Validate.isTrue(retryDelay > -1, "Retry delay has to be a positive number - given value %d.", retryDelay);
        this.retryCount = retryCount;
        this.retryDelay = retryDelay;
        this.currentRetryCount = 0;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void increaseCurrentRetryCount() {
        currentRetryCount++;
    }

    public boolean isWithinRetryCount() {
        return currentRetryCount <= retryCount;
    }

    public void printRetryNumber() {
        if (currentRetryCount > 0) {
            System.out.println("\tStart with retry " + currentRetryCount + "/" + retryCount + " after " + (retryDelay * currentRetryCount) + " ms.");
        }
    }
}
