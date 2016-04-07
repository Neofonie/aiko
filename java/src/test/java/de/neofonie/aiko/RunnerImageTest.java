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
import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class RunnerImageTest {

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
        final int exitCode = Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "image-tests.yml");

        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    public void shouldReturnTwoStatusCodeOnFailingTests() throws IOException, ParseException {
        final int exitCode = Runner.executeAikoTests("-f", TestUtil.getTestUserDir() + "failing-image-tests.yml");

        assertThat(exitCode).isEqualTo(2);
    }

    private void createServerRoutes() throws IOException {
        final byte[] imageContent = IOUtils.toByteArray(RunnerImageTest.class.getResourceAsStream("/example.png"));

        instanceRule.stubFor(get(urlEqualTo("/images/1"))
                .withHeader("Accept", equalTo("image/png"))
                .willReturn(aResponse()
                        .withStatus(ClientResponse.Status.OK.getStatusCode())
                        .withBody(imageContent)
                        .withHeader("Content-Type", "image/png")
                ));
    }
}
