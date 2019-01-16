package com.learnym.scan.api;

import com.learnym.scan.api.model.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jerry Kurian on 25-11-2017.
 */

public interface UserService {
    @POST("/api/users")
    public Call<Users> createUser(@Body Users user);
    @PUT("/api/users")
    public Call<Users> updateUser(@Body Users user);
    @GET("/api/users")
    public Call<List<Users>> getUser(@Query("master_id") long masterId);
    @GET("/api/users/{userid}")
    public Call<Users> getIndividualUser(@Path("userid") long userId);
}
