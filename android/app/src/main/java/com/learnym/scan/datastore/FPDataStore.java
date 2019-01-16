/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.datastore;

import com.learnym.scan.api.AttendanceService;
import com.learnym.scan.api.LocationService;
import com.learnym.scan.api.ScanService;
import com.learnym.scan.api.UserService;
import com.learnym.scan.api.model.Attendance;
import com.learnym.scan.api.model.AttendanceResult;
import com.learnym.scan.api.model.Location;
import com.learnym.scan.api.model.ScanData;
import com.learnym.scan.api.model.Users;
import com.learnym.scan.device.DeviceManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 *
 * @author Admin
 */

public class FPDataStore{
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://tfpapi.learnym.com")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    public static UserService userService = retrofit.create(UserService.class);
    public static ScanService scanService = retrofit.create(ScanService.class);
    public static AttendanceService attendanceService = retrofit.create(AttendanceService.class);
    public static LocationService locationService = retrofit.create(LocationService.class);
    static ArrayList<UserDetails> userDetailsList = new ArrayList<UserDetails>();
    static String CLASSNAME = "FPDataStore";
    static AttendanceResult currentResult;

    static public void initData(long locationId) throws IOException {
        try{
            Call<List<ScanData>> getScansCaller = scanService.getAllScanData(locationId);
            Response<List<ScanData>> res = getScansCaller.execute();
            userDetailsList.clear();
            if(res.isSuccessful()){
                List<ScanData> scans = res.body();
                if(scans != null){
                    for(ScanData scan:scans){
                        saveToMemory(scan.getId().toString(),
                                scan.getUser().getId().toString(),
                                scan.getUser().getName(),
                                scan.getUser().getBuckleId(),
                                scan.getUser().getUnitName(),
                                scan.getScanData());
                    }
                }
            }else{
                LogManager.logMessage(CLASSNAME + ".initData", "Unable to Init Data : " + res.message());
                throw new IOException(res.message());
            }
        }catch (Throwable t){
            LogManager.logMessage(CLASSNAME + ".initData",CLASSNAME + "Unable to Init Data : " + t.getMessage());
        }
    }
    
    static public Users addData(String userId, String userName, String buckleId, String unitName, String locationName,  byte[] data) throws IOException {
        Users user = new Users();
        user.setId(new Long(userId));
        Location loc = new Location();
        loc.setName(locationName);
        user.setLocation(loc);
        ScanData scan = saveScanOnServer(user, data);
        saveToMemory(scan.getId().toString(), userId, userName, buckleId, unitName, data);
        return user;
    }

    static void saveToMemory(String scanId, String userId, String userName, String buckleId, String unitName, byte[] data){
        UserDetails tmpData = new UserDetails();
        tmpData.id = scanId;
        tmpData.userId = userId;
        tmpData.buckleId = buckleId;
        tmpData.unitName = unitName;
        tmpData.name = userName;
        tmpData.FPData = new byte[data.length];
        System.arraycopy(data, 0, tmpData.FPData, 0, data.length);
        userDetailsList.add(tmpData);
    }

    static Users saveUserOnServer(Users user) throws IOException {
        Call<Users> userCall = userService.createUser(user);
        Response<Users> res = userCall.execute();
        if(res.isSuccessful()){
            return res.body();
        }else{
            LogManager.logMessage(CLASSNAME + ".saveUserOnServer", res.message());
            throw new IOException(res.message());
        }
    }
    static ScanData saveScanOnServer(Users user, byte[] data) throws IOException {
        ScanData scan = new ScanData();
        scan.setUser(user);
        scan.setScanData(data);
        Call<ScanData> scanCall = scanService.createScanData(scan);
        Response<ScanData> res = scanCall.execute();
        if(res.isSuccessful()){
            Call<Users> userCall = userService.updateUser(user);
            userCall.execute();
            return res.body();
        }else{
            LogManager.logMessage(CLASSNAME + ".saveScanOnServer", res.message());
            throw new IOException(res.message());
        }
    }

    public static void storeCurrentScanResultInMemory(byte[] scan, String score, boolean result){
        AttendanceResult attendanceResult = new AttendanceResult();
        attendanceResult.setScan(scan);attendanceResult.setResult(result);attendanceResult.setScore(score);
        currentResult = attendanceResult;
    }
    public static void storeCurrentResultToServer(String searchedUserId, long attendanceId){
        LogManager.logMessage("FPDataStore.storeCurrentResultToServer", "Got attendance ID " + attendanceId);
        Attendance attendance = new Attendance();attendance.setId(attendanceId);
        currentResult.setAttendance(attendance);
        try{
            LogManager.logMessage("FPDataStore.storeCurrentResultToServer", "Storing " + currentResult.getScan().length);
            attendanceService.recordAttendanceResult(currentResult).execute();
            LogManager.logMessage("FPDataStore.storeCurrentResultToServer", "Stored " + currentResult.getScan().length);
        }catch (Throwable t){
            LogManager.logMessage("FPDataStore.storeCurrentResultToServer", t.getMessage());
        }
    }

    private static int findScoreForUser(String userId, byte[] candidateData) {
        try{

            List<UserDetails> fpList = userDetailsList;
            boolean matchFound = false
                    ;
            int score = 0;
            if(candidateData != null && candidateData.length != 0){
                for(UserDetails fp:fpList){
                    if(fp.userId.equals(userId)) {
                        score = DeviceManager.getDevice().getDevice().MatchISO(candidateData, fp.FPData);
                        break;
                    }
                }
            }
            return  score;
        }catch (Throwable t){
            LogManager.logMessage("AuthenticationManager.findScoreForUser", t.getMessage());
            return 0;
        }
    }
    static public ArrayList<UserDetails> getData(){
        return userDetailsList; 
    }
}