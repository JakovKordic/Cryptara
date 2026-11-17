package cryptara.signature;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import cryptara.keys.KeyFiles;

public class SignatureFiles {

    public static void save(Path signatureFile, SignatureAlgorithm algorithm, byte[] signature)
            throws IOException {
        KeyFiles.write(signatureFile, Map.of(algorithm.getKeyLabel(), signature));
    }

    public static byte[] load(Path signatureFile, SignatureAlgorithm algorithm) throws IOException {
        byte[] signature = KeyFiles.read(signatureFile).get(algorithm.getKeyLabel());
        if (signature == null) {
            throw new IOException("No " + algorithm.getKeyLabel() + " signature found in "
                    + signatureFile.getFileName());
        }
        return signature;
    }
}