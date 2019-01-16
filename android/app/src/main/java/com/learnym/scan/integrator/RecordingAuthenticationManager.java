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
public class RecordingAuthenticationManager implements ScanCallBack{
    private ScanUICallback uiCbk;
    private ServerSideEventManager serverSideEventManager;
    public RecordingAuthenticationManager(ScanUICallback cbk, ServerSideEventManager serverSideEventManager) {
        this.uiCbk = cbk;
        this.serverSideEventManager = serverSideEventManager;
    }

    @Override
    public void processFailed() {
        this.uiCbk.scanFailed("");
    }
    
    public void processScanData(byte[] candidateData) {
        try{
            this.uiCbk.processAuthentication(true, "100", "Scan Done", candidateData);
        }catch (Throwable t){
            LogManager.logMessage("AuthenticationManager.processScanData", t.getMessage());
        }
    }

    @Override 
    public void processStatus(String message, int status){
        this.uiCbk.processAuthentication(false, new Integer(status).toString(), message, null);
    }
}
