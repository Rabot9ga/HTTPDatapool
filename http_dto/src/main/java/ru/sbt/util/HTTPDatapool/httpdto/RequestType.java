package ru.sbt.util.HTTPDatapool.httpdto;

/**
 * Class for choosing type of consuming parameters
 *
 * @author SBT-Muravev-AA
 */
public enum RequestType {

    /**
     * Get data sequentially from pool. If pool is over then start from the start
     */
    SEQUENTIAL,

    /**
     * Get random data from pool
     */
    RANDOM,

    /**
     * Get unique data from pool getting values sequentially. Throws Exception if there is no unique values left
     */
    UNIQUE_SEQUENTIAL,

    /**
     * Get unique data from pool getting values from random position. Throws Exception if there is no unique values left
     */
    UNIQUE_RANDOM
}

