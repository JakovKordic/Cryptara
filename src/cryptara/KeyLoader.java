package cryptara;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyLoader {

    public static SecretKey loadAesKey(Path directory) throws IOException {
        byte[] keyBytes = readEntry(directory.resolve(KeyManager.SECRET_FILE), KeyManager.AES_256);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static PublicKey loadPublicKey(Path directory, String label, String algorithm)
            throws IOException, GeneralSecurityException {
        byte[] keyBytes = readEntry(directory.resolve(KeyManager.PUBLIC_FILE), label);
        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    public static PrivateKey loadPrivateKey(Path directory, String label, String algorithm)
            throws IOException, GeneralSecurityException {
        byte[] keyBytes = readEntry(directory.resolve(KeyManager.PRIVATE_FILE), label);
        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private static byte[] readEntry(Path file, String label) throws IOException {
        byte[] keyBytes = KeyFiles.read(file).get(label);
        if (keyBytes == null) {
            throw new IOException("No " + label + " key found in " + file.getFileName());
        }
        return keyBytes;
    }
}