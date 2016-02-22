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
import de.neofonie.aiko.yaml.RequestDefinition;
import de.neofonie.aiko.yaml.ResponseDefinition;
import de.neofonie.aiko.yaml.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EngineTest {

    private Group group;
    private TestCase testCase;

    @Before
    public void setUp() throws Exception {
        group = new Group();
        group.setDomain("localhost:8080");
        testCase = new TestCase();

        testCase.setRequest(new RequestDefinition());
        testCase.getRequest().setMethod("PUT");
        testCase.getRequest().setUri("/users");

        testCase.setResponse(new ResponseDefinition());
        testCase.getResponse().setStatus(200);
    }

    @Test
    public void shouldReturnFalseWithInvalidRequest() throws Exception {
        testCase.getRequest().setUri(null);
        final boolean result = new Engine(TestUtil.getTestContext(), group, testCase).executeTest();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWithNoRequest() throws Exception {
        testCase.setRequest(null);
        final boolean result = new Engine(TestUtil.getTestContext(), group, testCase).executeTest();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWithInvalidResponse() throws Exception {
        testCase.getResponse().setStatus(0);
        final boolean result = new Engine(TestUtil.getTestContext(), group, testCase).executeTest();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWithNoResponse() throws Exception {
        testCase.setResponse(null);
        final boolean result = new Engine(TestUtil.getTestContext(), group, testCase).executeTest();

        assertThat(result).isFalse();
    }
}
