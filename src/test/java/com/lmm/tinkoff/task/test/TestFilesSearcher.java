package com.lmm.tinkoff.task.test;

import com.lmm.tinkoff.task.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebServiceConfig.class)
public class TestFilesSearcher {

    private static final int SOME_FILE_INDEX = 3;

    private static final int FILES_COUNT = 1;
    private static final int CHUNKS_COUNT = 1;

    private List<File> files;

    @Autowired
    private NumberSearcher searcher;

    @Autowired
    private FilesProvider filesProvider;

    @Autowired
    private ScannerProvider scannerProvider;

    @Before
    public void init() throws Exception {
        files = filesProvider.getFiles();
    }

    @Test
    public void testFilesSearcher() throws Exception {

        File someFile = getSomeFile();
        int someNumber = getSomeNumberFromFile(someFile);

        Collection<String> result = searcher.findNumber(someNumber);
        System.out.println(result);
        Assert.assertTrue(result.contains(someFile.getName()));
    }

    private File getSomeFile() {
        return files.get(SOME_FILE_INDEX);
    }

    private int getSomeNumberFromFile(File file) throws IOException {

        long fileSize = file.length();

        try(FileInputStream inputStream = new FileInputStream(file)) {
            inputStream.skip(fileSize / 2);

            Scanner scanner = scannerProvider.getScanner(inputStream);
            scanner.nextInt();
            return scanner.nextInt();
        }
    }
}
