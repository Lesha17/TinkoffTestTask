package com.lmm.tinkoff.task.filegen;

import java.io.File;
import java.io.IOException;

public interface FileContentGenerator {

    /**
     * Fills given file with generated content.
     *
     * @param file given file
     * @throws IOException if writing content to file fails
     */
    public void fillContent(File file) throws IOException;
}
