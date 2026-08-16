package com.prajwal.rtdspe.storage;

import com.prajwal.rtdspe.common.Record;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecordTest {

    @Test
    void roundTripSerializationAndDeserializePreservesData() {
        byte[] key = "user-123".getBytes();
        byte[] value = "order-placed".getBytes();
        long timestamp = System.currentTimeMillis();
        Record original = new Record(key, value, timestamp);

        byte[] serialized = original.serialize();
        Record restored = Record.deserialize(serialized);
        assertArrayEquals(original.getKey(), restored.getKey());
        assertArrayEquals(original.getValue(), restored.getValue());
        assertEquals(original.getTimestamp(), restored.getTimestamp());
    }

    @Test
    void roundTripWithNullKeyWorks() {
        byte[] value = "no-key-record".getBytes();
        long timestamp = System.currentTimeMillis();
        Record original = new Record(null, value, timestamp);
        byte[] serialized = original.serialize();
        Record restored = Record.deserialize(serialized);
        assertNull(restored.getKey());
        assertArrayEquals(original.getValue(), restored.getValue());
        assertEquals(original.getTimestamp(), restored.getTimestamp());
    }
}
