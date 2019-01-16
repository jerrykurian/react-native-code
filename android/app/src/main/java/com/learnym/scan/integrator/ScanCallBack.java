/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learnym.scan.integrator;

/**
 *
 * @author Admin
 */
public interface ScanCallBack {
    public void processScanData(byte [] scanData);
    public void processFailed();
    public void processStatus(String message, int status);
}
