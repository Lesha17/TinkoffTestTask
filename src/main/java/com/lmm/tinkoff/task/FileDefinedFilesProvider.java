package com.lmm.tinkoff.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileDefinedFilesProvider implements FilesProvider {

    private File file;

    public FileDefinedFilesProvider(File file) {
        this.file = file;
    }

    @Override
    public List<File> getFiles() throws IOException {

        List<File> result = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                File file = new File(line);
                result.add(file);
            }
        }
        return result;
    }
}
