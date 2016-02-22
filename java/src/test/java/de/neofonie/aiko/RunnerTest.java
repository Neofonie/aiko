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
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RunnerTest {

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(8111);

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    private String realUserDir;

    @Before
    public void setUp() throws IOException {
        createServerRoutes();

        realUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", TestUtil.getTestUserDir());
    }

    @After
    public void tearDown() throws Exception {
        System.setProperty("user.dir", realUserDir);
    }

    @Test
    public void shouldReturnZeroStatusCodeOnSuccessfulTests() throws IOException, ParseException {
        final int exitCode = Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "all-methods-tests.yml");

        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    public void shouldReturnTwoStatusCodeOnFailedTests() throws IOException, ParseException {
        final int exitCode = Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "fail-tests.yml");

        assertThat(exitCode).isEqualTo(2);
    }

    @Test
    public void shouldReturnTwoStatusCodeOnNotFoundBodyFileTests() throws IOException, ParseException {
        final int exitCode = Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "body-not-found-tests.yml");

        assertThat(exitCode).isEqualTo(2);
    }

    @Test
    public void shouldRetryFailingTest() throws IOException, ParseException {
        final long startTimeMillis = System.currentTimeMillis();
        final int exitCode = Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "retry-tests.yml");
        final long durationMillis = System.currentTimeMillis() - startTimeMillis;
        final long expectedDuration = 3000; //(one normal try + three retry) * retry delay

        assertThat(exitCode).isEqualTo(2);
        assertThat(durationMillis).isGreaterThan(expectedDuration);
    }

    @Test
    public void shouldThrowExceptionWithoutArgs() throws IOException, ParseException {
        final int exitCode = Runner.executeAikoTests();

        assertThat(exitCode).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionWithUnknownConfig() throws IOException, ParseException {
        final int exitCode = Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "unknown-tests.yml");

        assertThat(exitCode).isEqualTo(2);
    }

    @Test
    public void shouldThrowExceptionUnreachableServer() throws IOException, ParseException {
        assertThatThrownBy(() -> Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "wrong-domain-tests.yml"))
                .isInstanceOf(ClientHandlerException.class);
    }

    private void createServerRoutes() {
        instanceRule.stubFor(get(urlEqualTo("/users/1"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(ClientResponse.Status.OK.getStatusCode())
                        .withBody("{\n" +
                                "                 \"id\": 1,\n" +
                                "                 \"name\": \"Leanne Graham\",\n" +
                                "                 \"username\": \"Bret\",\n" +
                                "                 \"email\": \"Sincere@april.biz\"\n" +
                                "               }")
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                ));
        instanceRule.stubFor(post(urlEqualTo("/users"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Leanne Graham\",\n" +
                        "  \"username\": \"Bret\",\n" +
                        "  \"email\": \"Sincere@april.biz\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(ClientResponse.Status.CREATED.getStatusCode())
                        .withBody("{\n" +
                                "                 \"id\": 1,\n" +
                                "                 \"name\": \"Leanne Graham\",\n" +
                                "                 \"username\": \"Bret\",\n" +
                                "                 \"email\": \"Sincere@april.biz\"\n" +
                                "               }")
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                ));
        instanceRule.stubFor(put(urlEqualTo("/users/1"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Leanne Graham\",\n" +
                        "  \"username\": \"Bret\",\n" +
                        "  \"email\": \"Sincere@april.biz\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(ClientResponse.Status.OK.getStatusCode())
                        .withBody("{\n" +
                                "                 \"id\": 1,\n" +
                                "                 \"name\": \"Leanne Graham\",\n" +
                                "                 \"username\": \"Bret\",\n" +
                                "                 \"email\": \"Sincere@april.biz\"\n" +
                                "               }")
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                ));
        instanceRule.stubFor(delete(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(ClientResponse.Status.NO_CONTENT.getStatusCode())
                ));
    }
}
