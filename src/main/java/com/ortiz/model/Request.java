package com.ortiz.model;

import com.ortiz.enums.RequestType;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Request implements Serializable {
    private final RequestType requestType;
    private final String jsonString;

    public Request(RequestType requestType) {
        this.requestType = requestType;
        this.jsonString = "";
    }

    public Request(RequestType requestType, String jsonString) {
        this.requestType = requestType;
        this.jsonString = jsonString;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    @Nullable
    public JSONObject getJson() {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestType=" + requestType +
                ", jsonString='" + jsonString + '\'' +
                '}';
    }
}