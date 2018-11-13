package com.lmm.tinkoff.task;

import org.apache.commons.io.input.CountingInputStream;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class FileChunkNumberSearcher implements NumberSearcher {

    private File file;
    private String resourceName;
    private long chunkSize;
    private long chunkNumber;
    private ScannerProvider scannerProvider;

    public FileChunkNumberSearcher(File file, String resourceName, ScannerProvider scannerProvider, long chunkSize, long chunkNumber) {
        this.file = file;
        this.resourceName = resourceName;
        this.scannerProvider = scannerProvider;
        this.chunkSize = chunkSize;
        this.chunkNumber = chunkNumber;
    }

    public FileChunkNumberSearcher(File file, ScannerProvider scannerProvider, long chunkSize, long chunkNumber) {
        this(file, file.getName(), scannerProvider, chunkSize, chunkNumber);
    }

    @Override
    public Collection<String> findNumber(int number) {
        try(CountingInputStream inputStream = new CountingInputStream(new BufferedInputStream(new FileInputStream(file)))) {

            Scanner scanner = scannerProvider.getScanner(inputStream);

            if(chunkNumber > 0) {

                // Deal with situation when number chunk start position splits number
                inputStream.skip(chunkSize * chunkNumber - 1);
                inputStream.mark(4);
                int c = inputStream.read();
                if(Character.isDigit(c)) {
                    inputStream.reset();
                    scanner.nextInt();
                }
            }

            System.out.println("File: " + resourceName + ", Byte count: " + inputStream.getByteCount());

            while (scanner.hasNextInt() && inputStream.getByteCount() < chunkSize * (chunkNumber + 1)) {
                int n = scanner.nextInt();
                if(n == number) {
                    return Arrays.asList(resourceName);
                }
            }

            return Collections.emptyList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
