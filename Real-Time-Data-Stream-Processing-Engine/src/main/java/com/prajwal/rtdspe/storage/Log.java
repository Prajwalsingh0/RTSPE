package com.prajwal.rtdspe.storage;

import com.prajwal.rtdspe.common.Record;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private final List<Record> records = new ArrayList<>();

    public synchronized void append(Record record) {
        records.add(record);
    }

    public synchronized Record read(int index) {
        return records.get(index);
    }

    public synchronized int size() {
        return records.size();
    }
}
