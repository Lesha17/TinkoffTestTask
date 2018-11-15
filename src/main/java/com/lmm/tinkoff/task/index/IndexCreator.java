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

        List<BigInteger> numbersFromInput = readFile(input);
        numbersFromInput.sort(BigInteger::compareTo);

        int partsCount = getOptimalPartsCount(numbersFromInput.size());
        int partSize = numbersFromInput.size() / partsCount + 1;

        long mapOffset = 0;
        SortedMap<BigInteger, Long> partsOffsets = new TreeMap<>();

        try(CountingOutputStream outputStream = new CountingOutputStream(new BufferedOutputStream(new FileOutputStream(output), 1024 * 1024))) {
            byte[] emptyBytesForMapOffset = new byte[8];
            outputStream.write(emptyBytesForMapOffset);

                for(int partStartIndex = 0; partStartIndex < numbersFromInput.size(); partStartIndex += partSize) {

                    int partEndIndex = Math.min(partStartIndex + partSize, numbersFromInput.size());
                    List<BigInteger> part = new ArrayList<>(numbersFromInput.subList(partStartIndex, partEndIndex));

                    partsOffsets.put(part.get(0), outputStream.getByteCount());
                    writeObject(outputStream, part);
                }

                mapOffset = outputStream.getByteCount();
                writeObject(outputStream, partsOffsets);
        }

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(output,"rw")) {
            randomAccessFile.writeLong(mapOffset);
        }
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

    // Memory required to store integers from file of size s, where each number has n decimal digits, is
    // (s / (n+1)) * (n * log(10) / log(2)) / 8 bytes.
    private List<BigInteger> readFile(File input) throws IOException {
        List<BigInteger> result = new ArrayList<>();

        try(InputStream inputStream = new FileInputStream(input)) {

            Scanner scanner = scannerProvider.getScanner(inputStream);

            while (scanner.hasNextBigInteger()) {
                BigInteger n = scanner.nextBigInteger();
                result.add(n);
            }

            return result;

        }
    }
}
