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
import de.neofonie.aiko.yaml.TestConfiguration;
import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * The context is used to hold information about the context path and the test
 * configuration.
 */
public class Context {

    /**
     * The context path is used to expand the file references within the body of
     * requests / responds.
     */
    private final String contextPath;

    /**
     * The parsed test configuration.
     */
    private final TestConfiguration testConfiguration;

    /**
     * Creates a new instance with the given arguments.
     *
     * @param contextPath context path is used to expand the file references
     * within the body of requests / responds.
     * @param configurationFilePath path to the configuration file. it will be
     * parsed to get the configuration.
     * @throws IOException
     */
    public Context(final String contextPath, final String configurationFilePath) throws IOException {
        this.contextPath = contextPath;
        this.testConfiguration = TestConfiguration.getFromFile(configurationFilePath);
    }

    /**
     * Returns the file content if the given body references a file
     * ("@filename.json") otherwise it returns the given body.
     *
     * @param body body, can be a json-string ("{'json': 'text'}") or file
     * reference to a file that contains json ("@example.json"). When the
     * filename start with a "/", it is referenced from the root. Otherwise
     * the path is resolved relative to the user directory.
     * @return file content or the string itself
     * @throws IOException
     */
    public InputStream expandBodyField(final String body) throws IOException {
        InputStream result = null;
        if (body != null) {
            if (body.startsWith("@")) {
                String fileName = body.replaceAll("^@", "");                
                if (fileName != null && !fileName.isEmpty()) {
                    System.out.println("\t\tImporting file: " + fileName);
                    Path file = (fileName.startsWith("/"))
                            ? Paths.get(fileName)
                            : Paths.get(contextPath, fileName);
                    result = Files.newInputStream(file, StandardOpenOption.READ);
                }
            } else {
                result = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        return result;
    }

    public List<Group> getTestGroups() {
        return testConfiguration.getGroups();
    }

    @Override
    public String toString() {
        return "Context: Path = " + this.contextPath;
    }
}
