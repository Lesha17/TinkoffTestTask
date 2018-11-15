package com.lmm.tinkoff.task.index;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.SortedMap;

public class IndexReader {

    public SortedMap<BigInteger, Long> readPartsOffsets(File index) throws IOException {

        long mapOffset;
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(index, "r")) {
            mapOffset = randomAccessFile.readLong();
        }

        return (SortedMap<BigInteger, Long>) read(index, mapOffset);
    }

    public List<BigInteger> readPart(File index, long partOffset) throws IOException {
        return (List<BigInteger>) read(index, partOffset);
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

}
