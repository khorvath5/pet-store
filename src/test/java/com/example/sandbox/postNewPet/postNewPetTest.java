package com.example.sandbox.postNewPet;

import com.example.sandbox.Common;
import com.example.sandbox.util.body.pet.PostCreatePet;
import com.example.sandbox.util.swagger.definitions.Item;
import com.example.sandbox.util.swagger.definitions.PetBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.report.TestListener;

import java.util.LinkedHashMap;

import static com.example.sandbox.util.Tools.generateRandomNumber;
import static com.example.sandbox.util.body.pet.JsonBody.createJsonBody;
import static com.example.sandbox.util.constans.Tags.SMOKE;
import static com.example.sandbox.util.constans.TestData.HYDRAIMAGE;

@Listeners(TestListener.class)
public class postNewPetTest extends Common {

    @Test(enabled = true,groups = {SMOKE},description ="description")
    public void createNewPet(){

        Integer id = generateRandomNumber();
        String name = "Hydra";

        PostCreatePet body = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(id)
                        .category(Item.builder()
                                .id(1)
                                .name(name)
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


        Response  response = postUrl(newPet,createJsonBody(body));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");
        Assert.assertTrue(response.time() < 500, "Response time is longer than 500 milliseconds");

        response = getUrl(petById.replace("{petId}", id.toString()));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        LinkedHashMap json = response.jsonPath().get();
        Assert.assertEquals(json.get("id"), id.toString(), "Wrong id");
        Assert.assertEquals(json.get("name"), name, "Wrong name");
    }

    @Test(enabled = true,groups = {SMOKE},description ="description")
    public void createNewPetWithMissingRequiredField() {

        Integer id = generateRandomNumber();
        String name = "Hydra";

        PostCreatePet body = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(id)
                        .category(Item.builder()
                                .id(1)
                                .name(name)
                                .build())
                        .photoUrl(HYDRAIMAGE)
                        .status("available")
                        .build()
                ).build();

        Response  response = postUrl(newPet,createJsonBody(body));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        response = getUrl(petById.replace("{petId}", id.toString()));
        Assert.assertEquals(response.getStatusCode(),404,"The pet should not be created with the missing required field.");
    }


}
