package com.lmm.tinkoff.task;

import org.apache.commons.io.input.CountingInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

        ByteBuffer buffer;
        try(FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel channel = fileInputStream.getChannel()) {
            buffer = ByteBuffer.allocate((int)file.length());

            channel.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try(InputStream inputStream = new ByteArrayInputStream(buffer.array())) {

            Scanner scanner = scannerProvider.getScanner(inputStream);

            System.out.println("File: " + resourceName );

            while (scanner.hasNextInt() ) {
                int n = scanner.nextInt();
                if(n == number) {
                    System.out.println("Found");
                    return Arrays.asList(resourceName);
                }
            }

            System.out.println("Not found");
            return Collections.emptyList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
