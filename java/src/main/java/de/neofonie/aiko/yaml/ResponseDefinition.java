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

import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.util.Map;

import de.neofonie.aiko.Context;

/**
 * A definition of a response. It has a HTTP status code. Body and headers are optional.
 */
public class ResponseDefinition {

    /**
     * Headers that should be sent with the response.
     */
    private Map<String, String> headers;

    /**
     * Optional body, can be a json-string ("{'json': 'text'}") or file reference to a file that contains
     * json ("@example.json")
     */
    private String body;

    /**
     * HTTP status code of the expected response. e.g. 200, 204, 400
     */
    private int status;

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isInvalid() {
        return status == 0;
    }

    /**
     * Compares this response with the actual response and returns the result. It checks HTTP status code, headers
     * and body for equality.
     *
     * @param response actual response
     * @param context the context is used to expand the body content, if a file is referenced.
     * @return true - the response matches the definition | false - there are differences between definition and
     * actual response
     * @throws IOException
     */
    public boolean doesNotMatchResponse(final ClientResponse response, final Context context) throws IOException {
        if (response == null) {
            System.out.println("\t[ERROR] Got no response!");
            return true;
        }

        return isOneHeaderIncorrect(response) || isStatusIncorrect(response) || isBodyIncorrect(response, context);
    }

    private boolean isStatusIncorrect(final ClientResponse response) {
        boolean statusIncorrect = false;

        if (response.getStatus() != status) {
            System.out.println("\t[ERROR] Wrong status code: Expected '" + status + "' - got '" + response.getStatus() + "'.");
            statusIncorrect = true;
        }

        return statusIncorrect;
    }

    private boolean isOneHeaderIncorrect(final ClientResponse response) {
        boolean headerIncorrect = false;

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                final String realValue = response.getHeaders().getFirst(entry.getKey());
                final String expectedValue = entry.getValue();

                if ((!expectedValue.equals(realValue))) {
                    System.out.println("\t[ERROR] Wrong content type: Expected '" + expectedValue + "' - got '" + realValue + "'.");
                    headerIncorrect = true;
                }
            }
        }

        return headerIncorrect;
    }

    private boolean isBodyIncorrect(final ClientResponse response, final Context context) throws IOException {
        final String responseBody = IOUtils.toString(response.getEntityInputStream());
        final String expectedBody = context.expandBodyField(body);
        boolean bodyIncorrect = false;

        if (expectedBody != null) {
            try {
                JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.NON_EXTENSIBLE);
            } catch (JSONException | AssertionError e) {
                System.out.println("\t\t****************************************");
                System.out.println("\t\tGot: " + responseBody);
                System.out.println("\t\t****************************************");
                System.out.println("\t\tExpected: " + expectedBody);
                System.out.println("\t\t****************************************");
                System.out.println("\t[ERROR] " + e.getMessage().replaceAll("(\\r|\\n|\\r\\n)+", "\n\t"));
                bodyIncorrect = true;
            }
        }

        return bodyIncorrect;
    }

    @Override
    public String toString() {
        return "ResponseDefinition{" +
                "headers=" + headers +
                ", body='" + body + '\'' +
                ", status=" + status +
                '}';
    }
}
