package com.lmm.tinkoff.task.index;

import java.io.*;
import java.util.SortedMap;

/**
 * Provides index read operations.
 */
public class IndexReader {

    /**
     * Reads from given index file and returns map of index parts offsets.
     * Parts are intervals of sorted list of source numbers.
     * Key in given map is the first number in a corresponding part, value is offset of a part in an index file.
     *
     * @param index given index file (not null)
     * @return map, which keys are first numbers in parts, values are offsets of those parts in an index file
     * @throws IOException if reading failed
     */
    public SortedMap<Integer, Long> readPartsOffsets(File index) throws IOException {
        long mapOffset = readMapOffset(index);

        if (mapOffset <= 0) {
            throw new IllegalStateException("Index is not completed");
        }

        return (SortedMap<Integer, Long>) read(index, mapOffset);
    }

    /**
     * Reads from given index file and returns a part of an index, starting at given part offset.
     *
     * @param index      given index file (not null)
     * @param partOffset given part offset (not null)
     * @return part at given offset from given index file
     * @throws IOException if reading failed
     */
    public int[] readPart(File index, long partOffset) throws IOException {
        return (int[]) read(index, partOffset);
    }

    /**
     * Checks and returns if index within given file was successfully created.
     *
     * @param index given index file (not null)
     * @return true if index within given file was successfully created, false otherwise
     * @throws IOException if file reading failed
     */
    public boolean isIndexCompleted(File index) throws IOException {
        return readMapOffset(index) > 0;
    }

    private Object read(File file, long offset) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.skip(offset);

            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return objectInputStream.readObject();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private long readMapOffset(File file) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            if (randomAccessFile.length() < 8) {
                return -1;
            }

            return randomAccessFile.readLong();
        }
    }

}
