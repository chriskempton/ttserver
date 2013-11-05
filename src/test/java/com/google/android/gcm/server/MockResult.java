package com.google.android.gcm.server;

public class MockResult {

    public static Result getMockResult() {
        return new Result.Builder().errorCode("").messageId("Chris").build();
    }

}