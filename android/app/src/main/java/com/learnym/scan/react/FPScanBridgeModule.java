//  Created by react-native-create-bridge

package com.learnym.scan.react;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Callback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnym.scan.api.model.Attendance;
import com.learnym.scan.api.model.AttendanceResult;
import com.learnym.scan.api.model.Location;
import com.learnym.scan.api.model.ScanData;
import com.learnym.scan.api.model.Users;
import com.learnym.scan.datastore.FPDataStore;
import com.learnym.scan.datastore.LocationManager;
import com.learnym.scan.datastore.LogManager;
import com.learnym.scan.datastore.UserDetails;
import com.learnym.scan.integrator.ServerSideEventManager;
import com.learnym.scan.ui.ScanModule;
import com.learnym.scan.ui.ScanUICallback;

import java.io.IOException;
import java.util.List;

import com.learnym.scan.device.DeviceManager;
import com.learnym.scan.integrator.DeviceEventManager;

import retrofit2.Call;
import retrofit2.Response;

public class FPScanBridgeModule extends ReactContextBaseJavaModule {
    private static final String TAG = "GA.FPScanBridgeModule";
    public static final String REACT_CLASS = "FPScanBridge";
    private static ReactApplicationContext reactContext = null;
    private ScanModule scanModule;
    private String CLASSNAME = "FPScanBridgeModule";
    static ServerSideEventManager serverSideEventManager;
    public FPScanBridgeModule(ReactApplicationContext context) {
        // Pass in the context to the constructor and save it so you can emit events
        // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
        super(context);
        try{
            reactContext = context;
            this.initScanModule();
        }catch(Throwable t){}
    }

    private void initScanModule(){
        try{
            if(this.scanModule == null){
                DeviceManager.startListeningForDeviceEvents(new DeviceEventManager(reactContext));
                serverSideEventManager = new ServerSideEventManager(reactContext);
                this.scanModule = new ScanModule(serverSideEventManager);
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".initScanModule", t.getMessage());
        }
    }

