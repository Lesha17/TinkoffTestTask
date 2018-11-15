package com.lmm.tinkoff.task;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 *
 */
public class FilesNumberSearcher implements NumberSearcher {

    private Map<String, File> resources;
    private ScannerProvider scannerProvider;
    private int threadsCount = 1;
    private int chunksCount = 1;

    public FilesNumberSearcher(Map<String, File> resources, ScannerProvider scannerProvider) {
        this.resources = resources;
        this.scannerProvider = scannerProvider;
    }

    public FilesNumberSearcher(Collection<File> files, ScannerProvider scannerProvider) {
        this(files.stream().collect(Collectors.toMap(f -> f.getName(), f -> f)), scannerProvider);
    }

    public void setThreadsCount(int threadsCount) {
        this.threadsCount = threadsCount;
    }

    public void setChunksCount(int chunksCount) {
        this.chunksCount = chunksCount;
    }

    @Override
    public Collection<String> findNumber(int number) {

           return
                resources.entrySet().stream()
                    .map(e -> new SingleFileNumberSearcher(e.getValue(), e.getKey(), scannerProvider, chunksCount))
                    .flatMap(searcher -> searcher.findNumber(number).stream())
                    .collect(Collectors.toList());

    }
}
