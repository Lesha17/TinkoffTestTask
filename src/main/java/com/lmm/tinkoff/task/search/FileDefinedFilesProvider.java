package com.lmm.tinkoff.task.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads source files locations from specified file.
 */
public class FileDefinedFilesProvider implements FilesProvider {

    private File file;

    /**
     * Construct new instance with given file.
     *
     * @param file a file, which contains locations of source files, separated by \n
     */
    public FileDefinedFilesProvider(File file) {
        this.file = file;
    }

    /**
     * Reads and returns list of source files from file, with which this instance is constructed.
     *
     * @return list of source files (not null)
     * @throws IOException if reading list of source files failed
     */
    @Override
    public List<File> getFiles() throws IOException {

        List<File> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                File file = new File(line);
                result.add(file);
            }
        }
        return result;
    }
}
