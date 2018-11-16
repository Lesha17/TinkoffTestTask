package com.lmm.tinkoff.task.index;

import org.apache.commons.io.output.CountingOutputStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Provides operation of creating index.
 */
public class IndexCreator {

    @Autowired
    private Logger logger;

    /**
     * Creates index for given source file and writes it into given index file.
     *
     * @param sourceFile given source file (not null)
     * @param indexFile  given index file (not null)
     * @throws IOException if reading given source file or writing index to index file failed
     */
    public void createIndex(File sourceFile, File indexFile) throws IOException {

        int[] numbersFromInput = readFile(sourceFile);
        Arrays.sort(numbersFromInput);

        int partsCount = getOptimalPartsCount(numbersFromInput.length);
        int partSize = numbersFromInput.length / partsCount + 1;

        long mapOffset = 0;
        SortedMap<Integer, Long> partsOffsets = new TreeMap<>();

        try (CountingOutputStream outputStream = new CountingOutputStream(new BufferedOutputStream(new FileOutputStream(indexFile), 1024 * 1024))) {
            byte[] emptyBytesForMapOffset = new byte[8];
            outputStream.write(emptyBytesForMapOffset);

            for (int partStartIndex = 0; partStartIndex < numbersFromInput.length; partStartIndex += partSize) {

                int partEndIndex = Math.min(partStartIndex + partSize, numbersFromInput.length);

                int[] part = new int[partEndIndex - partStartIndex];
                for (int i = partStartIndex; i < partEndIndex; ++i) {
                    part[i - partStartIndex] = numbersFromInput[i];
                }

                partsOffsets.put(part[0], outputStream.getByteCount());
                writeObject(outputStream, part);
            }

            mapOffset = outputStream.getByteCount();
            writeObject(outputStream, partsOffsets);
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(indexFile, "rw")) {
            randomAccessFile.writeLong(mapOffset);
        }

        logger.info("Indexed " + sourceFile + " to " + indexFile);
    }

    /**
     * Returns a count of index parts.
     * An optimal value may allow faster index creating or / and search.
     *
     * @param numbersCount given count of indexed numbers
     * @return a count of index parts. Must be greater than 0
     */
    protected int getOptimalPartsCount(int numbersCount) {
        return (int) Math.sqrt(numbersCount);
    }

    private void writeObject(OutputStream outputStream, Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }

        outputStream.write(byteArrayOutputStream.toByteArray());
    }

    private int[] readFile(File input) throws IOException {

        int capacity = 1024;
        int size = 0;
        int[] result = new int[capacity];

        try (InputStream inputStream = new FileInputStream(input)) {

            Scanner scanner = new Scanner(inputStream).useDelimiter("[,|\\s]\\s*");

            while (scanner.hasNextInt()) {
                int n = scanner.nextInt();

                result[size++] = n;
                if (size == capacity) {
                    capacity *= 2;
                    result = Arrays.copyOf(result, capacity);
                }
            }

            return Arrays.copyOf(result, size);

        }
    }
}
