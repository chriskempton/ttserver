package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

/**
 * Created by ckempton on 9/7/13.
 * A MessageSend is an individual instance of a TappyTapMessage being sent to a recipient
 */
@Entity
public class MessageSend extends TappyTapData {

    @Id
    Long id;
    @Index
    @Load
    Ref<TappyTapMessage> tappyTapMessage;
    @Index
    @Load
    Ref<Device> recipient;
    @Index
    @Load
    Ref<User> sender;

    public TappyTapMessage getTappyTapMessage() {
        return tappyTapMessage.getValue();
    }

    public void setTappyTapMessage(TappyTapMessage tappyTapMessage) {
        this.tappyTapMessage = Ref.create(tappyTapMessage);
    }

    public Device getRecipient() {
        return recipient.getValue();
    }

    public void setRecipient(Device recipient) {
        this.recipient = Ref.create(recipient);
    }

    public User getSender() {
        return sender.getValue();
    }

    public void setSender(User sender) {
        this.sender = Ref.create(sender);
    }
}
