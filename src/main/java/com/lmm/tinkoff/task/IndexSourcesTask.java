package com.lmm.tinkoff.task;

import com.lmm.tinkoff.task.index.BlockingIndexer;
import com.lmm.tinkoff.task.index.Indexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IndexSourcesTask implements ApplicationRunner {

    @Autowired
    private Indexer indexer;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        System.out.println("Starting reindex");
        indexer.reindexIfNeeded();
    }
}
