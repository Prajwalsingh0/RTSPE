// package com.prajwal.rtdspe.storage;

// import com.prajwal.rtdspe.common.Record;

// import java.io.IOException;
// import java.io.RandomAccessFile;
// import java.nio.channels.FileChannel;
// import java.nio.ByteBuffer;
// import java.io.File;

// /**
//  * Represents a single segment file of a partition's log.
//  *
//  * Records are appended sequentially (no random writes — this is what gives
//  * us high throughput, since sequential disk I/O is dramatically faster than random I/O).
//  *
//  * baseOffset = the logical offset of the first record in this segment.
//  * nextOffset = the offset that will be assigned to the NEXT appended record.
//  */
// public class LogSegment {

//     private final long baseOffset;
//     private final File file;
//     private final RandomAccessFile raf;
//     private final FileChannel channel;

//     private long nextOffset;

//     public LogSegment(long baseOffset, String directory) throws IOException {
//         this.baseOffset = baseOffset;
//         this.nextOffset = baseOffset;

//         // Segment file naming convention: the base offset, padded, as the filename.
//         // e.g. baseOffset=0 -> "00000000000000000000.log"
//         String filename = String.format("%020d.log", baseOffset);
//         this.file = new File(directory, filename);

//         this.raf = new RandomAccessFile(file, "rw");
//         this.channel = raf.getChannel();

//         // If the file already has data (e.g. from a previous run), start writing
//         // from the end, not overwrite from the start.
//         this.channel.position(this.channel.size());
//     }

//     /**
//      * Appends a record to this segment. Returns the offset assigned to it.
//      */
//     public long append(Record record) throws IOException {
//         byte[] bytes = record.serialize();
//         ByteBuffer buffer = ByteBuffer.wrap(bytes);

//         while (buffer.hasRemaining()) {
//             channel.write(buffer);
//         }

//         long assignedOffset = nextOffset;
//         nextOffset++;
//         return assignedOffset;
//     }

//     /**
//      * Reads the record at the given logical offset.
//      *
//      * NAIVE IMPLEMENTATION: scans from the beginning of the file, deserializing
//      * records one by one, until it reaches the requested offset.
//      * This is intentionally O(n) for now — OffsetIndex (next) will make this O(log n).
//      */
//     public Record read(long offset) throws IOException {
//         if (offset < baseOffset || offset >= nextOffset) {
//             throw new IllegalArgumentException(
//                 "Offset " + offset + " out of range for this segment [" + baseOffset + ", " + nextOffset + ")"
//             );
//         }

//         FileChannel readChannel = new RandomAccessFile(file, "r").getChannel();
//         long position = 0;
//         long currentOffset = baseOffset;

//         while (currentOffset <= offset) {
//             // Read the 4-byte length field first to know how much more to read.
//             ByteBuffer lengthBuf = ByteBuffer.allocate(4);
//             readChannel.read(lengthBuf, position);
//             lengthBuf.flip();
//             int length = lengthBuf.getInt();

//             int totalRecordSize = 4 + length; // length field itself + everything it counts

//             if (currentOffset == offset) {
//                 ByteBuffer recordBuf = ByteBuffer.allocate(totalRecordSize);
//                 readChannel.read(recordBuf, position);
//                 recordBuf.flip();
//                 readChannel.close();
//                 return Record.deserialize(recordBuf.array());
//             }

//             position += totalRecordSize;
//             currentOffset++;
//         }

//         readChannel.close();
//         throw new IllegalStateException("Offset not found: " + offset);
//     }

//     public long getBaseOffset() {
//         return baseOffset;
//     }

//     public long getNextOffset() {
//         return nextOffset;
//     }

//     public void close() throws IOException {
//         channel.close();
//         raf.close();
//     }
// }