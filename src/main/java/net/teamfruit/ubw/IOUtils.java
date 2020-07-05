package net.teamfruit.ubw;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
        }
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static List<String> readLines(InputStream input, Charset encoding) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, encoding);
        return readLines((Reader) reader);
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        return readLines(input, Charset.forName(encoding));
    }

    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            list.add(line);
        }

        return list;
    }
}
