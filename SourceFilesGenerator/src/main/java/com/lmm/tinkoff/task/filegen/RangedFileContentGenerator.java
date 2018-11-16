package com.lmm.tinkoff.task.filegen;

import org.apache.commons.io.output.CountingOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class RangedFileContentGenerator implements FileContentGenerator {

    private int origin;
    private int bound;
    private Random random = new Random();

    private long fileSize;

    public RangedFileContentGenerator(int origin, int bound, long fileSize) {

        this.origin = origin;
        this.bound = bound;

        this.fileSize = fileSize;
    }

    @Override
    public void fillContent(File file) throws IOException {

        try(CountingOutputStream outputStream = new CountingOutputStream(new FileOutputStream(file));
            PrintWriter printWriter = new PrintWriter(outputStream)) {

            while (outputStream.getByteCount() < fileSize) {

                int number = nextNumber(outputStream.getByteCount());
                printWriter.write(number + ",");
            }
        }
    }

    protected int nextNumber(long currentSize) {
        return random.nextInt(bound - origin) + origin;
    }
}
