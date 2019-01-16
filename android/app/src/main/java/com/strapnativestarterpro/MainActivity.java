package com.strapnativestarterpro;

import android.os.Bundle;
import com.facebook.react.ReactActivity;
import com.learnym.scan.datastore.LogManager;
import com.learnym.scan.device.DeviceManager;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends ReactActivity {

    private static String CLASSNAME = "MainActivity";
    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "StrapNativeStarterPro";
    }

    @Override
    protected void onStart() {
      try{
        boolean isNew = DeviceManager.createDevice(this);
        if(isNew){
          DeviceManager.initDevice();
        }
        LogManager.logMessage(CLASSNAME + ".onStart","On Start Called");
      }catch(Throwable t){
          LogManager.logMessage(CLASSNAME + ".onStart", t.getMessage());
      }
      super.onStart();
    }

    protected void onStop() {
      try{
        DeviceManager.uninitDevice();
      }catch(Throwable t){
          LogManager.logMessage(CLASSNAME + ".onStart", t.getMessage());
      }
      super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      try{
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
      }catch(Throwable t){
          LogManager.logMessage(CLASSNAME + ".onStart", t.getMessage());
      }
    }
}
