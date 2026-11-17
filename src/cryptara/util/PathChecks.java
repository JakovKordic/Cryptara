package cryptara.util;

import java.io.IOException;
import java.nio.file.Path;

public class PathChecks {

    public static void checkDifferent(Path input, Path output) throws IOException {
        if (input.toAbsolutePath().normalize().equals(output.toAbsolutePath().normalize())) {
            throw new IOException("Input and output files must be different.");
        }
    }
}