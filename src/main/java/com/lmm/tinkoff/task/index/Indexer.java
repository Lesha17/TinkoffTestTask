package com.lmm.tinkoff.task.index;

import com.lmm.tinkoff.task.FilesProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

public abstract class Indexer {

    protected IndexCreator indexCreator;

    private FilesProvider filesProvider;
    private IndexProvider indexProvider;
    private IndexReader indexReader;

    public Indexer(FilesProvider filesProvider, IndexProvider indexProvider, IndexCreator indexCreator, IndexReader indexReader) {
        this.filesProvider = filesProvider;
        this.indexProvider = indexProvider;
        this.indexCreator = indexCreator;
        this.indexReader = indexReader;
    }

    public synchronized void reindexIfNeeded() throws IOException {
        for (File file : filesProvider.getFiles()) {
            reindexIfNeeded(file, indexProvider.getIndex(file));
        }
    }

    public boolean needReindex(File sourceFile, File indexFile) throws IOException {

        if(!indexReader.isIndexCompleted(indexFile)) {
            return true;
        }

        FileTime fileLastModified = Files.getLastModifiedTime(sourceFile.toPath());
        FileTime indexLastModified = Files.getLastModifiedTime(indexFile.toPath());

        return fileLastModified.compareTo(indexLastModified) > 0;
    }

    protected abstract void reindex(File sourceFile, File indexFile) throws IOException;

    private void reindexIfNeeded(File sourceFile, File indexFile) throws IOException {
        if (needReindex(sourceFile, indexFile)) {
            reindex(sourceFile, indexFile);
        }
    }

}
