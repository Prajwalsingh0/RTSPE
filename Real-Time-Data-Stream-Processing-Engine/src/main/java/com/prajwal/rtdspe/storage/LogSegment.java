package com.prajwal.rtdspe.storage;

public class LogSegment {
    private final long baseOffset;

    public LogSegment(long baseOffset) {
        this.baseOffset = baseOffset;
    }

    public long baseOffset() {
        return baseOffset;
    }
}
