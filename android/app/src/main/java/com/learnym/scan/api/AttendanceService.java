package com.learnym.scan.api;

import com.learnym.scan.api.model.Attendance;
import com.learnym.scan.api.model.AttendanceResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by Jerry Kurian on 25-11-2017.
 */

public interface AttendanceService {
    @POST("/api/attendances")
    public Call<Attendance> recordAttendance(@Body Attendance attendance);

    @PUT("/api/attendances")
    public Call<Attendance> updateAttendance(@Body Attendance attendance);

    @POST("/api/attendance-results")
    public Call<AttendanceResult> recordAttendanceResult(@Body AttendanceResult attendanceResult);
}
