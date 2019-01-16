/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.integrator;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.learnym.scan.datastore.LogManager;
import com.learnym.scan.device.DeviceManager;

/**
 *
 * @author Admin
 */
public class ServerSideEventManager implements ScanCallBack{
    private ReactContext reactContext;
    public ServerSideEventManager(ReactContext context) {
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
        if(status == 1){
            this.sendEvent(this.reactContext, "SERVERCALLED", null);
        }
        if(status == 2){
            this.sendEvent(this.reactContext, "SERVERRETURNED", null);
        }
        if(status == 3){
            this.sendEvent(this.reactContext, "SCANSTARTED", null);
        }
        if(status == 4){
            this.sendEvent(this.reactContext, "SCANENDED", null);
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
