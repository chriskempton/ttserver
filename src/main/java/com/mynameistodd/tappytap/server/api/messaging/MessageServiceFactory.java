package com.mynameistodd.tappytap.server.api.messaging;

/**
 * Created with IntelliJ IDEA.
 * User: ckempton
 * Date: 10/27/13
 */
public class MessageServiceFactory {
    public static IMessageService getInstance() {
        // GCM only supported Message Service at this time
        return new GcmMessageService();
    }
}
