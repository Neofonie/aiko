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

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import de.neofonie.aiko.yaml.RequestDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequestDefinitionTest {

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(8111);

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    private Context context = TestUtil.getTestContext();
    private RequestDefinition requestDefinition;

    public RequestDefinitionTest() throws IOException {
    }

    @Before
    public void setUp() throws IOException {
        instanceRule.stubFor(get(urlEqualTo("/my/uri")).willReturn(aResponse()));
        instanceRule.stubFor(post(urlEqualTo("/my/uri")).willReturn(aResponse()));
        instanceRule.stubFor(put(urlEqualTo("/my/uri")).willReturn(aResponse()));
        instanceRule.stubFor(delete(urlEqualTo("/my/uri")).willReturn(aResponse()));

        requestDefinition = new RequestDefinition();
        requestDefinition.setUri("/my/uri");
    }

    @After
    public void tearDown() throws Exception {
        instanceRule.resetMappings();
        instanceRule.resetRequests();
        instanceRule.resetScenarios();
    }

    @Test
    public void shouldBeValidWithMethodAndURI() {
        requestDefinition.setUri("/my/uri");
        requestDefinition.setMethod("GET");

        assertThat(requestDefinition.isInvalid()).isFalse();
    }

    @Test
    public void shouldBeInvalidWithoutURI() {
        requestDefinition.setUri(null);
        requestDefinition.setMethod("GET");

        assertThat(requestDefinition.isInvalid()).isTrue();
    }

    @Test
    public void shouldBeInvalidWithoutMethod() {
        requestDefinition.setUri("/my/uri");
        requestDefinition.setMethod(null);

        assertThat(requestDefinition.isInvalid()).isTrue();
    }

    @Test
    public void shouldPerformGetRequestWithLowercaseMethod() throws IOException {
        requestDefinition.setMethod("get");
        requestDefinition.performRequest("http://localhost:8111", context);

        instanceRule.verify(1, getRequestedFor(urlEqualTo("/my/uri")));
    }

    @Test
    public void shouldPerformGetRequest() throws IOException {
        requestDefinition.setMethod("GET");
        requestDefinition.performRequest("http://localhost:8111", context);

        instanceRule.verify(1, getRequestedFor(urlEqualTo("/my/uri")));
    }

    @Test
    public void shouldPerformGetRequestWithHeader() throws IOException {
        requestDefinition.setMethod("GET");
        requestDefinition.setHeaders(ImmutableMap.of("X-Custom-Header", "true"));
        requestDefinition.performRequest("http://localhost:8111", context);

        instanceRule.verify(1, getRequestedFor(urlEqualTo("/my/uri")).withHeader("X-Custom-Header", equalTo("true")));
    }

    @Test
    public void shouldPerformGetRequestWithUnknownPath() throws IOException {
        requestDefinition.setMethod("GET");
        requestDefinition.setUri("/not/my/uri");
        ClientResponse response = requestDefinition.performRequest("http://localhost:8111", context);

        assertThat(response.getStatus()).isEqualTo(ClientResponse.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void shouldThrowExceptionWithWrongDomain() throws IOException {
        requestDefinition.setMethod("GET");

        assertThatThrownBy(() -> requestDefinition.performRequest("http://localhoster:8111", context)).isInstanceOf(ClientHandlerException.class);
    }

    @Test
    public void shouldPerformPostRequest() throws IOException {
        requestDefinition.setMethod("POST");
        requestDefinition.performRequest("http://localhost:8111", context);

        instanceRule.verify(1, postRequestedFor(urlEqualTo("/my/uri")));
    }

    @Test
    public void shouldPerformPutRequest() throws IOException {
        requestDefinition.setMethod("PUT");
        requestDefinition.performRequest("http://localhost:8111", context);

        instanceRule.verify(1, putRequestedFor(urlEqualTo("/my/uri")));
    }

    @Test
    public void shouldPerformDeleteRequest() throws IOException {
        requestDefinition.setMethod("DELETE");
        requestDefinition.performRequest("http://localhost:8111", context);

        instanceRule.verify(1, deleteRequestedFor(urlEqualTo("/my/uri")));
    }
}
