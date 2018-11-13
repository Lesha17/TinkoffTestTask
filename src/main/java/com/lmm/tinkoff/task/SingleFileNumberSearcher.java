package com.lmm.tinkoff.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class SingleFileNumberSearcher implements NumberSearcher {

    public static final int DEFAULT_CHUNKS_COUNT = 1;

    private File file;
    private String resourceName;
    private ScannerProvider scannerProvider;

    private int chunksCount;

    public SingleFileNumberSearcher(File file, String resourceName, ScannerProvider scannerProvider, int chunksCount) {
        this.file = file;
        this.resourceName = resourceName;
        this.scannerProvider = scannerProvider;
        this.chunksCount = chunksCount;
    }

    public SingleFileNumberSearcher(File file, String resourceName, ScannerProvider scannerProvider) {
        this(file, resourceName, scannerProvider, DEFAULT_CHUNKS_COUNT);
    }

    public SingleFileNumberSearcher(File file, ScannerProvider scannerProvider) {
        this(file, file.getName(), scannerProvider);
    }

    @Override
    public Collection<String> findNumber(int number) {

        ForkJoinPool forkJoinPool = new ForkJoinPool(chunksCount);

        try {
            long fileSize = Files.size(file.toPath());
            long chunkSize = fileSize / chunksCount + 1;

            List<NumberSearcher> chunkSearchers = new ArrayList<>();
            for(int i = 0; i < chunksCount; ++i) {
                chunkSearchers.add(new FileChunkNumberSearcher(file, resourceName, scannerProvider, chunkSize, i));
            }

            return forkJoinPool.submit(() -> chunkSearchers.parallelStream()
                        .map(numberSearcher -> numberSearcher.findNumber(number))
                        .filter(result -> !result.isEmpty())
                        .findAny().orElse(Collections.emptyList()))
                    .get();
        }
        catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            forkJoinPool.shutdown();
        }
    }
}
