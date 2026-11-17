package cryptara.hash;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class FileHash {

    private static final String ALGORITHM = "SHA-256";
    private static final int BUFFER_SIZE = 8192;

    public static String compute(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    public static String save(Path file, Path hashFile) throws IOException, NoSuchAlgorithmException {
        String hash = compute(file);
        Files.writeString(hashFile, hash + "  " + file.getFileName() + System.lineSeparator());
        return hash;
    }

    public static boolean verify(Path file, Path hashFile) throws IOException, NoSuchAlgorithmException {
        String storedHash = readStoredHash(hashFile);
        return compute(file).equalsIgnoreCase(storedHash);
    }

    private static String readStoredHash(Path hashFile) throws IOException {
        String content = Files.readString(hashFile).trim();
        String storedHash = content.split("\\s+")[0];
        if (!storedHash.matches("[0-9a-fA-F]{64}")) {
            throw new IOException("Hash file does not contain a valid SHA-256 digest.");
        }
        return storedHash;
    }
}