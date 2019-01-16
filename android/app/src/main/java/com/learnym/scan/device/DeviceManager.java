package com.learnym.scan.device;

import com.device.mantra.MF100Device;
import com.learnym.scan.datastore.LogManager;
import com.learnym.scan.integrator.DeviceEventManager;
import com.learnym.scan.integrator.ScanCallBack;

import android.app.Activity;

public class DeviceManager {
    private static MF100Device device;
    private static int deviceInitState = -1;
    private static boolean isConnected = false;
    private static String CLASSNAME = "DeviceManager";

    public static boolean createDevice(Activity activity) {
        if (device == null) {
            device = new MF100Device(activity);
            return true;
        } else {
            return false;
        }
    }

    public static void initDevice() {
        try {
            deviceInitState = device.init();
            if (deviceInitState == 0) {
                isConnected = true;
            } else {
                isConnected = false;
            }
        } catch (Throwable t) {
            LogManager.logMessage(CLASSNAME + ".initDevice", t.getMessage());
        }
    }

    private static ScanCallBack deviceCallBack;
    public static void startListeningForDeviceEvents(ScanCallBack callBack){
        deviceCallBack = callBack;
    }

    public static void stopListeningForDeviceEvents(){
        deviceCallBack = null;
    }

    public static ScanCallBack getDeviceCallBack(){
        return deviceCallBack;
    }

    public static void uninitDevice() {
        try {
            if(device != null){
                device.UnInit();
                device = null;
                isConnected = false;
            }
        } catch (Throwable t) {
            LogManager.logMessage(CLASSNAME + ".uninitDevice",t.getMessage());
        }
    }

    public static MF100Device getDevice() {
        return device;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void disconnect() {
        isConnected = false;
    }
}