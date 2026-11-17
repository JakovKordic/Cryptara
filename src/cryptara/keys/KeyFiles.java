package cryptara.keys;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeyFiles {

    public static void write(Path file, Map<String, byte[]> entries) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
            lines.add(entry.getKey() + "=" + Base64.getEncoder().encodeToString(entry.getValue()));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public static Map<String, byte[]> read(Path file) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator < 0) {
                throw new IOException("Invalid key file line: " + line);
            }
            String name = line.substring(0, separator);
            byte[] value;
            try {
                value = Base64.getDecoder().decode(line.substring(separator + 1));
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid Base64 in key file line: " + name, e);
            }
            entries.put(name, value);
        }
        return entries;
    }
}