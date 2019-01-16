/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.ui;

/**
 *
 * @author Admin
 */
public interface ScanUICallback {
    void scanFailed(String message);
    void processRegistration(boolean result, String id, String message);
    void processAuthentication(boolean result, String id,String userId, String userName, String buckleId, String unitName,
                               String message, byte[] scans);
    void processAuthentication(boolean result, String id, String messag, byte[] scans);
    void processMessage(String message, int status);
}
