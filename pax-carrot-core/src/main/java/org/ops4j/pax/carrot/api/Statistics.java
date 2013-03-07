/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.carrot.api;

import java.io.Serializable;

/**
 * Statistics of a test or group of tests.
 * 
 * @author Harald Wellmann
 *
 */
public class Statistics implements Serializable {

    private static final long serialVersionUID = 1L;

    private int numRight;
    private int numWrong;
    private int numException;
    private int numIgnored;

    public Statistics() {
    }

    public Statistics(int right, int wrong, int exception, int ignored) {
        this.numRight = right;
        this.numWrong = wrong;
        this.numException = exception;
        this.numIgnored = ignored;
    }

    /**
     * Accumulates statistics, adding the individual values (think {@code this += other}.
     * @param other statistics to be accumulated into the current ones
     */
    public void accumulate(Statistics other) {
        numRight += other.numRight;
        numWrong += other.numWrong;
        numException += other.numException;
        numIgnored += other.numIgnored;
    }

    /**
     * Total number of tests, sum of all counters.
     * @return
     */
    public int totalCount() {
        return numRight + numWrong + numException + numIgnored;
    }

    public String toString() {
        return String.format("%d tests: %d right, %d wrong, %d ignored, %d exception(s)",
            totalCount(), numRight, numWrong, numIgnored, numException);
    }

    /** Increments number of right cells (met expectations). */
    public void right() {
        numRight++;
    }

    /** Increments number of wrong cells (failed expectations). */
    public void wrong() {
        numWrong++;
    }

    /** Increments number of cells with an exception. */
    public void exception() {
        numException++;
    }

    /** Increments number of ignored cells (no expectations). */
    public void ignored() {
        numIgnored++;
    }

    /** True if there has been at least one exception or a wrong cell. */
    public boolean hasFailed() {
        return numWrong > 0 || numException > 0;
    }

    
    /**
     * @return the numRight
     */
    public int getNumRight() {
        return numRight;
    }

    
    /**
     * @return the numWrong
     */
    public int getNumWrong() {
        return numWrong;
    }

    
    /**
     * @return the numException
     */
    public int getNumException() {
        return numException;
    }

    
    /**
     * @return the numIgnored
     */
    public int getNumIgnored() {
        return numIgnored;
    }
}
