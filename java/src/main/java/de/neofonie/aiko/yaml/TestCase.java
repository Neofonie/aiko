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
package de.neofonie.aiko.yaml;

/**
 * A test case has a name, request definition and response definition definition. If a retry is defined, it will be used to retry failed test.
 */
public class TestCase {

    /**
     * Name of the test case.
     */
    private String name;

    /**
     * Optional retry, that is used for failed tests.
     */
    private Retry retry;

    /**
     * This request definition for a request that is performed during the test phase.
     */
    private RequestDefinition request;

    /**
     * This response definition describes the expected result of the performed request.
     */
    private ResponseDefinition response;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public RequestDefinition getRequest() {
        return request;
    }

    public void setRequest(RequestDefinition request) {
        this.request = request;
    }

    public ResponseDefinition getResponse() {
        return response;
    }

    public void setResponse(ResponseDefinition response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "name='" + name + '\'' +
                ", retry=" + retry +
                ", request=" + request +
                ", response=" + response +
                '}';
    }
}
