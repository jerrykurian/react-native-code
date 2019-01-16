/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.integrator;

import com.learnym.scan.api.model.ScanData;
import com.learnym.scan.api.model.Users;
import com.learnym.scan.ui.ScanUICallback;
import com.learnym.scan.datastore.*;

import retrofit2.Call;

/**
 *
 * @author Admin
 */
public class RegistrationManager implements ScanCallBack {
    private ScanUICallback uiCbk;
    private String userId;
    private String userName;
    private String buckleId;
    private String unitName;
    private String locationName;
    private static String CLASSNAME = "RegistrationManager";
    private ServerSideEventManager serverSideEventManager;

    public RegistrationManager(ServerSideEventManager serverSideEventManager,ScanUICallback cbk,
                               String userId, String userName, String buckleId, String unitName,
                               String locationName) {
        this.serverSideEventManager = serverSideEventManager;
        this.uiCbk = cbk;
        this.userId = userId;
        this.userName = userName;
        this.unitName = unitName;
        this.buckleId = buckleId;
        this.locationName = locationName;
    }

    @Override
    public void processScanData(byte[] scanData) {
        try{
            if(this.serverSideEventManager != null){
                this.serverSideEventManager.processStatus("Started", 1);
            }
            Users user = FPDataStore.addData(this.userId, userName, buckleId, unitName, locationName, scanData);
            if(user!=null){
                uiCbk.processRegistration(true, user.getId().toString(), "User Added");
            }else{
                uiCbk.processRegistration(false, "-1", "User Not Added");
            }
            if(this.serverSideEventManager != null){
                this.serverSideEventManager.processStatus("Ended", 2);
            }
        }catch (Throwable t){
            LogManager.logMessage(CLASSNAME + ".processScanData", t.getMessage());
            uiCbk.processRegistration(false, "-1", t.getMessage());
            if(this.serverSideEventManager != null){
                this.serverSideEventManager.processStatus("Ended", 2);
            }
        }
    }

    @Override
    public void processFailed() {
        uiCbk.scanFailed("Could not scan for User id " + userId);
    }

    @Override 
    public void processStatus(String message, int status){
        this.uiCbk.processMessage(message, status);
    }
    
}
