package com.lmm.tinkoff.task;

import java.util.Collection;

/**
 * Provides <i>findNumber</i> operation implementation.
 */
public interface NumberSearcher {

    /**
     * Searches given number in sources and returns names of those where given number found.
     *
     * @param  number given number
     * @return names of source in which given number found (not <code>null</code>)
     */
    public Collection<String> findNumber(int number);
}
