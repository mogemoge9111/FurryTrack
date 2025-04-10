package com.example.furrytrackapp.Utils;

import com.example.furrytrackapp.Model.Pet;
import com.example.furrytrackapp.Model.PetRecord;
import com.example.furrytrackapp.Model.Post;
import com.example.furrytrackapp.Model.Users;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Для записей
    @POST("records")
    Call<PetRecord> createRecord(@Header("Authorization") String token, @Body PetRecord record);

    @PUT("records/{id}")
    Call<PetRecord> updateRecord(@Header("Authorization") String token, @Path("id") String id, @Body PetRecord record);
    @GET("pets/{petId}")
    Call<Pet> getPetById(@Path("petId") String petId);

    @GET("records/pet/{petId}")
    Call<List<PetRecord>> getPetRecords(@Path("petId") String petId);


    @DELETE("records/{recordId}")
    Call<Void> deleteRecord(@Path("recordId") String recordId);

    // Для пользователя
    @GET("users/me")
    Call<Users> getCurrentUser(@Header("Authorization") String token);

    @PUT("users/me")
    Call<Users> updateUser(@Header("Authorization") String token, @Body Users user);

    // Для питомцев
    @GET("pets")
    Call<List<Pet>> getUserPets(@Header("Authorization") String token);

    @POST("pets")
    Call<Pet> createPet(@Header("Authorization") String token, @Body Pet pet);

    @POST("/api/posts")
    Call<PostResponse> createPost(String s, @Body PostRequest postRequest);

    @GET("/api/posts/feed")
    Call<List<Post>> getFeed();

    Call<Users> getUser(String string);

    Call<Users> loginUser(Users loginRequest);
}