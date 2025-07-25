package com.example.sandbox.getPetList;

import com.example.sandbox.Common;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.report.TestListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.example.sandbox.util.constans.Tags.SMOKE;


@Listeners(TestListener.class)
public class petListTest extends Common {

    @Test(enabled = true,groups = {SMOKE},description ="description")
    public void Test1(){
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("status","available");
        queryParams.put("asd","asd");
        queryParams.put("maki","kakadu");

        Response  response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");
    }

    @Test(enabled = true,groups = {SMOKE},description ="description")
    public void Test2(){
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("status","available");
        Map<String, String> headers = new TreeMap<>();
        headers.put("Mandatoyheader","BFG");

        Response  response = getUrl(findByStatus,headers,queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");
    }

    //positive test
    //GET: /pet/findByStatus
    @Test(enabled = true, groups = {SMOKE}, description ="Checking if all statuses are pending if the given status is 'pending'")
    public void getPendingStatuses() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("status", "pending");

        Response  response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        List<String> statuses = response.jsonPath().getList("status");

        // Check that all statuses are "pending"
        boolean allPending = statuses.stream().allMatch(status -> status.equals("pending"));

        Assert.assertTrue(allPending, "Not all statuses are 'pending'. Actual: " + statuses);
        Assert.assertTrue(response.time() < 500, "Response time is longer than 500 milliseconds");

    }

    //negative test
    //GET: /pet/findByStatus
    @Test(enabled = true, groups = {SMOKE}, description ="Trying to get a non existing status")
    public void getNonExistingStatus() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("status", "test");

        Response  response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),400,"Invalid response code");
        Assert.assertTrue(response.time() < 500, "Response time is longer than 500 milliseconds");
    }
}
