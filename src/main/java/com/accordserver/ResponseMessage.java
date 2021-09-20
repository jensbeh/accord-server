package com.accordserver;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

public class ResponseMessage {
    private String status;
    private String message;
    private Object data;

    public ResponseMessage(String status, String message, JsonObject data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseMessage(String status, String message, JsonArray jsonArrayData) {
        this.status = status;
        this.message = message;
        this.data = jsonArrayData;
    }

    /**
     * Getter are needed to return an auto-generated answer to the Rest-call
     */
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
