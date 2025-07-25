package com.example.sandbox.util;

import io.restassured.response.Response;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Tools {
    public static int generateRandomNumber(){
        return (int) (Math.random()*100);
    }

    /**
     * Returns a random number which is not included in the given set of numbers
     * @param ids
     * @return random id
     */
    public static Integer findNonExistingId(Set<Object> ids) {
        int newId;
        int maxAttempts = 1000;
        int attempts = 0;

        do {
            newId = ThreadLocalRandom.current().nextInt(1, 999999);
            attempts++;
            if (attempts > maxAttempts) {
                throw new RuntimeException("Unable to find a non existing ID after " + maxAttempts + " attempts");
            }
        } while (ids.contains(newId));

        return newId;
    }
}
