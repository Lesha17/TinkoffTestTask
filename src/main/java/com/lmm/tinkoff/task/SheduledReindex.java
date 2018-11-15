package com.lmm.tinkoff.task;

import com.lmm.tinkoff.task.index.Indexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SheduledReindex {

    private static final long DELAY = 10000L;

    @Autowired
    private Indexer indexer;

    @Scheduled(fixedDelay = DELAY, initialDelay = DELAY)
    public void reindexIfNeeded() throws IOException {
        System.out.println("Reindex task started");
        indexer.reindexIfNeeded();
    }

}
