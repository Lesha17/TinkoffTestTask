package com.lmm.tinkoff.task.test;

import com.lmm.tinkoff.task.FilesProvider;
import com.lmm.tinkoff.task.ScannerProvider;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebServiceConfig.class)
public class IndexTest {

    private static final int SOME_FILE_INDEX = 5;
    private static final String INDEX_SUFFIX = "ttIndex";

    @Autowired
    private FilesProvider filesProvider;

    @Autowired
    private ScannerProvider scannerProvider;

    private File inputFile;
    private File indexFile;

    private IndexCreator indexCreator;
    private IndexReader indexReader;

    @Before
    public void init() throws Exception {
        List<File> files = filesProvider.getFiles();
        inputFile = files.get(SOME_FILE_INDEX);

        indexFile = Files.createTempFile(inputFile.getName(), INDEX_SUFFIX).toFile();
        indexFile.deleteOnExit();

        indexCreator = new IndexCreator(scannerProvider);
        indexReader = new IndexReader();
    }

    @After
    public void shutdown() {
        indexFile.delete();
    }

    @Test
    public void testIndex() throws Exception {

        indexCreator.createIndex(inputFile, indexFile);

        SortedMap<BigInteger, Long> partsOffsets = indexReader.readPartsOffsets(indexFile);
        Assert.assertFalse(partsOffsets.isEmpty());

        BigInteger someBigInteger = partsOffsets.firstKey().multiply(BigInteger.valueOf(2)).divide(BigInteger.valueOf(3))
                .add(partsOffsets.lastKey().divide(BigInteger.valueOf(3)));

        BigInteger key;
        if(partsOffsets.containsKey(someBigInteger)) {
            key = someBigInteger;
        } else {
            key = partsOffsets.headMap(someBigInteger).lastKey();
        }

        Long partOffset = partsOffsets.get(key);

        List<BigInteger> numbers = indexReader.readPart(indexFile, partOffset);
        System.out.println(numbers.size());
        System.out.println(someBigInteger);
        System.out.println(numbers);

        Assert.assertFalse(numbers.isEmpty());
        assertSorted(numbers);
        Assert.assertTrue(numbers.get(0).compareTo(someBigInteger) <= 0);
        Assert.assertTrue(numbers.get(numbers.size() - 1).compareTo(someBigInteger) >= 0);
    }

    private void assertSorted(List<BigInteger> numbers) {
        Assert.assertEquals("Numbers must be sorted", numbers.stream().sorted().collect(Collectors.toList()), numbers);
    }

}
