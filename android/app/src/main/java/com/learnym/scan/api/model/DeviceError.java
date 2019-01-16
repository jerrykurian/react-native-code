package com.learnym.scan.api.model;

/**
 * Created by Jerry Kurian on 26-11-2017.
 */
import java.util.Date;
public class DeviceError {
    private static final long serialVersionUID = 1L;

    private Long id;
    private long deviceLocation;
    private String message;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getDeviceLocation() {
        return deviceLocation;
    }
    public void setDeviceLocation(long deviceLocation) {
        this.deviceLocation = deviceLocation;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "DeviceError{" +
                "id=" + id +
                ", deviceLocation=" + deviceLocation +
                ", message='" + message + '\'' +
                '}';
    }

}
