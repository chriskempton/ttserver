package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

/**
 * Created by ckempton on 9/7/13.
 * A Message is a text that a User has sent.  It can be reused.
 */
@Entity
public class Message extends TappyTapData {
	
	@Id
	Long id;
	String message;
    @Index
    @Load
    Ref<User> sender;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender.getValue();
    }

    public void setSender(User sender) {
        this.sender = Ref.create(sender);
    }
}
