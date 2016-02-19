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

import java.io.IOException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Entry point of the application.
 */
public class Aiko {

    public final static Options AIKO_OPTIONS = getOptions();

    private final static String HEADER = "Neofonie Aiko - Test your REST interface";

    public static void main(final String... args) {
        System.out.println(HEADER);

        try {
            final int exitCode = Runner.start(args);
            System.exit(exitCode);
        } catch (ParseException e) {
            HelpFormatter showHelp = new HelpFormatter();            
            showHelp.printHelp("java -jar aiko.jar ", "Neofonie Aiko", AIKO_OPTIONS, "");
            System.out.println("Parse Error: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption(Option.builder("f").required().hasArg().desc("the YAML file to test").build());

        return options;
    }
}