    @ReactMethod
    public void init(String locationId, Callback callback) {
        int result = 0;
        try{
            FPDataStore.initData(new Long(locationId));
            callback.invoke(true, "Initialized");
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".init", t.getMessage());
            callback.invoke(false,t.getMessage());
        }
    }

    @ReactMethod
    public void isConnected(Callback callback) {
        try{
            if(DeviceManager.isConnected()){
                callback.invoke(true);
            }else{
                callback.invoke(false);
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".isConnected", t.getMessage());
        }
    }

    @ReactMethod
    public void loadUserInfo(String masterId, Callback callback){
        try{
            Call loadUserCall = FPDataStore.userService.getUser(new Long(masterId).longValue());
            Response<List<Users>> res = loadUserCall.execute();
            if(res.isSuccessful()){
                List<Users> users  = res.body();
                if(users!=null){
                    Users user = users.get(0);
                    callback.invoke(true, user.getId().toString(), user.getBuckleId().toString(),
                            user.getMasterId().toString(), user.getName(), user.getUnitName());
                }else {
                    callback.invoke(false, "Could not find any user");
                }
            }
        }catch (Throwable t){
            LogManager.logMessage(CLASSNAME + ".loadUserInfo", t.getMessage());
            callback.invoke(false, t.getMessage());
        }
    }

    @ReactMethod
    public void loadIndividualUserInfo(String userId, Callback callback){
        try{
            Call loadUserCall = FPDataStore.userService.getIndividualUser(new Long(userId).longValue());
            Response<Users> res = loadUserCall.execute();
            if(res.isSuccessful()){
                Users user  = res.body();
                if(user!=null){
                    callback.invoke(true, user.getId().toString(), user.getBuckleId().toString(),
                            user.getMasterId().toString(), user.getName(), user.getUnitName());
                }else {
                    callback.invoke(false, "Could not find any user");
                }
            }
        }catch (Throwable t){
            LogManager.logMessage(CLASSNAME + ".loadIndividualUserInfo", t.getMessage());
            callback.invoke(false, t.getMessage());
        }
    }

    @ReactMethod
    public void register(String userId, String userName, String buckleId, String unitName, String locationName, Callback callback) {
        try{
            this.scanModule.register(userId, new UICallBack(callback),userName, buckleId, unitName, locationName);
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".register", t.getMessage());
            callback.invoke(t.getMessage());
        }
    }

    @ReactMethod
    public void rejectAttendance(String attendanceId,
                                 final Callback cbk) {
        try {
            Attendance attendance = new Attendance();
            attendance.setId(new Long(attendanceId));
            attendance.setStatus(0);
            Call<Attendance> attendanceCall = FPDataStore.attendanceService.updateAttendance(attendance);
            Response<Attendance> res = attendanceCall.execute();
            if(res.isSuccessful()){
                cbk.invoke(true,200,attendanceId);
            }
        } catch (Throwable t) {
            LogManager.logMessage(CLASSNAME + ".rejectAttendance", t.getMessage());
            cbk.invoke(false, -1, t.getMessage());
        }
    }

    @ReactMethod
    public void recordAttendanceResult(String searchedUserId, String attendanceId){
        FPDataStore.storeCurrentResultToServer(searchedUserId, new Long(attendanceId));
    }

    @ReactMethod
    public void recordAttendance(String userId, String latitude, String longitude,
                                 String locationId, String scanId,
                                 final boolean result,
                                 final Promise promise){
        try{
            Users user = new Users(); user.setId(new Long(userId));
            Location location = new Location();
            location.setId(new Long(locationId));
            Attendance attendance = new Attendance();
            attendance.setUser(user);attendance.setLatitude(new Double(latitude));
            attendance.setLongitude(new Double(longitude));attendance.setLocation(location);
            if(scanId!=null)
                attendance.setScanId(new Long(scanId));

            Call<Attendance> attendanceCall = FPDataStore.attendanceService.recordAttendance(attendance);
            attendanceCall.enqueue(new retrofit2.Callback<Attendance>() {
                @Override
                public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                    WritableMap map = Arguments.createMap();
                    try{
                        if(response.isSuccessful()){
                            Attendance attendance = response.body();
                            map.putBoolean("result", true);
                            map.putString("message", "Success");
                            map.putString("attendanceId", new Long(attendance.getId()).toString());
                            promise.resolve(map);
                        }else{
                            sendFailureMessage("Unable to create attendance", promise);
                        }
                    }catch (Throwable t){
                        LogManager.logMessage(CLASSNAME + ".recordAttendance", t.getMessage());
                        sendFailureMessage("Error creating attendance " + t.getMessage(), promise);
                    }
                }

                @Override
                public void onFailure(Call<Attendance> call, Throwable t) {
                    try{
                        sendFailureMessage("Error creating attendance " + t.getMessage(), promise);
                    }catch (Throwable t1){
                        LogManager.logMessage(CLASSNAME + ".recordAttendance", t.getMessage());
                        sendFailureMessage("Error creating attendance " + t.getMessage(), promise);
                    }

                }
            });
        }catch (Throwable t){
            LogManager.logMessage(CLASSNAME + ".recordAttendance", t.getMessage());
            sendFailureMessage("Error creating attendance " + t.getMessage(), promise);
        }
    }

    private void sendFailureMessage(String message, Promise promise){
        WritableMap map = Arguments.createMap();
        map.putBoolean("result", false);
        map.putString("errorCode", "-1");
        map.putString("message", "Error creating attendance " + message);
        promise.resolve(map);
    }
    @ReactMethod
    public void authenticate(Promise result) {
        //"Getting string as bytes".getBytes();
        try{
            this.scanModule.authenticate(new UICallBack(result));            
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".authenticate", t.getMessage());
            sendFailureMessage(t.getMessage(), result);
        }
    }

    @ReactMethod
    public void userAuthenticate(String userId,Promise result) {
        //"Getting string as bytes".getBytes();
        try{
            this.scanModule.userAuthenticate(userId, new UICallBack(result));
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".authenticate", t.getMessage());
            sendFailureMessage(t.getMessage(), result);
        }
    }

    @ReactMethod
    public void recordAuthenticate(Promise result) {
        //"Getting string as bytes".getBytes();
        try{
            this.scanModule.recordAuthenticate(new UICallBack(result));
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".authenticate", t.getMessage());
            sendFailureMessage(t.getMessage(), result);
        }
    }

    @ReactMethod
    public void stopCapture() {
        try{
            DeviceManager.getDevice().stopCapture();
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".stopCapture", t.getMessage());
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }
}

