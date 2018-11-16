package com.lmm.tinkoff.task.index;

import com.lmm.tinkoff.task.search.FilesProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

/**
 * Provides creating index of source files when some indexes are invalid.
 */
public abstract class Indexer {

    protected IndexCreator indexCreator;

    private FilesProvider filesProvider;
    private IndexFilesProvider indexFilesProvider;
    private IndexReader indexReader;

    /**
     * Constructs new instance with given parameters.
     *
     * @param filesProvider      given files provider (not null)
     * @param indexFilesProvider given index files provider (not null)
     * @param indexCreator       given index creator (not null)
     * @param indexReader        given index reader (not null)
     */
    public Indexer(FilesProvider filesProvider, IndexFilesProvider indexFilesProvider, IndexCreator indexCreator, IndexReader indexReader) {
        this.filesProvider = filesProvider;
        this.indexFilesProvider = indexFilesProvider;
        this.indexCreator = indexCreator;
        this.indexReader = indexReader;
    }

    /**
     * Performs creating index for source files, for which index is currently invalid.
     *
     * @throws IOException if checking for index validity or creating new index failed
     */
    public synchronized void reindexIfNeeded() throws IOException {
        for (File file : filesProvider.getFiles()) {
            reindexIfNeeded(file, indexFilesProvider.getIndex(file));
        }
    }

    /**
     * Checks and returns if index within given index file is invalid for given source file.
     *
     * @param sourceFile given source file (not null)
     * @param indexFile  given index file (not null)
     * @return true if index is invalid, false otherwise
     * @throws IOException if checking index validity failed
     */
    public boolean needReindex(File sourceFile, File indexFile) throws IOException {

        if (!indexReader.isIndexCompleted(indexFile)) {
            return true;
        }

        FileTime fileLastModified = Files.getLastModifiedTime(sourceFile.toPath());
        FileTime indexLastModified = Files.getLastModifiedTime(indexFile.toPath());

        return fileLastModified.compareTo(indexLastModified) > 0;
    }

    /**
     * Performs creating new index for given source file.
     * Created index would be written to given index file.
     *
     * @param sourceFile given source file (not null)
     * @param indexFile  given index file (not null)
     * @throws IOException if creating index or writin index to index file failed
     */
    protected abstract void reindex(File sourceFile, File indexFile) throws IOException;

    private void reindexIfNeeded(File sourceFile, File indexFile) throws IOException {
        if (needReindex(sourceFile, indexFile)) {
            reindex(sourceFile, indexFile);
        }
    }

}
