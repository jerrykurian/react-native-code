package com.device.mantra;

import android.app.Activity;

import com.learnym.scan.device.DeviceManager;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;

import java.io.File;
import java.io.FileOutputStream;

import com.learnym.scan.integrator.ScanCallBack;

public class MF100Device implements MFS100Event {

    int timeout = 10000;
    static MFS100 mfs100 = null;

    public static String _testKey = "";
    
    void SetTextOnUIThread(String message){
    
    }
    
    void SetLogOnUIThread(String message){
    
    }
    private Activity activity;
    public MF100Device(Activity activity){
        this.activity = activity;
        mfs100 = new MFS100(this);
        mfs100.SetApplicationContext(this.activity);
    }
    
    public int init() {
        int ret = 0;
        try {
            ret = mfs100.Init();
            
            if (ret != 0) {
                SetTextOnUIThread(mfs100.GetErrorMsg(ret));
            } else {
                SetTextOnUIThread("Init success");
                String info = "Serial: " + mfs100.GetDeviceInfo().SerialNo()
                        + " Make: " + mfs100.GetDeviceInfo().Make()
                        + " Model: " + mfs100.GetDeviceInfo().Model()
                        + "\nCertificate: " + mfs100.GetCertification();
                SetLogOnUIThread(info);
            }
            ScanCallBack deviceCallBack = DeviceManager.getDeviceCallBack();

            if(deviceCallBack != null){
                deviceCallBack.processStatus(mfs100.GetErrorMsg(ret), 0);
            }
            return ret; 
        } catch (Exception ex) {
            SetTextOnUIThread("Init failed, unhandled exception");
            return -1; 
        }
    }
    
    public int UnInit() {
        int ret = 0; 
        try {
            ret = mfs100.UnInit();
            if (ret != 0) {
                SetTextOnUIThread(mfs100.GetErrorMsg(ret));
            } else {
                SetTextOnUIThread("Uninit Success");
            }
        } catch (Exception e) {
            // sLog.e("UnInitScanner.EX", e.toString());
        }
        if (mfs100 != null) {
            mfs100.Dispose();
        }
        
        return ret; 
    }
    
    public MFS100 getDevice(){
        return mfs100;
    }
    
    public void startFingerPrintCapture(ScanCallBack callBack ) {
        FPCaputureThread scannerThread = new FPCaputureThread(callBack, this.mfs100); 
        scannerThread.start();
    }

    public void stopCapture() {
        try {
            mfs100.StopAutoCapture();
        } catch (Exception e) {
            SetTextOnUIThread("Error");
        }
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        ScanCallBack deviceCallBack = DeviceManager.getDeviceCallBack();
        try{
            int ret;
            if (!hasPermission) {
                SetTextOnUIThread("Permission denied");
                if(deviceCallBack != null){
                    deviceCallBack.processStatus("Permission denied", 300);
                }
                return;
            }
            if (vid == 1204 || vid == 11279) {
                if (pid == 34323) {
                    ret = mfs100.LoadFirmware();
                    
                    if (ret != 0) {
                        SetTextOnUIThread(mfs100.GetErrorMsg(ret));
                        if(deviceCallBack != null){
                            deviceCallBack.processStatus(mfs100.GetErrorMsg(ret), 301);
                        }
                    } else {
                        SetTextOnUIThread("Load firmware success");
                        if(deviceCallBack != null){
                            deviceCallBack.processStatus("Load firmware success", 200);
                        }
                    }
                    
                } else if (pid == 4101) {
                    String key = "Without Key";
                    ret = mfs100.Init("");
                    if (ret == -1322) {
                        key = "Test Key";
                        ret = mfs100.Init(_testKey);
                    }
                    if (ret == 0) {
                        showSuccessLog(key);
                        if(deviceCallBack != null){
                            deviceCallBack.processStatus(key, 200);
                        }
                    } else {
                        SetTextOnUIThread(mfs100.GetErrorMsg(ret));
                        if(deviceCallBack != null){
                            deviceCallBack.processStatus(mfs100.GetErrorMsg(ret), 302);
                        }
                    }

                }
            }else{
                if(deviceCallBack != null){
                    deviceCallBack.processStatus("Unable to connect", 500);
                }
            }
        }catch(Throwable t){
            if(deviceCallBack != null){
                deviceCallBack.processStatus(t.getMessage(), 500);
            }
        }
    }

    private void showSuccessLog(String key) {
        SetTextOnUIThread("Init success");
        String info = "\nKey: " + key + "\nSerial: "
                + mfs100.GetDeviceInfo().SerialNo() + " Make: "
                + mfs100.GetDeviceInfo().Make() + " Model: "
                + mfs100.GetDeviceInfo().Model()
                + "\nCertificate: " + mfs100.GetCertification();
        SetLogOnUIThread(info);
    }

    @Override
    public void OnDeviceDetached() {
        try{
            ScanCallBack deviceCallBack = DeviceManager.getDeviceCallBack();
            if(deviceCallBack != null){
                deviceCallBack.processStatus("Device removed", -1);
            }
        }catch (Throwable t){

        }
    }
    
    @Override
    public void OnHostCheckFailed(String s) {

    }
    
    
}

class FPCaputureThread extends Thread{
    private ScanCallBack scanCallBack = null;
    private MFS100 mfs100 = null; 
    byte[] capturedImage = null; 
    
    int timeout = 0;
    int quality = 56;
    boolean falseDetection = false; 
    boolean preview = false;
    
    public FPCaputureThread(ScanCallBack scanCB, MFS100 tmpMfs100){
        scanCallBack = scanCB; 
        mfs100 = tmpMfs100; 
    }    

    public void run(){
        try{
            FingerData fingerData = new FingerData();
            int ret = mfs100.AutoCapture(fingerData, timeout, falseDetection);
            if (ret != 0) {
                scanCallBack.processStatus(mfs100.GetErrorMsg(ret), ret);
            } else {
                // Old code
                // capturedImage = new byte[fingerData.ISOTemplate().length];
                // System.arraycopy(fingerData.ISOTemplate(), 0, capturedImage, 0, fingerData.ISOTemplate().length);
                // Old code
                
                byte[] tmpData = new byte[2000]; // length 2000 is mandatory
                int dataLen = mfs100.ExtractANSITemplate(fingerData.RawData(), tmpData);
                if (dataLen <= 0) {
                    scanCallBack.processStatus(mfs100.GetErrorMsg(ret), ret);
                }
                else {
                    capturedImage = new byte[dataLen];
                    System.arraycopy(tmpData, 0, capturedImage, 0, dataLen);
                }
                this.scanCallBack.processScanData(capturedImage);
            }
        }
        catch (Throwable ex) {
            scanCallBack.processStatus(ex.getMessage(), -1);
        }
    }
}