class UICallBack implements ScanUICallback{
    Callback callback;
    Promise callbackPromise;
    String CLASSNAME = "UICallBack";
    UICallBack(Callback cbk){
        callback = cbk;
    }
    UICallBack(Promise promise){
        callbackPromise = promise;
    }

    @Override
    public void scanFailed(String message) {
        try{
            callback.invoke(false, -1, message);    
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".scanFailed", t.getMessage());
            callback.invoke(false, -1, t.getMessage());
        }
    }

    @Override
    public void processRegistration(boolean result, String id, String message) {
        try{
            callback.invoke(result, id, message);
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".processRegistration", t.getMessage());
            callback.invoke(false, -1, t.getMessage());
        }
    }

    private long getUserForScan(String id) throws IOException {
        Call loadScanData = FPDataStore.scanService.getScanData(new Long(id));
        Response<ScanData> res = loadScanData.execute();
        if(res.isSuccessful()){
            ScanData scanData = res.body();
            return scanData.getUser().getId();
        }else{
            throw new IOException("Unable to find user");
        }
    }

    @Override
    public void processAuthentication(boolean result, String id, String userId, String userName,
                                      String buckleId, String unitName, String message,byte[] scan) {
        WritableMap map = Arguments.createMap();
        try{
            if(result){
                map.putBoolean("result", true);
                map.putString("scanId", id);
                map.putString("userId", userId);
                map.putString("buckleId", buckleId);
                map.putString("userName", userName);
                map.putString("unitName", unitName);
                map.putString("message", message);
            }else{
                map.putBoolean("result", false);
                map.putString("message", message);
            }
            FPDataStore.storeCurrentScanResultInMemory(scan, message, result);
            callbackPromise.resolve(map);
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".processAuthentication", t.getMessage());
            sendFailureMessage(t.getMessage(), "-11", callbackPromise);
        }
    }

    @Override
    public void processAuthentication(boolean result, String id, String message, byte[] scan) {
        WritableMap map = Arguments.createMap();
        try{
            if(result) {
                map.putBoolean("result", true);
                map.putString("scanId", id);
                map.putString("message", message + scan);
                FPDataStore.storeCurrentScanResultInMemory(scan, message, result);
                callbackPromise.resolve(map);
            }else {
                FPDataStore.storeCurrentScanResultInMemory(scan, message, result);
                // This is the actual finger print mismatch, so send the passed in id as the status
                sendFailureMessage("Could not find any user", id, callbackPromise);
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".processAuthentication", t.getMessage());
            sendFailureMessage(t.getMessage(), "-11", callbackPromise);
        }
    }

    private void sendFailureMessage(String message, String status, Promise promise){
        try{
            WritableMap map = Arguments.createMap();
            map.putBoolean("result", false);
            map.putString("errorCode", status);
            map.putString("message", "Error creating attendance " + message);
            promise.resolve(map);
        }catch (Throwable t){}
    }

    @Override
    public void processMessage(String message, int status){
        try{
            if(callback != null){
                callback.invoke(message, status);
            }
            if(callbackPromise != null){
                sendFailureMessage(message, "-11", callbackPromise);
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".processMessage", t.getMessage());
            try{
                if(callback != null){
                    callback.invoke(t.getMessage(), status);
                }
                if(callbackPromise != null){
                    sendFailureMessage(t.getMessage(), "-11", callbackPromise);
                }
            }catch(Throwable t1){
                LogManager.logMessage(CLASSNAME + ".processMessage", t1.getMessage());
            }
        }
    }
}