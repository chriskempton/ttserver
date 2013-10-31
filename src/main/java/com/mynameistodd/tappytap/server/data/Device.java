package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.mynameistodd.tappytap.server.data.util.DatastoreHelper;
import com.googlecode.objectify.Ref;

/**
 * Created by ckempton on 10/23/13.
 * A Device is a Message destination associated with a User.
 */
@Entity
public class Device extends TappyTapData {
    
	@Id
    String deviceId;
    @Parent
    @Index
    @Load
    Ref<User> user;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

    public User getUser() {
        return user.getValue();
    }

    public void setUser(User user) {
        this.user = Ref.create(user);
    }

    @Override
    public void remove() {
		DatastoreHelper.unenrollDevice(deviceId);
        objectifyService.delete().entity(this);
	}
}
