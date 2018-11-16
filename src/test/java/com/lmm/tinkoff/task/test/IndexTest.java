package com.lmm.tinkoff.task.test;

import com.lmm.tinkoff.task.search.FilesProvider;
import com.lmm.tinkoff.task.WebServiceConfig;
import com.lmm.tinkoff.task.index.IndexCreator;
import com.lmm.tinkoff.task.index.IndexReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebServiceConfig.class)
public class IndexTest {

    private static final int SOME_FILE_INDEX = 1;
    private static final String INDEX_SUFFIX = "ttIndex";

    @Autowired
    private FilesProvider filesProvider;

    @Autowired
    private IndexCreator indexCreator;

    @Autowired
    private IndexReader indexReader;

    private File inputFile;
    private File indexFile;

    @Before
    public void init() throws Exception {
        List<File> files = filesProvider.getFiles();
        inputFile = files.get(SOME_FILE_INDEX);

        indexFile = Files.createTempFile(inputFile.getName(), INDEX_SUFFIX).toFile();
        indexFile.deleteOnExit();
    }

    @After
    public void shutdown() {
        indexFile.delete();
    }

    @Test
    public void testIndex() throws Exception {

        indexCreator.createIndex(inputFile, indexFile);

        SortedMap<Integer, Long> partsOffsets = indexReader.readPartsOffsets(indexFile);
        Assert.assertFalse(partsOffsets.isEmpty());

        int someNumber = (partsOffsets.firstKey() * 2 / 3) +
                (partsOffsets.lastKey() / 3);

        Integer key;
        if (partsOffsets.containsKey(someNumber)) {
            key = someNumber;
        } else {
            key = partsOffsets.headMap(someNumber).lastKey();
        }

        Long partOffset = partsOffsets.get(key);

        int[] numbers = indexReader.readPart(indexFile, partOffset);

        Assert.assertFalse(numbers.length == 0);
        assertSorted(numbers);
        Assert.assertTrue(numbers[0] <= someNumber);
        Assert.assertTrue(numbers[numbers.length - 1] >= someNumber);
    }

    private void assertSorted(int[] numbers) {

        int[] sortedArr = Arrays.copyOf(numbers, numbers.length);
        Arrays.sort(sortedArr);

        Assert.assertArrayEquals("Numbers must be sorted", sortedArr, numbers);
    }

}
