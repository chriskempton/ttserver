package com.mynameistodd.tappytap.server.data.util;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.ObjectifyFactory;
import com.mynameistodd.tappytap.server.data.Device;
import com.mynameistodd.tappytap.server.data.User;
import com.mynameistodd.tappytap.server.data.Enrollment;
import com.mynameistodd.tappytap.server.data.Message;
import com.mynameistodd.tappytap.server.data.MessageSend;

/**
 * User: ckempton
 * Date: 10/30/13
 */
public class OfyService {
    static {
        factory().register(Device.class);
        factory().register(User.class);
        factory().register(Enrollment.class);
        factory().register(Message.class);
        factory().register(MessageSend.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}