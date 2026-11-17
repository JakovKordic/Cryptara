package cryptara;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyLoader {

    public static SecretKey loadAesKey(Path directory) throws IOException {
        Map<String, byte[]> entries = KeyFiles.read(directory.resolve(KeyManager.SECRET_FILE));
        byte[] keyBytes = entries.get(KeyManager.AES_256);
        if (keyBytes == null) {
            throw new IOException("No " + KeyManager.AES_256 + " key found in " + KeyManager.SECRET_FILE);
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
}