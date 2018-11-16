package com.lmm.tinkoff.task.filegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilesGenerator {

    private static final String FILENAME_BASE = "SourceFile";
    private static final String EXTENSION = ".txt";

    private File filesDir;
    private int filesCount;

    public FilesGenerator(File filesDir, int filesCount) {
        this.filesDir = filesDir;
        this.filesCount = filesCount;
    }

    public List<File> generateFiles() throws IOException {

        List<File> files = new ArrayList<>();
        for(int i = 0; i < filesCount; ++i) {
            String fileName = FILENAME_BASE + i + EXTENSION;
            File file = new File(filesDir, fileName);
            if(!file.exists()) {
                file.createNewFile();
            }

            files.add(file);
        }

        return files;
    }
}
