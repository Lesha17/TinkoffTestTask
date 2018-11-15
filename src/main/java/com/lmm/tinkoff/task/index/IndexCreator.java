package com.lmm.tinkoff.task.index;

import com.lmm.tinkoff.task.ScannerProvider;
import org.apache.commons.io.output.CountingOutputStream;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class IndexCreator {

    private ScannerProvider scannerProvider;

    public IndexCreator(ScannerProvider scannerProvider) {
        this.scannerProvider = scannerProvider;
    }

    public void createIndex(File input, File output) throws IOException {

        int[] numbersFromInput = readFile(input);
        Arrays.sort(numbersFromInput);

        int partsCount = getOptimalPartsCount(numbersFromInput.length);
        int partSize = numbersFromInput.length / partsCount + 1;

        long mapOffset = 0;
        SortedMap<Integer, Long> partsOffsets = new TreeMap<>();

        try(CountingOutputStream outputStream = new CountingOutputStream(new BufferedOutputStream(new FileOutputStream(output), 1024 * 1024))) {
            byte[] emptyBytesForMapOffset = new byte[8];
            outputStream.write(emptyBytesForMapOffset);

                for(int partStartIndex = 0; partStartIndex < numbersFromInput.length; partStartIndex += partSize) {

                    int partEndIndex = Math.min(partStartIndex + partSize, numbersFromInput.length);

                    int[] part = new int[partEndIndex - partStartIndex];
                    for(int i = partStartIndex; i < partEndIndex; ++i) {
                        part[i - partStartIndex] = numbersFromInput[i];
                    }

                    partsOffsets.put(part[0], outputStream.getByteCount());
                    writeObject(outputStream, part);
                }

                mapOffset = outputStream.getByteCount();
                writeObject(outputStream, partsOffsets);
        }

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(output,"rw")) {
            randomAccessFile.writeLong(mapOffset);
        }

        System.out.println("Indexed " + input + " to " + output);
    }

    protected int getOptimalPartsCount(int numbersCount) {
        return (int)Math.sqrt(numbersCount);
    }

    private void writeObject(OutputStream outputStream, Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }

        outputStream.write(byteArrayOutputStream.toByteArray());
    }

    private int[] readFile(File input) throws IOException {

        int capacity = 1024;
        int size = 0;
        int[] result = new int[capacity];

        try(InputStream inputStream = new FileInputStream(input)) {

            Scanner scanner = scannerProvider.getScanner(inputStream);

            while (scanner.hasNextInt()) {
                int n = scanner.nextInt();

                result[size++] = n;
                if(size == capacity) {
                    capacity *= 2;
                    result = Arrays.copyOf(result, capacity);
                }
            }

            return Arrays.copyOf(result, size);

        }
    }
}
