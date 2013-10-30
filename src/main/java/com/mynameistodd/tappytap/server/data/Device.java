package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.mynameistodd.tappytap.server.data.util.DatastoreHelper;

/**
 * Created by ckempton on 10/23/13.
 * A Device is a Message destination associated with a User.
 */
@Entity
public class Device extends TappyTapData {
    
	@Id
	Long id;
	String userId;
	String deviceId;

    public String getUserID() {
        return userId;
    }

    public void setUserID(String userID) {
        this.userId = userID;
    }

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
    public void remove() {
		DatastoreHelper.unenrollDevice(deviceId);
        objectifyService.delete().entity(this);
	}
}
