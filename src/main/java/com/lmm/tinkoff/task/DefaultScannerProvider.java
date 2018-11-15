package com.lmm.tinkoff.task;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class DefaultScannerProvider implements ScannerProvider {

    @Override
    public Scanner getScanner(InputStream stream) {

        return new Scanner(stream).useDelimiter("[,|\\s]\\s*");
    }
}
