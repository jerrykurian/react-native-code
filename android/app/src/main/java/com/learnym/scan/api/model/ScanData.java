package com.learnym.scan.api.model;

import java.util.Objects;

/**
 * Created by Jerry Kurian on 26-11-2017.
 */

public class ScanData {
    private static final long serialVersionUID = 1L;

    private Long id;

    private byte[] scanData;

    private Integer fingerId;

    private Users user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getScanData() {
        return scanData;
    }

    public ScanData scanData(byte[] scanData) {
        this.scanData = scanData;
        return this;
    }

    public void setScanData(byte[] scanData) {
        this.scanData = scanData;
    }

    public Integer getFingerId() {
        return fingerId;
    }

    public ScanData fingerId(Integer fingerId) {
        this.fingerId = fingerId;
        return this;
    }

    public void setFingerId(Integer fingerId) {
        this.fingerId = fingerId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScanData scanData = (ScanData) o;
        if (scanData.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), scanData.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ScanData{" +
                "id=" + getId() +
                ", scanData='" + getScanData() + "'" +
                ", fingerId='" + getFingerId() + "'" +
                ", users='" + getUser() + "'" +
                "}";
    }
}

