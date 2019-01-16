package com.strapnativestarterpro;

import com.learnym.scan.datastore.LogManager;

/**
 * Created by Jerry Kurian on 08-12-2017.
 */

public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try{
            LogManager.logMessage(thread.getName(), throwable.getMessage());
        }catch (Throwable t){

        }
    }
}
