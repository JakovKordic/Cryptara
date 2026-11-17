package cryptara;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

public class SymmetricFiles {

    public static void encryptFile(Path input, Path output, SecretKey key)
            throws IOException, GeneralSecurityException {
        checkDifferent(input, output);
        byte[] plaintext = Files.readAllBytes(input);
        byte[] encrypted = SymmetricCrypto.encrypt(plaintext, key);
        Files.write(output, encrypted);
    }

    public static void decryptFile(Path input, Path output, SecretKey key)
            throws IOException, GeneralSecurityException {
        checkDifferent(input, output);
        byte[] encrypted = Files.readAllBytes(input);
        byte[] plaintext = SymmetricCrypto.decrypt(encrypted, key);
        Files.write(output, plaintext);
    }

    private static void checkDifferent(Path input, Path output) throws IOException {
        if (input.toAbsolutePath().normalize().equals(output.toAbsolutePath().normalize())) {
            throw new IOException("Input and output files must be different.");
        }
    }
}