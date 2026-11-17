package cryptara;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public class Envelope {

    public record Parts(byte[] keyMaterial, byte[] encryptedData) {
    }

    public static byte[] pack(byte[] keyMaterial, byte[] encryptedData) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + keyMaterial.length + encryptedData.length);
        buffer.putInt(keyMaterial.length);
        buffer.put(keyMaterial);
        buffer.put(encryptedData);
        return buffer.array();
    }

    public static Parts unpack(byte[] envelope) throws GeneralSecurityException {
        ByteBuffer buffer = ByteBuffer.wrap(envelope);
        if (buffer.remaining() < Integer.BYTES) {
            throw new GeneralSecurityException("Envelope is too short.");
        }
        int keyLength = buffer.getInt();
        if (keyLength < 0 || keyLength > buffer.remaining()) {
            throw new GeneralSecurityException("Envelope is corrupted.");
        }
        byte[] keyMaterial = new byte[keyLength];
        buffer.get(keyMaterial);
        byte[] encryptedData = new byte[buffer.remaining()];
        buffer.get(encryptedData);
        return new Parts(keyMaterial, encryptedData);
    }
}