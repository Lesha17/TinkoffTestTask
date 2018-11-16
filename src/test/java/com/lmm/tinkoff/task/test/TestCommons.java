package com.lmm.tinkoff.task.test;

import com.lmm.tinkoff.task.search.FilesProvider;
import com.lmm.tinkoff.task.index.IndexFilesProvider;
import com.lmm.tinkoff.task.index.IndexReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;

public class TestCommons {

    @Autowired
    private FilesProvider filesProvider;

    @Autowired
    private IndexFilesProvider indexFilesProvider;

    @Autowired
    private IndexReader indexReader;

    public File getSomeFile() throws IOException {
        List<File> files = filesProvider.getFiles();
        File someFile = files.get(files.size() / 3);
        return someFile;
    }

    public int getExistingNumber(File file) throws IOException {

        File indexFile = indexFilesProvider.getIndex(file);

        SortedMap<Integer, Long> partOffsets = indexReader.readPartsOffsets(indexFile);

        int someNumber = partOffsets.firstKey() / 3 + partOffsets.lastKey() * 2 / 3;
        Integer partKey = partOffsets.headMap(someNumber).lastKey();

        Long offset = partOffsets.get(partKey);
        int[] numbers = indexReader.readPart(indexFile, offset);

        int numberIndex = numbers.length * 3 / 5;
        return numbers[numberIndex];
    }
}
