package com.lmm.tinkoff.task.search;

import com.lmm.tinkoff.task.index.IndexFilesProvider;
import com.lmm.tinkoff.task.index.IndexReader;
import com.lmm.tinkoff.task.index.Indexer;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * Implementation of {@link NumberSearcher}, which uses pre-created indexes of source files.
 */
public class IndexedNumberSearcher implements NumberSearcher, InitializingBean {

    private FilesProvider filesProvider;
    private IndexFilesProvider indexFilesProvider;
    private IndexReader indexReader;

    @Autowired
    private Logger logger;

    @Autowired
    private Indexer indexer;

    /**
     * Consturcts new instance with given source files provider, index files provider and index reader.
     *
     * @param filesProvider      given source files provider (not null)
     * @param indexFilesProvider given index files provider (not null)
     * @param indexReader        given index reader (not null)
     */
    public IndexedNumberSearcher(FilesProvider filesProvider, IndexFilesProvider indexFilesProvider, IndexReader indexReader) {
        this.filesProvider = filesProvider;
        this.indexFilesProvider = indexFilesProvider;
        this.indexReader = indexReader;
    }

    /**
     * {@inheritDoc}
     *
     * <br>
     * <p>
     * Searches for given number in source files, using only indexes of those files.
     * If some indexes is out-of-dated, result may be invalid.
     */
    @Override
    public List<String> findNumber(int number) {

        NumberSearcher numberSearcher = new NumberSearcher(number);

        ForkJoinPool pool = new ForkJoinPool();

        try {
            return pool.submit(() -> filesProvider.getFiles().parallelStream()
                    .filter(numberSearcher::searchNumber).map(this::getSourceName)
                    .collect(Collectors.toList()))
                    .get();
        } catch (Exception e) {
            throw new FindNumberException(e);
        } finally {
            pool.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        if (logger != null) {
            logger.info("Started reindex files");
        }
        if (indexer != null) {
            indexer.reindexIfNeeded();
        }
    }

    /**
     * Returns name of source for given file.
     * <br>
     * In this implementation source name is just name of file.
     *
     * @param file given file (not null)
     * @return source name for given file (not null)
     */
    protected String getSourceName(File file) {
        return file.getName();
    }

    private class NumberSearcher {
        private int number;

        public NumberSearcher(int number) {
            this.number = number;
        }

        public boolean searchNumber(File file) {

            try {
                File index = indexFilesProvider.getIndex(file);

                SortedMap<Integer, Long> partsOffsets = indexReader.readPartsOffsets(index);

                Integer partKey;
                if (partsOffsets.containsKey(number)) {
                    partKey = number;
                } else {
                    SortedMap<Integer, Long> headMap = partsOffsets.headMap(number);
                    if (!headMap.isEmpty()) {
                        partKey = headMap.lastKey();
                    } else {
                        return false; // Given number is out of map's range
                    }
                }

                long partOffset = partsOffsets.get(partKey);
                int[] part = indexReader.readPart(index, partOffset);

                return Arrays.binarySearch(part, number) >= 0;
            } catch (Exception e) {
                throw new FindNumberException(e);
            }
        }
    }

}
