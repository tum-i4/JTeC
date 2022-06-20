package de.junit4project;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Printer {
    public static void printInputStream(final InputStream is) {
        try (final InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void printBytes(final byte[] bytes) {
        System.out.println(new String(bytes));
    }
}
