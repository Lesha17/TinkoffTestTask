package com.lmm.tinkoff.task.index;

import com.lmm.tinkoff.task.FilesProvider;

import java.io.File;
import java.io.IOException;

public class BlockingIndexer extends Indexer {

    public BlockingIndexer(FilesProvider filesProvider, IndexProvider indexProvider, IndexCreator indexCreator, IndexReader indexReader) {
        super(filesProvider, indexProvider, indexCreator, indexReader);
    }

    @Override
    protected void reindex(File sourceFile, File indexFile) throws IOException {
        indexCreator.createIndex(sourceFile, indexFile);
    }
}
