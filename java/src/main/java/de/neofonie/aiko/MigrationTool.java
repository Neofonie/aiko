package de.neofonie.aiko;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MigrationTool {

    public static void main(String... args) throws IOException {
        final File ymlDirectory = new File("/home/muecke/Entwicklung/Projekte/app-drug-service/aiko/tests");

        for (final File oldConfigFile : ymlDirectory.listFiles()) {
            if (oldConfigFile.isFile() && oldConfigFile.getName().endsWith("yml")) {
                System.out.println("Convert '" + oldConfigFile.getName() + "' to new formgat...");
                String newFormat = convertToNewFormat(oldConfigFile);
                final FileOutputStream outputStream = new FileOutputStream(oldConfigFile, false);
                newFormat = newFormat.replaceAll("(.*)\\- name", "\n$1- name");

                final byte[] myBytes = newFormat.getBytes();

                outputStream.write(myBytes);
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static String convertToNewFormat(File configurationFileInOldFormat) throws IOException {
        try (InputStream input = new FileInputStream(configurationFileInOldFormat)) {
            Yaml yaml = new Yaml();
            List<Map<String, Object>> oldYml = (List<Map<String, Object>>) yaml.load(input);
            List<Map<String, Object>> newGroups = new ArrayList<>();

            for (Map<String, Object> group : oldYml) {
                List<Map> groupFields = (List<Map>) group.get("group");
                Map<String, Object> newGroup = getNewGroupFormat(groupFields);
                newGroups.add(newGroup);
            }

            Map<String, List> config = new LinkedHashMap<>();
            config.put("groups", newGroups);

            return createYmlString(config);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getNewGroupFormat(List<Map> groupFields) {
        Map<String, Object> newGroup = new LinkedHashMap<>();
        List<Map<String, Object>> newTests = new ArrayList<>();

        for (Map groupField : groupFields) {
            if (groupField.containsKey("name")) {
                newGroup.put("name", groupField.get("name"));
            } else if (groupField.containsKey("domain")) {
                newGroup.put("domain", groupField.get("domain"));
            } else if (groupField.containsKey("test")) {
                for (Map<String, Object> testCase : ((List<Map<String, Object>>) groupField.get("test"))) {
                    newTests.add(getNewTestCaseFormat(testCase));
                }
            }
        }

        newGroup.put("tests", newTests);

        return newGroup;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getNewTestCaseFormat(Map<String, Object> testCase) {
        String name = testCase.get("name").toString();
        String method = testCase.get("operation").toString();
        String uri = testCase.get("uri").toString();
        List<Map> request = (List<Map>) testCase.get("request");
        List<Map> response = (List<Map>) testCase.get("response");
        List<Map> retry = (List<Map>) testCase.get("strategy");

        Map<String, Object> newTestCase = new LinkedHashMap<>();
        Map<String, Object> newRequest = getNewRequestFormat(request, method, uri);
        Map<String, Object> newResponse = getNewResponseFormat(response);

        newTestCase.put("name", name);

        if (retry != null) {
            Map<String, Object> newRetry = getNewRetryFormat(retry);
            newTestCase.put("retry", newRetry);
        }

        newTestCase.put("request", newRequest);
        newTestCase.put("response", newResponse);

        return newTestCase;
    }

    private static Map<String, Object> getNewRetryFormat(List<Map> retryFields) {
        final Map<String, Object> retry = new LinkedHashMap<>();

        for (Map field : retryFields) {
            if (field.containsKey("retry")) {
                retry.put("count", field.get("retry"));
            } else if (field.containsKey("retry-delay")) {
                retry.put("delay", field.get("retry-delay"));
            }
        }

        return retry;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getNewResponseFormat(List<Map> responseFields) {
        final Map<String, Object> response = new LinkedHashMap<>();

        for (Map field : responseFields) {
            if (field.containsKey("status")) {
                response.put("status", field.get("status"));
            } else if (field.containsKey("body")) {
                response.put("body", field.get("body"));
            } else if (field.containsKey("headers")) {
                response.put("headers", getNewHeaderFormat((List<Map<String, String>>) field.get("headers")));
            }
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getNewRequestFormat(List<Map> requestFields, String method, String uri) {
        final Map<String, Object> request = new LinkedHashMap<>();

        request.put("method", method);
        request.put("uri", uri);

        for (Map field : requestFields) {
            if (field.containsKey("body")) {
                request.put("body", field.get("body"));
            } else if (field.containsKey("headers")) {
                request.put("headers", getNewHeaderFormat((List<Map<String, String>>) field.get("headers")));
            }
        }

        return request;
    }

    private static Map<String, String> getNewHeaderFormat(List<Map<String, String>> oldHeaders) {
        Map<String, String> headers = new LinkedHashMap<>();

        for (Map<String, String> header : oldHeaders) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headers.put(entry.getKey(), entry.getValue());
            }
        }

        return headers;
    }

    private static String createYmlString(Map<String, List> config) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setWidth(240);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        return new Yaml(dumperOptions).dump(config);
    }
}
