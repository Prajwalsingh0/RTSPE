package com.prajwal.rtdspe.storage;

import com.prajwal.rtdspe.common.Record;

    
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.File;

public class LogSegment {

    private final long baseOffset;
    private final File file;
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private long nextOffset;

    public LogSegment(long baseOffset, String directory) throws IOException {
        this.baseOffset = baseOffset;
        this.nextOffset = baseOffset;

        String filename = String.format("%020d.log", baseOffset);
        this.file = new File(directory, filename);
        this.raf = new RandomAccessFile(file, "rw");
        this.channel = raf.getChannel();
        this.channel.position(this.channel.size());
    }

    public long append(Record record) throws IOException {
        byte[] bytes = record.serialize();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        long assignedOffset = nextOffset;
        nextOffset++;
        return assignedOffset;
    }

    public Record read(long offset) throws IOException {
        if (offset < baseOffset || offset >= nextOffset) {
            throw new IllegalArgumentException("Offset " + offset + "out of range for this segment[" + baseOffset + "," + nextOffset + ")");
        }

        FileChannel readChannel = new RandomAccessFile(file, "r").getChannel();
        long currentOffset = baseOffset;
        long position = 0;

        while(currentOffset <= offset){
            ByteBuffer lengthBuf = ByteBuffer.allocate(4);
            readChannel.read(lengthBuf, position);
            lengthBuf.flip();
            int length = lengthBuf.getInt();

            int totalRecordSize = 4 + length;

            if (currentOffset == offset) {
                ByteBuffer recordBuf = ByteBuffer.allocate(totalRecordSize);
                readChannel.read(recordBuf, position);
                recordBuf.flip();
                readChannel.close();
                return Record.deserialize(recordBuf.array());
            }

            position += totalRecordSize;
            currentOffset++;
        }
        readChannel.close();
        throw new IllegalStateException("Offset not found: " + offset);
    }

    public long getBaseOffset() {
        return baseOffset;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public void close() throws IOException {
        channel.close();
        raf.close();
    }
}
