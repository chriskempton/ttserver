package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.List;

import static com.mynameistodd.tappytap.server.data.util.OfyService.ofy;

/**
 * User: ckempton
 * Date: 11/1/13
 */
@Entity
public class MulticastMessage extends TappyTapData {

    @Id
    @Load
    String multicastIdentifier;
    @Index
    @Load
    Ref<TappyTapMessage> tappyTapMessage;
    List<Device> recipientDevices;
    @Index
    @Load
    Ref<User> sender;

    public String getMulticastIdentifier() {
        return multicastIdentifier;
    }

    public void setMulticastIdentifier(String multicastIdentifier) {
        this.multicastIdentifier = multicastIdentifier;
    }

    public TappyTapMessage getTappyTapMessage() {
        return tappyTapMessage.getValue();
    }

    public void setTappyTapMessage(TappyTapMessage tappyTapMessage) {
        this.tappyTapMessage = Ref.create(tappyTapMessage);
    }

    public List<Device> getRecipientDevices() {
        return recipientDevices;
    }

    public void setRecipientDevices(List<Device> recipientDevices) {
        this.recipientDevices = recipientDevices;
    }

    public User getSender() {
        return sender.getValue();
    }

    public void setSender(User sender) {
        this.sender = Ref.create(sender);
    }

    public static MulticastMessage findById(String multicastIdentifier) {
        return ofy().load().type(MulticastMessage.class).id(multicastIdentifier).now();
    }
}
