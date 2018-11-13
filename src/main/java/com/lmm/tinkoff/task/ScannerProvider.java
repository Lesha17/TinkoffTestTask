package com.lmm.tinkoff.task;

import java.io.InputStream;
import java.util.Scanner;

public interface ScannerProvider {

    public Scanner getScanner(InputStream stream);
}
