package com.lmm.tinkoff.task.index;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.SortedMap;

public class IndexReader {

    public SortedMap<Integer, Long> readPartsOffsets(File index) throws IOException {
        long mapOffset = readMapOffset(index);

        if(mapOffset <= 0) {
            throw new IllegalArgumentException("Index is not completed");
        }

        return (SortedMap<Integer, Long>) read(index, mapOffset);
    }

    public int[] readPart(File index, long partOffset) throws IOException {
        return (int[]) read(index, partOffset);
    }

    public boolean isIndexCompleted(File index) throws IOException {
        return readMapOffset(index) > 0;
    }

    private Object read(File file, long offset) throws IOException {
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.skip(offset);

            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return  objectInputStream.readObject();
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private long readMapOffset(File file) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            if(randomAccessFile.length() < 8) {
                return -1;
            }

            return randomAccessFile.readLong();
        }
    }

}
