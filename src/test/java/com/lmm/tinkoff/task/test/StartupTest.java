package com.lmm.tinkoff.task.test;

import com.lmm.tinkoff.task.Application;
import com.lmm.tinkoff.task.search.FilesProvider;
import com.lmm.tinkoff.task.WebServiceConfig;
import com.lmm.tinkoff.task.index.IndexFilesProvider;
import com.lmm.tinkoff.task.index.Indexer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class, WebServiceConfig.class})
public class StartupTest {

    @Autowired
    private FilesProvider filesProvider;

    @Autowired
    private IndexFilesProvider indexFilesProvider;

    @Autowired
    private Indexer indexer;

    @Test
    public void testIndexFilesCreated() throws Exception {
        for (File file : filesProvider.getFiles()) {
            Assert.assertFalse(indexer.needReindex(file, indexFilesProvider.getIndex(file)));
        }
    }
}
