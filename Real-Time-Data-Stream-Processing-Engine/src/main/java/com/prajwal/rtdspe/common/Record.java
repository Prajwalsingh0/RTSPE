package com.prajwal.rtdspe.common;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * A single record stored in the log.
 * Binary layout on disk (Phase 1): [length][crc][timestamp][keyLen][key][valueLen][value]
 */
public class Record {
    private final byte[] key;
    private final byte[] value;
    private final long timestamp;

    public Record(byte[] key, byte[] value, long timestamp) {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    public byte[] getKey() { return key; }
    public byte[] getValue() { return value; }
    public long getTimestamp() { return timestamp; }

    public byte[] serialize() {
       int keyLen = (key==null)? 0:key.length;
        int valueLen = value.length;

        int contentSize = 8 + 4+keyLen+4+valueLen;

        ByteBuffer contentBuffer = ByteBuffer.allocate(contentSize);
        contentBuffer.putLong(timestamp);
        contentBuffer.putInt(keyLen);
        if(key != null){
            contentBuffer.put(key);
        }
        contentBuffer.putInt(valueLen);
        contentBuffer.put(value);

        byte[] content = contentBuffer.array();

        CRC32 crc32 = new CRC32();
        crc32.update(content);
        long crc = crc32.getValue();

        int totalSize = 4+8+content.length;
        ByteBuffer finalBuffer = ByteBuffer.allocate(totalSize);
        finalBuffer.putInt(content.length);
        finalBuffer.putLong(crc);
        finalBuffer.put(content);

       return finalBuffer.array();
    }


}