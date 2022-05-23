package com.byko.api_3d_printing.model;

public class LastTimeActiveResponse {

    public long lastTimeActive;

    public LastTimeActiveResponse(){}

    public LastTimeActiveResponse(long lastTimeActive) {
        this.lastTimeActive = lastTimeActive;
    }

    public long getLastTimeActive() {
        return lastTimeActive;
    }

    public void setLastTimeActive(long lastTimeActive) {
        this.lastTimeActive = lastTimeActive;
    }
}
