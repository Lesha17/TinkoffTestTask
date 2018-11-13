package com.lmm.tinkoff.task.filegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GenerateSourceFilesMain {

    private static final String BASEDIR_KEY = "-baseDir";
    private static final String FILES_LIST_KEY = "-filesList";

    private static final String DEFAULT_BASE_DIR_NAME = "TinkoffTestTask";
    private static final String DEFAULT_SOURCE_FILES_DIR_NAME = "SourceFiles";
    private static final String DEFAULT_FILES_LIST = "filesList.txt";

    public static void main(String[] args) throws IOException {

        File sourceFilesDir = getDefaultSourceFilesDir();
        File sourceFilesListFile = getDefaultFilesList();

        for(int i = 0; i + 1 < args.length; i+=2) {
            if(args[i].equals(BASEDIR_KEY)) {
                sourceFilesDir = new File(args[i+1]);
            }
            if(args[i].equals(FILES_LIST_KEY)) {
                sourceFilesListFile = new File(args[i+1]);
            }
        }

        if(!sourceFilesDir.exists()) {
            sourceFilesDir.mkdirs();
        }

        FilesGenerator filesGenerator = new FilesGenerator(sourceFilesDir);
        FileContentGenerator contentGenerator = new RangedFileContentGenerator();

        List<File> files = filesGenerator.generateFiles();
        for(File f : files) {
            contentGenerator.fillContent(f);
            System.out.println("Generated " + f.getAbsolutePath());
        }

        if(!sourceFilesListFile.exists()) {
            sourceFilesListFile.createNewFile();
        }

        try(PrintWriter printWriter = new PrintWriter(new FileWriter(sourceFilesListFile))) {
            for(File f : files) {
                printWriter.println(f.getAbsoluteFile().getAbsolutePath());
            }
        }
    }

    private static File getDefaultSourceFilesDir() {
        return new File(getBaseDir(), DEFAULT_SOURCE_FILES_DIR_NAME);
    }

    private static File getDefaultFilesList() {
        return new File(getBaseDir(), DEFAULT_FILES_LIST);
    }

    private static File getBaseDir() {
        return new File(System.getProperty("user.home"), DEFAULT_BASE_DIR_NAME);
    }
}
