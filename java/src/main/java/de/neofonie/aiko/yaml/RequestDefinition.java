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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.neofonie.aiko.Context;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * A definition of a request. It defines method and uri. Headers and body are optional.
 */
public class RequestDefinition {

    /**
     * Optional headers that should be used with this request.
     */
    private Map<String, String> headers;

    /**
     * Optional body, can be a json-string ("{'json': 'text'}") or file reference to a file that contains
     * json ("@example.json")
     */
    private String body;

    /**
     * HTTP method of the request. e.g. "GET", "POST", "PUT", "DELETE"
     */
    private String method;

    /**
     * The request is sent to this uri. e.g. "/users"
     */
    private String uri;

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isInvalid() {
        return StringUtils.isEmpty(method) || StringUtils.isEmpty(uri);
    }

    /**
     * Performs the request and returns the response.
     *
     * @param domain the request goes to this domain
     * @param context the context is used to expand the body content, if a file is referenced.
     * @return response of the request
     * @throws IOException
     */
    public ClientResponse performRequest(final String domain, final Context context) throws IOException {
        final WebResource webResource = getWebResource(createClient(), domain);
        final WebResource.Builder requestBuilder = webResource.getRequestBuilder();
        final InputStream requestBody = context.expandBodyField(body);
        final String upperCaseMethod = method.toUpperCase();
        addHeaders(requestBuilder);

        final ClientResponse response;

        if (requestBody != null) {
            response = requestBuilder.entity(requestBody).method(upperCaseMethod, ClientResponse.class, requestBody);
        } else {
            response = requestBuilder.method(upperCaseMethod, ClientResponse.class);
        }

        return response;
    }

    private Client createClient() {
        Client client = Client.create();
        client.setConnectTimeout(60_000);
        client.setReadTimeout(60_000);

        return client;
    }

    private WebResource getWebResource(final Client client, final String domain) {
        String path = getUri().replaceAll("/$", "");
        URI uri = URI.create(domain + path);

        System.out.println("\t\t" + method + " " + uri.toASCIIString());

        return client.resource(uri);
    }

    private void addHeaders(WebResource.Builder requestBuilder) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
                System.out.println("\t\t  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return "RequestDefinition{" +
                "headers=" + headers +
                ", body='" + body + '\'' +
                ", method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
