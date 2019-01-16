/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.ui;

import com.device.mantra.MF100Device;
import com.learnym.scan.datastore.LogManager;
import com.learnym.scan.device.DeviceManager;
import com.learnym.scan.integrator.AuthenticationManager;
import com.learnym.scan.integrator.RecordingAuthenticationManager;
import com.learnym.scan.integrator.RegistrationManager;
import com.learnym.scan.integrator.ScanCallBack;
import com.learnym.scan.integrator.ServerSideEventManager;
import com.learnym.scan.integrator.UserAuthenticationManager;

/**
 *
 * @author Admin
 */
public class ScanModule{
    public static final String REACT_CLASS = "Geoattendance";
    private String CLASSNAME = "ScanModule";
    private ServerSideEventManager serverEventsManager;
    public ScanModule(ServerSideEventManager serverEventsManager){
        this.serverEventsManager = serverEventsManager;
    }
   
    public int initDevice(){
        try{
            MF100Device device = DeviceManager.getDevice();
            if(device != null){
                return DeviceManager.getDevice().init();
            }else {
                return -1;
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".initDevice", t.getMessage());
            return -1;
        }
        
    }
    
    public void register(String id, ScanUICallback cbk, String userName, String buckleId, String unitName, String locationId ){
        ScanCallBack call = new RegistrationManager(this.serverEventsManager, cbk, id, userName, buckleId, unitName, locationId);
        try{
            MF100Device device = DeviceManager.getDevice();
            if(device != null) {
                device.startFingerPrintCapture(call);
            }else{
                if(cbk != null){
                    cbk.processMessage("No Device Found",-1);
                }
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".register", t.getMessage());
            cbk.processMessage(t.getMessage(),-1);
        }
        
    }

    public void authenticate(ScanUICallback cbk){
        ScanCallBack call = new AuthenticationManager(cbk, this.serverEventsManager);
        try{
            MF100Device device = DeviceManager.getDevice();
            if(device != null) {
                device.startFingerPrintCapture(call);
            }else{
                if(cbk != null){
                    cbk.processMessage("No Device Found",-11);
                }
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".authenticate", t.getMessage());
            cbk.processMessage(t.getMessage(),-11);
        }
        
    }

    public void userAuthenticate(String userId, ScanUICallback cbk){
        ScanCallBack call = new UserAuthenticationManager(userId, cbk, this.serverEventsManager);
        try{
            MF100Device device = DeviceManager.getDevice();
            if(device != null) {
                device.startFingerPrintCapture(call);
            }else{
                if(cbk != null){
                    cbk.processMessage("No Device Found",-11);
                }
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".authenticate", t.getMessage());
            cbk.processMessage(t.getMessage(),-11);
        }

    }

    public void recordAuthenticate(ScanUICallback cbk){
        ScanCallBack call = new RecordingAuthenticationManager(cbk, this.serverEventsManager);
        try{
            MF100Device device = DeviceManager.getDevice();
            if(device != null) {
                device.startFingerPrintCapture(call);
            }else{
                if(cbk != null){
                    cbk.processMessage("No Device Found",-11);
                }
            }
        }catch(Throwable t){
            LogManager.logMessage(CLASSNAME + ".authenticate", t.getMessage());
            cbk.processMessage(t.getMessage(),-11);
        }

    }
}
