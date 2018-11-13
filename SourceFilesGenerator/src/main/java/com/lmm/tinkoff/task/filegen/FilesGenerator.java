package com.lmm.tinkoff.task.filegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilesGenerator {

    private static final String FILENAME_BASE = "SourceFile";
    private static final String EXTENSION = ".txt";
    private static final int FILES_COUNT = 20;

    private File filesDir;

    public FilesGenerator(File filesDir) {
        this.filesDir = filesDir;
    }

    public List<File> generateFiles() throws IOException {

        List<File> files = new ArrayList<>();
        for(int i = 0; i < FILES_COUNT; ++i) {
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
