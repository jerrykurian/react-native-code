/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.integrator;

import com.learnym.scan.datastore.FPDataStore;
import com.learnym.scan.datastore.LogManager;
import com.learnym.scan.datastore.UserDetails;
import com.learnym.scan.device.DeviceManager;
import com.learnym.scan.ui.ScanUICallback;

import java.util.List;

/**
 *
 * @author Admin
 */
public class UserAuthenticationManager implements ScanCallBack{
    private ScanUICallback uiCbk;
    private ServerSideEventManager serverSideEventManager;
    private String userId;
    public UserAuthenticationManager(String userId,ScanUICallback cbk, ServerSideEventManager serverSideEventManager) {
        this.uiCbk = cbk;
        this.serverSideEventManager = serverSideEventManager;
        this.userId = userId;
    }

    @Override
    public void processFailed() {
        this.uiCbk.scanFailed("");
    }
    
    public void processScanData(byte[] candidateData) {
        try{

            List<UserDetails> fpList = FPDataStore.getData();
            boolean matchFound = false
                    ;
            int score = 0;
            if(candidateData != null && candidateData.length != 0){
                for(UserDetails fp:fpList){
                    if(fp.userId.equals(this.userId)) {
                        score = DeviceManager.getDevice().getDevice().MatchISO(candidateData, fp.FPData);
                        if (score >= 1400) {
                            this.uiCbk.processAuthentication(true, fp.id, fp.userId,
                                    fp.name, fp.buckleId, fp.unitName, new Integer(score).toString(), candidateData);
                            matchFound = true;
                        }
                        break;
                    }
                }
                if(!matchFound){
                    this.uiCbk.processAuthentication(false, "-1", new Integer(score).toString(), candidateData);
                }
            }
        }catch (Throwable t){
            LogManager.logMessage("AuthenticationManager.processScanData", t.getMessage());
        }
    }

    @Override 
    public void processStatus(String message, int status){
        this.uiCbk.processAuthentication(false, new Integer(status).toString(), message, null);
    }
}
