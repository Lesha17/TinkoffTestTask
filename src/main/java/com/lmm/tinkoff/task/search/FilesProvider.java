package com.lmm.tinkoff.task.search;


import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Provides collection of source files.
 *
 * <strong>Does not generate content of those files.</strong>
 */
public interface FilesProvider {

    /**
     * Returns collection of source files.
     *
     * <strong>Does not generate content of those files.</strong>
     *
     * @return collection of source files (not <code>null</code>)
     * @throws IOException if files creation fails
     */
    public List<File> getFiles() throws IOException;
}
