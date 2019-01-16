package com.learnym.scan.api;

import com.learnym.scan.api.model.ScanData;
import com.learnym.scan.api.model.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jerry Kurian on 25-11-2017.
 */

public interface ScanService {
    @POST("/api/scan-data")
    public Call<ScanData> createScanData(@Body ScanData scanData);
    @GET("/api/scan-data")
    public Call<List<ScanData>> getAllScanData(@Query("location_id") Long locationId);
    @GET("/api/scan-data/{scanid}")
    public Call<ScanData> getScanData(@Path("scanid") long userId);
}
