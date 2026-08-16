package com.prajwal.rtdspe.common;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * A single record stored in the log.
 *
 * Binary layout on disk: [length (4 bytes)][crc (8 bytes)][timestamp (8 bytes)]
 * [keyLen (4 bytes)][key (variable)][valueLen (4 bytes)][value (variable)]
 *
 * "length" = size of everything AFTER the length field itself (i.e. crc +
 * content). "crc" = checksum of (timestamp + keyLen + key + valueLen + value),
 * used to detect corruption.
 */
public class Record {

    private final byte[] key;     // can be null (no key = round-robin partitioning later)
    private final byte[] value;   // the actual payload, never null
    private final long timestamp;

    public Record(byte[] key, byte[] value, long timestamp) {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Converts this Record into its binary on-disk representation.
     */
    public byte[] serialize() {
        int keyLen = (key == null) ? 0 : key.length;
        int valueLen = value.length;

        // content = timestamp(8) + keyLen(4) + key(keyLen) + valueLen(4) + value(valueLen)
        int contentSize = 8 + 4 + keyLen + 4 + valueLen;

        ByteBuffer contentBuffer = ByteBuffer.allocate(contentSize);
        contentBuffer.putLong(timestamp);
        contentBuffer.putInt(keyLen);
        if (key != null) {
            contentBuffer.put(key);
        }
        contentBuffer.putInt(valueLen);
        contentBuffer.put(value);

        byte[] content = contentBuffer.array();

        CRC32 crc32 = new CRC32();
        crc32.update(content);
        long crc = crc32.getValue();

        // length = crc(8) + content.length  -> everything AFTER the length field
        int length = 8 + content.length;

        // Final buffer: length(4) + crc(8) + content
        int totalSize = 4 + length;
        ByteBuffer finalBuffer = ByteBuffer.allocate(totalSize);
        finalBuffer.putInt(length);
        finalBuffer.putLong(crc);
        finalBuffer.put(content);

        return finalBuffer.array();
    }

    /**
     * Reconstructs a Record from its binary representation. Verifies the CRC
     * before parsing fields — if the checksum doesn't match, the bytes are
     * corrupted and we fail loudly instead of returning garbage.
     */
    public static Record deserialize(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        int length = buffer.getInt();   // crc(8) + content.length
        long crc = buffer.getLong();

        int contentSize = length - 8;
        byte[] content = new byte[contentSize];
        buffer.get(content);

        // Verify CRC against the raw content bytes before trusting any field inside it.
        CRC32 crc32 = new CRC32();
        crc32.update(content);
        long computedCrc = crc32.getValue();

        if (computedCrc != crc) {
            throw new IllegalStateException("Record corrupted: expected CRC " + crc + " but computed " + computedCrc);
        }

        // Now that content is verified, parse fields out of it.
        ByteBuffer contentBuffer = ByteBuffer.wrap(content);

        long timestamp = contentBuffer.getLong();
        int keyLen = contentBuffer.getInt();
        byte[] key = null;
        if (keyLen > 0) {
            key = new byte[keyLen];
            contentBuffer.get(key);
        }
        int valueLen = contentBuffer.getInt();
        byte[] value = new byte[valueLen];
        contentBuffer.get(value);

        return new Record(key, value, timestamp);
    }
}
