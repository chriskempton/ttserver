package com.mynameistodd.tappytap.server.api.messaging;

import com.google.android.gcm.server.*;
import com.mynameistodd.tappytap.server.api.ApiKeyInitializer;
import com.mynameistodd.tappytap.server.data.Device;
import com.mynameistodd.tappytap.server.data.MessageSend;
import com.mynameistodd.tappytap.server.data.TappyTapMessage;
import com.mynameistodd.tappytap.server.data.User;

import javax.servlet.ServletConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: ckempton
 * Date: 10/26/13
 */
public class GcmMessageService implements IMessageService {

    private Sender sender;
    protected final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void send(MessageSend messageSend) throws IOException {
        logger.info("Sending message to device " + messageSend.getRecipient().getDeviceId());
        Message message = createMessage(messageSend.getTappyTapMessage().getMessageText());
        Result result;
        try {
            result = sender.sendNoRetry(message, messageSend.getRecipient().getDeviceId());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception posting " + message, e);
            throw e;
        }
        if (result == null) {
            throw new NullPointerException("Sender result was null");
        }
        if (result.getMessageId() != null) {
            messageSend.save();
            logger.info("Successfully sent message to device " + messageSend.getRecipient().getDeviceId());
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // same device has more than one registration id: update it
                logger.finest("canonicalRegId " + canonicalRegId);
                Device recipientDevice = Device.findById(messageSend.getRecipient().getDeviceId());
                recipientDevice.updateRegistration(canonicalRegId);
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // application has been removed from device - unregister it
                Device theDevice = new Device();
                theDevice.setDeviceId(messageSend.getRecipient().getDeviceId());
                theDevice.remove();
            } else {
                logger.severe("Error sending message to device " + messageSend.getRecipient().getDeviceId()
                        + ": " + error);
            }
        }
    }

    @Override
    public List<MessageSend> sendMulticastReturnRetries(List<MessageSend> messageSends) throws IOException {
        List<String> regIds = new ArrayList<>();
        TappyTapMessage tappyTapMessage = null;
        String senderId = null;
        for(MessageSend messageSend:messageSends){
            regIds.add(messageSend.getRecipient().getDeviceId());
            tappyTapMessage = messageSend.getTappyTapMessage();
            senderId = messageSend.getSender().getEmail();
        }
        Message message = createMessage(tappyTapMessage.getMessageText());
        MulticastResult multicastResult;
        try {
            multicastResult = sender.sendNoRetry(message, regIds);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception posting " + message, e);
            throw e;
        }
        // check if any registration id must be updated
        if (multicastResult.getCanonicalIds() != 0) {
            List<Result> results = multicastResult.getResults();
            for (int i = 0; i < results.size(); i++) {
                String canonicalRegId = results.get(i).getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    String regId = regIds.get(i);
                    Device recipientDevice = Device.findById(regId);
                    recipientDevice.updateRegistration(canonicalRegId);
                }
            }
        }
        List<MessageSend> retriableMessageSends = new ArrayList<>();
        if (multicastResult.getFailure() != 0) {
            // there were failures, check if any could be retried
            List<Result> results = multicastResult.getResults();
            for (int i = 0; i < results.size(); i++) {
                String error = results.get(i).getErrorCodeName();
                if (error != null) {
                    String regId = regIds.get(i);
                    logger.warning("Got error (" + error + ") for regId " + regId);
                    if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                        // application has been removed from device - unregister it
                        Device theDevice = Device.findById(regId);
                        theDevice.remove();
                    }
                    if (error.equals(Constants.ERROR_UNAVAILABLE)) {
                        MessageSend messageSend = new MessageSend();
                        messageSend.setTappyTapMessage(tappyTapMessage);
                        messageSend.setRecipient(Device.findById(regId));
                        messageSend.setSender(User.findByEmail(senderId));
                        retriableMessageSends.add(messageSend);
                    }
                }
            }
        }
        return retriableMessageSends;
    }

    @Override
    public void init(ServletConfig config) {
        sender = newSender(config);
    }

    /**
     * Creates the {@link Sender} based on the servlet settings.
     */
    protected Sender newSender(ServletConfig config) {
        String key = ApiKeyInitializer.getAccessKey();
        return new Sender(key);
    }

    private Message createMessage(String messageText) {
        return new Message.Builder().addData("key1", messageText).build();
    }
}
