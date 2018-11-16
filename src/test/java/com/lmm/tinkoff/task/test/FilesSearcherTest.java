package com.lmm.tinkoff.task.test;

import com.lmm.tinkoff.task.search.FilesProvider;
import com.lmm.tinkoff.task.search.NumberSearcher;
import com.lmm.tinkoff.task.WebServiceConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Collection;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebServiceConfig.class, TestConfig.class})
public class FilesSearcherTest {

    private List<File> files;

    @Autowired
    private TestCommons testCommons;

    @Autowired
    private NumberSearcher searcher;

    @Autowired
    private FilesProvider filesProvider;

    @Before
    public void init() throws Exception {
        files = filesProvider.getFiles();
    }

    @Test
    public void testFilesSearcher() throws Exception {
        File someFile = testCommons.getSomeFile();
        int someNumber = testCommons.getExistingNumber(someFile);

        Collection<String> result = searcher.findNumber(someNumber);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.contains(someFile.getName()));
    }
}
