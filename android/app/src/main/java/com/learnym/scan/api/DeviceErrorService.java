package com.learnym.scan.api;

import com.learnym.scan.api.model.DeviceError;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Jerry Kurian on 26-11-2017.
 */

public interface DeviceErrorService {
    @POST("/api/device-error")
    public Call<DeviceError> createError(@Body DeviceError error);
}
