package com.example.sandbox.getPet;

import com.example.sandbox.Common;
import com.example.sandbox.util.enums.PetStatus;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.report.TestListener;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.sandbox.util.constans.Tags.SMOKE;

@Listeners(TestListener.class)
public class petDetailTest extends Common {

    @Test(enabled = true,groups = {SMOKE},description ="description")
    public void Test1(){
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("status","available");

        Response  response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        String id = response.jsonPath().get("find{it.status.equals('available')}.id").toString();

        Response  response2 = getUrl(petById.replace("{petId}",id));
        Assert.assertEquals(response2.getStatusCode(),200,"Invalid response code");
    }


    //positive test
    //GET: /pet/{id}
    @Test(enabled = true, groups = {SMOKE}, description ="Get pet by random id")
    public void getPetById() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("status","available");

        Response  response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        List<Object> ids = response.jsonPath().getList("id");
        String id = ids.get(new Random().nextInt(ids.size())).toString();

        response = getUrl(petById.replace("{petId}", id));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        // Assert required fields (name, photoUrls)
        Object nameObj = response.jsonPath().get("name");
        Assert.assertNotNull(nameObj, "'name' is missing");
        Assert.assertTrue(nameObj instanceof String, "'name' should be a string");

        List<String> photoUrls = response.jsonPath().getList("photoUrls");
        Assert.assertNotNull(photoUrls, "'photoUrls' is missing or null");
        Assert.assertFalse(photoUrls.isEmpty(), "'photoUrls' array should not be empty");

        for (Object url : photoUrls) {
            Assert.assertTrue(url instanceof String, "Each photoUrl should be a string");
        }

        // Assert if status has correct value
        String status = response.jsonPath().getString("status");
        if (status != null) {
            Assert.assertTrue(PetStatus.isValid(status), "'status' value is invalid: " + status);
        }

        Assert.assertTrue(response.time() < 500, "Response time is longer than 500 milliseconds");
    }

    //negative test
    //GET: /pet/{id}
    @Test(enabled = true, groups = {SMOKE}, description ="Get pet by non existing id")
    public void getPetByNonExistingId() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("status","available,pending,sold");

        Response  response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        List<Object> ids = response.jsonPath().getList("id");

        Integer nonExistingId = findNonExistingId(new HashSet<>(ids));

        response = getUrl(petById.replace("{petId}", nonExistingId.toString()));
        Assert.assertEquals(response.getStatusCode(),404,"Invalid response code");
        Assert.assertTrue(response.time() < 500, "Response time is longer than 500 milliseconds");
    }

    /**
     * Returns a random number which is not included in the given set of numbers
     * @param ids
     * @return random id
     */
    public Integer findNonExistingId(Set<Object> ids) {
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
