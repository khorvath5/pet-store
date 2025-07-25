package com.example.sandbox.businessProcesses;

import com.example.sandbox.Common;
import com.example.sandbox.util.Tools;
import com.example.sandbox.util.body.pet.PostCreatePet;
import com.example.sandbox.util.enums.PetStatus;
import com.example.sandbox.util.swagger.definitions.Item;
import com.example.sandbox.util.swagger.definitions.PetBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.report.TestListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.example.sandbox.util.Tools.generateRandomNumber;
import static com.example.sandbox.util.body.pet.JsonBody.createJsonBody;
import static com.example.sandbox.util.constans.Tags.SMOKE;
import static com.example.sandbox.util.constans.TestData.HYDRAIMAGE;

@Listeners(TestListener.class)
public class PetLifeCycle extends Common {
    @Test(enabled = true,groups = {SMOKE},description ="description")
    public void Test1(){

        PostCreatePet body = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(generateRandomNumber())
                        .category(Item.builder()
                                .id(1)
                                .name("Hydra")
                                .build())
                        .name("Princess")
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(2)
                                .name("cute")
                                .build())
                        .status("available")
                        .build()
                ).build();


        Response response = postUrl(newPet,createJsonBody(body));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        String id = response.jsonPath().get("id").toString();

        Response  response2 = getUrl(petById.replace("{petId}",id));
        Assert.assertEquals(response2.getStatusCode(),200,"Invalid response code");
    }

    @Test(enabled = true, groups = {SMOKE}, description ="description")
    public void testPetLifeCycle() throws InterruptedException {
        // preparing test data - non existing id
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("status","available,pending,sold");
        Response  response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");
        List<Object> ids = response.jsonPath().getList("id");

        Integer id = Tools.findNonExistingId(new HashSet<>(ids));
        String name = "Biggi";

        // preparing request body
        PostCreatePet body = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(id)
                        .category(Item.builder()
                                .id(1)
                                .name("test")
                                .build())
                        .name(name)
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(1)
                                .name("test")
                                .build())
                        .status(PetStatus.AVAILABLE.getValue())
                        .build()
                ).build();

        // create new pet
        response = postUrl(newPet, createJsonBody(body));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        // assert that the pet is created
        response = waitForPetStatus(petById.replace("{petId}", id.toString()), 200, 10, 2000);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");
        Integer createdId = response.jsonPath().get("id");
        String createdName = response.jsonPath().get("name");
        Assert.assertEquals(createdId, id, "Wrong id");
        Assert.assertEquals(createdName, name, "Wrong name");

        // edit the created pet
        String newName = "Biggu";
        body = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(id)
                        .category(Item.builder()
                                .id(1)
                                .name("test")
                                .build())
                        .name(newName)
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(1)
                                .name("test")
                                .build())
                        .status(PetStatus.AVAILABLE.getValue())
                        .build()
                ).build();

        response = putUrl(newPet, createJsonBody(body));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        // assert that the pet is updated
        waitFiveSeconds();
        response = waitForPetStatus(petById.replace("{petId}", id.toString()), 200, 10, 2000);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");
        String editedName = response.jsonPath().get("name");
        Assert.assertEquals(editedName, newName, "Wrong name");

        // delete the created pet
        response = tryToDelete(petById.replace("{petId}",id.toString()));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        // assert that the pet is deleted
        response = waitForPetStatus(petById.replace("{petId}", id.toString()), 404, 10, 2000);
        Assert.assertEquals(response.getStatusCode(),404,"Invalid response code");
    }

    public Response waitForPetStatus(String endpoint, int expectedStatus, int maxRetries, long delayMillis) throws InterruptedException {
        Response response = null;
        for (int i = 0; i < maxRetries; i++) {
            response = getUrl(endpoint);
            if (response.statusCode() == expectedStatus) {
                return response;
            }
            Thread.sleep(delayMillis);
        }
        return response; // Last response after timeout
    }

    public Response tryToDelete(String endpoint) throws InterruptedException {
        Response response = null;
        for (int i = 0; i < 5; i++) {
            response = deleteUrl(endpoint);
            if (response.statusCode() == 200) {
                return response;
            }
            Thread.sleep(1000);
        }
        return response; // Last response after timeout
    }

    public void waitFiveSeconds() throws InterruptedException {
        Thread.sleep(5000);
    }

}
