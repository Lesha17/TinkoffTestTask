package com.lmm.tinkoff.task.index;

import com.lmm.tinkoff.task.search.FilesProvider;

import java.io.File;
import java.io.IOException;

/**
 * {@link Indexer} implementation with blocking {@link #reindex(File, File)} operation.
 */
public class BlockingIndexer extends Indexer {

    /**
     * Overrides {@link Indexer#Indexer(FilesProvider, IndexFilesProvider, IndexCreator, IndexReader)}.
     */
    public BlockingIndexer(FilesProvider filesProvider, IndexFilesProvider indexFilesProvider, IndexCreator indexCreator, IndexReader indexReader) {
        super(filesProvider, indexFilesProvider, indexCreator, indexReader);
    }

    /**
     * Blocking implementation of {@link Indexer#reindex(File, File)}.
     */
    @Override
    protected void reindex(File sourceFile, File indexFile) throws IOException {
        indexCreator.createIndex(sourceFile, indexFile);
    }
}
