package com.learnym.scan.api;

import com.learnym.scan.api.model.Attendance;
import com.learnym.scan.api.model.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by Jerry Kurian on 25-11-2017.
 */

public interface LocationService {
    @GET("/api/locations")
    public Call<List<Location>> loadAll();
}
