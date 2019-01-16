/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.integrator;

import com.device.mantra.MF100Device;
import com.learnym.scan.device.DeviceManager;
import com.learnym.scan.ui.ScanUICallback;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import com.learnym.scan.datastore.*;

/**
 *
 * @author Admin
 */
public class AuthenticationManager implements ScanCallBack{
    private ScanUICallback uiCbk;
    private ServerSideEventManager serverSideEventManager;
    public AuthenticationManager(ScanUICallback cbk, ServerSideEventManager serverSideEventManager) {
        this.uiCbk = cbk;
        this.serverSideEventManager = serverSideEventManager;
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
                    score = DeviceManager.getDevice().getDevice().MatchISO(candidateData, fp.FPData);
                    if (score >= 1400) {
                        this.uiCbk.processAuthentication(true, fp.id, fp.userId,
                                fp.name,fp.buckleId,fp.unitName,new Integer(score).toString(), candidateData);
                        matchFound = true;
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
