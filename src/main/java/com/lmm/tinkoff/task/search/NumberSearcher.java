package com.lmm.tinkoff.task.search;

import java.util.Collection;

/**
 * Searches for specified number in sources.
 */
public interface NumberSearcher {

    /**
     * Searches given number in some sources and returns names of those where given number found.
     *
     * @param number given number
     * @return names of source in which given number found (not <code>null</code>)
     * @throws FindNumberException if searching for number failed due to some internal reason
     */
    public Collection<String> findNumber(int number) throws FindNumberException;
}
