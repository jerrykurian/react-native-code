package com.learnym.scan.datastore;

import com.learnym.scan.api.DeviceErrorService;
import com.learnym.scan.api.model.DeviceError;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Jerry Kurian on 26-11-2017.
 */

public class LogManager {
    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://tfpapi.learnym.com")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    static DeviceErrorService errorService = retrofit.create(DeviceErrorService.class);
    public static void logMessage(String errorPoint, String message){
        DeviceError error = new DeviceError();
        error.setMessage(errorPoint + "-" + message);
        Call<DeviceError> errorCall = errorService.createError(error);
        errorCall.enqueue(new Callback<DeviceError>() {
            @Override
            public void onResponse(Call<DeviceError> call, Response<DeviceError> response) {

            }

            @Override
            public void onFailure(Call<DeviceError> call, Throwable t) {

            }
        });
    }
}
