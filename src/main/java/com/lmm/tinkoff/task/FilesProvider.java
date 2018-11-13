package com.lmm.tinkoff.task;


import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Provides collection of files to be used as sources in {@link FilesNumberSearcher}.
 *
 * <strong>Does not generate content of those files.</strong>
 */
public interface FilesProvider {

    /**
     * Returns collection of files to be used as sources in {@link FilesNumberSearcher}.
     *
     * <strong>Does not generate content of those files.</strong>
     *
     * @return collection of files (not <code>null</code>)
     * @throws IOException if files creation fails
     */
    public List<File> getFiles() throws IOException;
}
