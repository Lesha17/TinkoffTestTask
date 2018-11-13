package com.lmm.tinkoff.task;

import java.io.InputStream;
import java.util.Scanner;

public class DefaultScannerProvider implements ScannerProvider {

    @Override
    public Scanner getScanner(InputStream stream) {
        return new Scanner(stream).useDelimiter("[,|\\s]\\s*");
    }
}
