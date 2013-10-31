package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.mynameistodd.tappytap.server.data.util.DatastoreHelper;
import static com.mynameistodd.tappytap.server.data.util.OfyService.ofy;
import com.googlecode.objectify.Ref;

import java.util.List;

/**
 * Created by ckempton on 10/23/13.
 * A Device is a Message destination associated with a User.
 */
@Entity
public class Device extends TappyTapData {
    
	@Id
    String deviceId;
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
        ofy().delete().entity(this);
	}

    /**
     * Updates the registration id of the device.
     */
    public void updateRegistration(String newId) {
        this.remove();
        this.setDeviceId(newId);
        this.save();
    }

    /**
     * Gets a device by Id.
     */
    public static Device findById(String deviceId) {
        return ofy().load().type(Device.class).id(deviceId).now();
    }

    /**
     * Gets all registered devices.
     */
    public static List<Device> getAll() {
        return ofy().load().type(Device.class).list();
    }

    /**
     * Gets the number of total devices.
     */
    public static int getTotalCount() {
        return getAll().size();
    }
}
