package com.lmm.tinkoff.task;

import com.lmm.tinkoff.task.index.IndexProvider;
import com.lmm.tinkoff.task.index.IndexReader;
import com.lmm.tinkoff.task.index.Indexer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class IndexedNumberSearcher implements NumberSearcher {

    private FilesProvider filesProvider;
    private IndexProvider indexProvider;
    private IndexReader indexReader;

    public IndexedNumberSearcher(FilesProvider filesProvider, IndexProvider indexProvider, IndexReader indexReader) {
        this.filesProvider = filesProvider;
        this.indexProvider = indexProvider;
        this.indexReader = indexReader;
    }

    @Override
    public List<String> findNumber(int number) {

        NumberSearcher numberSearcher = new NumberSearcher(number);

        ForkJoinPool pool = new ForkJoinPool();

        try {
            return pool.submit(() -> filesProvider.getFiles().parallelStream()
                    .filter(numberSearcher::searchNumber).map(this::getResourceName)
                    .collect(Collectors.toList()))
                    .get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        finally {
            pool.shutdown();
        }
    }

    protected String getResourceName(File file) {
        return file.getName();
    }

    private class NumberSearcher {
        private int number;

        public NumberSearcher(int number) {
            this.number = number;
        }

        public boolean searchNumber(File file) {

            try {
                File index = indexProvider.getIndex(file);

                SortedMap<Integer, Long> partsOffsets = indexReader.readPartsOffsets(index);

                Integer partKey;
                if (partsOffsets.containsKey(number)) {
                    partKey = number;
                } else {
                    SortedMap<Integer, Long> headMap = partsOffsets.headMap(number);
                    if(!headMap.isEmpty()) {
                        partKey = headMap.lastKey();
                    } else {
                        return false; // Given number is out of map's range
                    }
                }

                long partOffset = partsOffsets.get(partKey);
                int[] part = indexReader.readPart(index, partOffset);

                return Arrays.binarySearch(part, number) >= 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
