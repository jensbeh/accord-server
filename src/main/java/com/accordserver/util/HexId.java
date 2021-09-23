package com.accordserver.util;

import java.math.BigInteger;
import java.util.Random;

public class HexId {
    /**
     * Method generates a random hex string with 25 length from a BigInteger with 2^69 to set a unique id for database
     *
     * @return hexId string
     */
    public static String generateHexId() {
        BigInteger min = BigInteger.valueOf(2).pow(96);
        Random random = new Random();
        BigInteger result = new BigInteger(97, random).add(min);
        return result.toString(16);
    }
}