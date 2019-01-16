package com.learnym.scan.api.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Users.
 */
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String buckleId;

    private Long masterId;

    private String name;

    private String unitName;

    private Location location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuckleId() {
        return buckleId;
    }

    public Users buckleId(String buckleId) {
        this.buckleId = buckleId;
        return this;
    }

    public void setBuckleId(String buckleId) {
        this.buckleId = buckleId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public Users masterId(Long masterId) {
        this.masterId = masterId;
        return this;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getName() {
        return name;
    }

    public Users name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return unitName;
    }

    public Users unitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Users users = (Users) o;
        if (users.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), users.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + getId() +
                ", buckleId='" + getBuckleId() + "'" +
                ", masterId='" + getMasterId() + "'" +
                ", name='" + getName() + "'" +
                ", unitName='" + getUnitName() + "'" +
                "}";
    }
}
