package com.lmm.tinkoff.task.filegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GenerateSourceFilesMain {

    private static final String SOURCE_DIR_KEY = "-sourceDir";
    private static final String FILES_LIST_KEY = "-filesList";
    private static final String FILES_COUNT_KEY = "-filesCount";
    private static final String FILE_SIZE_KEY = "-fileSize";
    private static final String ORIGIN_KEY = "-origin";
    private static final String BOUND_KEY = "-bound";

    private static final String DEFAULT_BASE_DIR_NAME = "TinkoffTestTask";
    private static final String DEFAULT_SOURCE_FILES_DIR_NAME = "SourceFiles";
    private static final String DEFAULT_FILES_LIST = "filesList.txt";
    private static final int DEFAULT_FILES_COUNT = 20;
    private static final int DEFAULT_FILE_SIZE = 128 * 1024 * 1024;
    private static final int DEFAULT_ORIGIN = 0;
    private static final int DEFAULT_BOUND = 1024 * 1024 * 1024;

    public static void main(String[] args) throws IOException {


        Map<String, String> argsMap = new HashMap<>();
        for (int i = 0; i + 1 < args.length; i += 2) {
            argsMap.put(args[i], args[i + 1]);
        }

        File sourceFilesDir = getSourceFilesDir(argsMap);
        File sourceFilesListFile = getFilesList(argsMap);
        int filesCount = getFilesCount(argsMap);
        int fileSize = getFileSize(argsMap);
        int origin = getOrigin(argsMap);
        int bound = getBound(argsMap);

        if (!sourceFilesDir.exists()) {
            sourceFilesDir.mkdirs();
        }

        FilesGenerator filesGenerator = new FilesGenerator(sourceFilesDir, filesCount);
        FileContentGenerator contentGenerator = new RangedFileContentGenerator(origin, bound, fileSize);

        List<File> files = filesGenerator.generateFiles();
        for (File f : files) {
            contentGenerator.fillContent(f);
            System.out.println("Generated " + f.getAbsolutePath());
        }

        if (!sourceFilesListFile.exists()) {
            sourceFilesListFile.createNewFile();
        }

        try (PrintWriter printWriter = new PrintWriter(new FileWriter(sourceFilesListFile))) {
            for (File f : files) {
                printWriter.println(f.getAbsoluteFile().getAbsolutePath());
            }
        }
    }

    private static File getSourceFilesDir(Map<String, String> argsMap) {
        return getOrDefault(argsMap, SOURCE_DIR_KEY, File::new, getDefaultSourceFilesDir());
    }

    private static File getFilesList(Map<String, String> argsMap) {
        return getOrDefault(argsMap, FILES_LIST_KEY, File::new, getDefaultFilesList());
    }

    private static int getFilesCount(Map<String, String> argsMap) {
        return getOrDefault(argsMap, FILES_COUNT_KEY, Integer::parseInt, DEFAULT_FILES_COUNT);
    }

    private static int getFileSize(Map<String, String> argsMap) {
        return getOrDefault(argsMap, FILE_SIZE_KEY, Integer::parseInt, DEFAULT_FILE_SIZE);
    }

    private static int getOrigin(Map<String, String> argsMap) {
        return getOrDefault(argsMap, ORIGIN_KEY, Integer::parseInt, DEFAULT_ORIGIN);
    }

    private static int getBound(Map<String, String> argsMap) {
        return getOrDefault(argsMap, BOUND_KEY, Integer::parseInt, DEFAULT_BOUND);
    }

    private static <T> T getOrDefault(Map<String, String> argsMap, String argKey,
                                      Function<String, T> provider, T defaultValue) {
        String arg = argsMap.get(argKey);
        if (arg != null) {
            return provider.apply(arg);
        } else {
            return defaultValue;
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
