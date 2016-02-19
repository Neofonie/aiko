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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.InBoundHeaders;
import de.neofonie.aiko.yaml.ResponseDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseDefinitionTest {

    private ResponseDefinition responseDefinition = new ResponseDefinition();
    private Context context = TestUtil.getTestContext();

    public ResponseDefinitionTest() throws IOException {
    }

    @Before
    public void setUp() throws Exception {
        responseDefinition.setStatus(200);
        responseDefinition.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
        responseDefinition.setBody("{\"name\":\"aiko\",\"version\":\"1.0\"}");
    }

    private ClientResponse getOkJsonResponse() {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream("{\"name\":\"aiko\",\"version\":\"1.0\"}".getBytes());
        final InBoundHeaders headers = new InBoundHeaders();
        headers.put("Content-Type", ImmutableList.of("application/json"));

        return new ClientResponse(200, headers, inputStream, null);
    }

    @Test
    public void shouldBeValidWithStatusCode() {
        responseDefinition.setStatus(200);

        assertThat(responseDefinition.isInvalid()).isFalse();
    }

    @Test
    public void shouldBeInvalidWithoutStatusCode() {
        responseDefinition.setStatus(0);

        assertThat(responseDefinition.isInvalid()).isTrue();
    }

    @Test
    public void shouldMatchExpectedResponse() throws IOException {
        final ClientResponse clientResponse = getOkJsonResponse();

        assertThat(responseDefinition.doesNotMatchResponse(clientResponse, context)).isFalse();
    }

    @Test
    public void shouldMatchExpectedResponseWithUnorderedJson() throws IOException {
        final ClientResponse clientResponse = getOkJsonResponse();

        responseDefinition.setBody("{\"version\":\"1.0\",\"name\":\"aiko\"}");

        assertThat(responseDefinition.doesNotMatchResponse(clientResponse, context)).isFalse();
    }

    @Test
    public void shouldMatchExpectedResponseWithoutHeaders() throws IOException {
        final ClientResponse clientResponse = getOkJsonResponse();

        responseDefinition.setHeaders(null);

        assertThat(responseDefinition.doesNotMatchResponse(clientResponse, context)).isFalse();
    }

    @Test
    public void shouldFailWithDifferentHeader() throws IOException {
        final ClientResponse clientResponse = getOkJsonResponse();

        responseDefinition.setHeaders(ImmutableMap.of("Content-Type", "text/html"));

        assertThat(responseDefinition.doesNotMatchResponse(clientResponse, context)).isTrue();
    }

    @Test
    public void shouldFailWithDifferentBody() throws IOException {
        final ClientResponse clientResponse = getOkJsonResponse();

        responseDefinition.setBody("{\"name\":\"aiko\",\"version\":\"10\"}");

        assertThat(responseDefinition.doesNotMatchResponse(clientResponse, context)).isTrue();
    }

    @Test
    public void shouldFailWithDifferentStatus() throws IOException {
        final ClientResponse clientResponse = getOkJsonResponse();

        responseDefinition.setStatus(201);

        assertThat(responseDefinition.doesNotMatchResponse(clientResponse, context)).isTrue();
    }
}
