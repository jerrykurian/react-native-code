package com.learnym.scan.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * Created by Jerry Kurian on 28-11-2017.
 */

public class Attendance {
    private static final long serialVersionUID = 1L;

    private Long id;
    @JsonInclude
    private Users user;
    @JsonInclude
    private Location location;
    private Double latitude;
    private Double longitude;
    private int status = 1;
    private long scanId = 0;
    private Date scanTime;
    private int scanType = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Attendance latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Attendance longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public long getScanId() {
        return scanId;
    }

    public void setScanId(long scanId) {
        this.scanId = scanId;
    }

    public Date getScanTime() {
        return scanTime;
    }

    public void setScanTime(Date scanTime) {
        this.scanTime = scanTime;
    }

    public int getScanType() {
        return scanType;
    }

    public void setScanType(int scanType) {
        this.scanType = scanType;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + getId() +
                ", latitude='" + getLatitude() + "'" +
                ", longitude='" + getLongitude() + "'" +
                "}";
    }
}
