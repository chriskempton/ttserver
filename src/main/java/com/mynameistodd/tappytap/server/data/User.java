package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by ckempton on 9/7/13.
 * A User is a person who has installed the TappyTap client.
 * A User can enroll in other Users' notifications and have enrollees of his/her own.
 */
@Entity
public class User extends TappyTapData {
    
	@Id
	String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
