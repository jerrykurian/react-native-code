/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.integrator;

import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.learnym.scan.datastore.LogManager;
import com.learnym.scan.device.DeviceManager;
/**
 *
 * @author Admin
 */
public class DeviceEventManager implements ScanCallBack{
    private ReactContext reactContext;
    public DeviceEventManager(ReactContext context) {
        this.reactContext = context;
    }
    private static String CLASSNAME = "DeviceEventManager";

    @Override
    public void processFailed() {
    }
    
    public void processScanData(byte[] candidateData) {
    }

    @Override 
    public void processStatus(String message, int status){
        if(status == -1){
            this.sendEvent(this.reactContext, "DEVICEDETACHED", null);
            DeviceManager.disconnect();
        }
        if(status >=200 && status < 300){
            this.sendEvent(this.reactContext, "DEVICEATTACHED", null);   
            DeviceManager.initDevice();
        }
        if(status >300){
            this.sendEvent(this.reactContext, "DEVICEFAILED", null);
            DeviceManager.disconnect();
            LogManager.logMessage(CLASSNAME + ".processStatus","DeviceFailed");
        }
    }

    private void sendEvent(ReactContext reactContext,
                       String eventName,
                       WritableMap params) {
      this.reactContext
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
          .emit(eventName, params);
    }

}
