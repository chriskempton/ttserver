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
	String recipientDeviceId;
	String senderId;
	
	public String getRecipientID() {
	    return recipientDeviceId;
	}
	
	public void setRecipientID(String recipientID) {
	    this.recipientDeviceId = recipientID;
	}
	
	public String getSenderID() {
	    return senderId;
	}
	
	public void setSenderID(String senderID) {
	    this.senderId = senderID;
	}
	
	public void save() {
		DatastoreHelper.enroll(recipientDeviceId, senderId);
	}
	
	public void remove() {
		DatastoreHelper.unenroll(recipientDeviceId, senderId);
	}
}
