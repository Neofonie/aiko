package de.neofonie.aiko.yaml;
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
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.List;

/**
 * This test configuration is used as a container for the different test groups.
 */
public class TestConfiguration {

    /**
     * Test groups.
     */
    private List<Group> groups;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Returns the parsed test configuration from the given file.
     *
     * @param file yml file that contains the test configuration
     * @return parsed test configuration
     * @throws IOException
     */
    public static TestConfiguration getFromFile(final File file) throws IOException {
        try (InputStream input = new FileInputStream(file)) {
            Yaml yaml = new Yaml(TestConfiguration.getYamlConstructor());
            return yaml.loadAs(input, TestConfiguration.class);
        }
    }

    /**
     * Returns the parsed test configuration from the given file path.
     *
     * @param filePath path to a yml file that contains the test configuration
     * @return parsed test configuration
     * @throws IOException
     */
    public static TestConfiguration getFromFile(final String filePath) throws IOException {
        return getFromFile(new File(filePath));
    }

    /**
     * Returns the parsed test configuration from the given input string.
     *
     * @param input input string that contains valid yml
     * @return parsed test configuration
     */
    public static TestConfiguration getFromString(final String input) {
        Yaml yaml = new Yaml(TestConfiguration.getYamlConstructor());
        return yaml.loadAs(input, TestConfiguration.class);
    }

    private static Constructor getYamlConstructor() {
        Constructor constructor = new Constructor(TestConfiguration.class);

        TypeDescription testConfigurationTypeDescription = new TypeDescription(TestConfiguration.class);
        TypeDescription groupTypeDescription = new TypeDescription(Group.class);
        TypeDescription testCaseTypeDescription = new TypeDescription(TestCase.class);
        TypeDescription requestTypeDescription = new TypeDescription(RequestDefinition.class);
        TypeDescription responseTypeDescription = new TypeDescription(ResponseDefinition.class);
        TypeDescription retryStrategyTypeDescription = new TypeDescription(Retry.class);

        testConfigurationTypeDescription.putListPropertyType("groups", Group.class);
        groupTypeDescription.putListPropertyType("test", TestCase.class);
        testCaseTypeDescription.putListPropertyType("retry", Retry.class);
        testCaseTypeDescription.putListPropertyType("request", RequestDefinition.class);
        testCaseTypeDescription.putListPropertyType("response", ResponseDefinition.class);
        requestTypeDescription.putMapPropertyType("headers", String.class, String.class);
        responseTypeDescription.putMapPropertyType("headers", String.class, String.class);

        constructor.addTypeDescription(testConfigurationTypeDescription);
        constructor.addTypeDescription(groupTypeDescription);
        constructor.addTypeDescription(testCaseTypeDescription);
        constructor.addTypeDescription(retryStrategyTypeDescription);
        constructor.addTypeDescription(requestTypeDescription);
        constructor.addTypeDescription(responseTypeDescription);

        return constructor;
    }

    @Override
    public String toString() {
        return "TestConfiguration{" +
                "groups=" + groups +
                '}';
    }
}
