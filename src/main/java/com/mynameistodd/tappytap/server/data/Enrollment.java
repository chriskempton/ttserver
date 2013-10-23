package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by ckempton on 9/7/13.
 * An Enrollment is a mapping of one sender User to one recipient User.
 */
@Entity
public class Enrollment {

	@Id
	Long id;
	String recipientID;
	String senderID;
	
	public String getRecipientID() {
	    return recipientID;
	}
	
	public void setRecipientID(String recipientID) {
	    this.recipientID = recipientID;
	}
	
	public String getSenderID() {
	    return senderID;
	}
	
	public void setSenderID(String senderID) {
	    this.senderID = senderID;
	}
}
