package com.lmm.tinkoff.task.index;

import java.io.File;
import java.io.IOException;

/**
 * Provides index file for source file.
 */
public class IndexFilesProvider {

    public static final String INDEX_EXTENSION = ".index";

    private File indexDirectory;

    public IndexFilesProvider(File indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    /**
     * Creates, if necessary, and returns index file for given source file.
     *
     * @param sourceFile given source file (not <code>null</code>)
     * @return index file for given source file
     * @throws IOException if creating index file failed
     */
    public File getIndex(File sourceFile) throws IOException {
        File indexFile = new File(indexDirectory, sourceFile.getName().concat(INDEX_EXTENSION));
        if (!indexFile.exists()) {
            indexFile.createNewFile();
        }
        return indexFile;
    }
}
