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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContextTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = TestUtil.getTestContext();
    }

    @Test
    public void shouldNotExpandBodyIfNoFileIsReferenced() throws IOException {
        final String expectedExpandedBody = "no file body";
        final String expandedBody = context.expandBodyFieldToString("no file body");

        assertThat(expandedBody).isEqualTo(expectedExpandedBody);
    }

    @Test
    public void shouldExpandBodyIfFileIsReferenced() throws IOException {
        final String expectedExpandedBody = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Leanne Graham\",\n" +
                "  \"username\": \"Bret\",\n" +
                "  \"email\": \"Sincere@april.biz\"\n" +
                "}";
        final String expandedBody = context.expandBodyFieldToString("@testdata.json");

        assertThat(expandedBody).isEqualTo(expectedExpandedBody);
    }

    @Test
    public void shouldThrowExceptionIfNoFileExistsForExpanding() throws IOException {
        assertThatThrownBy(() -> context.expandBodyField("@testdata2.json")).isInstanceOf(IOException.class);
    }
}
