package com.prajwal.rtdspe.storage;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.prajwal.rtdspe.common.Record;

class LogSegmentTest {

    @Test
    void appendAndReadSingleRecord(@TempDir Path tempDir) throws IOException {
        LogSegment segment = new LogSegment(0, tempDir.toString());
        Record record = new Record("key1".getBytes(), "values1".getBytes(), System.currentTimeMillis());
        long offset = segment.append(record);

        Record readBack = segment.read(0);
        assertArrayEquals(record.getKey(), readBack.getKey());
        assertArrayEquals(record.getValue(), readBack.getValue());
        assertEquals(record.getTimestamp(), readBack.getTimestamp());

        segment.close();
    }

    @Test
    void appendMultipleRecordsAssignSequentialOffsets(@TempDir Path tempDir) throws IOException {
        LogSegment segment = new LogSegment(0, tempDir.toString());

        long offset0 = segment.append(new Record("k0".getBytes(), "v0".getBytes(), 1000L));
        long offset1 = segment.append(new Record("k1".getBytes(), "v1".getBytes(), 1001L));
        long offset2 = segment.append(new Record("k2".getBytes(), "v2".getBytes(), 1002L));

        assertEquals(0, offset0);
        assertEquals(1, offset1);
        assertEquals(2, offset2);

        Record r0 = segment.read(0);
        Record r1 = segment.read(1);
        Record r2 = segment.read(2);

        assertArrayEquals("v0".getBytes(), r0.getValue());
        assertArrayEquals("v1".getBytes(), r1.getValue());
        assertArrayEquals("v2".getBytes(), r2.getValue());

        segment.close();
    }

    @Test
    void readingOutOfRangeOffsetThrows(@TempDir Path tempDir) throws IOException {
        LogSegment segment = new LogSegment(0, tempDir.toString());
        segment.append(new Record("k".getBytes(), "v".getBytes(), 1001L));

        assertThrows(IllegalArgumentException.class, () -> segment.read(5));

        segment.close();
    }

    @Test
    void segmentRecoversExistingDataOnReopen(@TempDir Path tempDir) throws IOException {
        LogSegment segment1 = new LogSegment(0, tempDir.toString());
        segment1.append(new Record("k0".getBytes(), "v0".getBytes(), 1000L));
        segment1.close();

        LogSegment segment2 = new LogSegment(0, tempDir.toString());
        long newOffset = segment2.append(new Record("k1".getBytes(), "v1".getBytes(), 1001L));
        assertEquals(1, newOffset, "New segment should continue offsets after reopening, not restart from baseOffset");
        segment2.close();

    }

}
