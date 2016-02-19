package de.neofonie.aiko;

import de.neofonie.aiko.yaml.TestConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationToNewFormatTest {

    @Test
    @Ignore
    public void test() throws IOException {
        final File configurationFileInOldFormat = new File(getClass().getResource("/old-format-tests.yml").getFile());
        String ymlInNewFormat = MigrationTool.convertToNewFormat(configurationFileInOldFormat);
        TestConfiguration configuration = TestConfiguration.getFromString(ymlInNewFormat);

        assertThat(configuration).isNotNull();
    }
}
