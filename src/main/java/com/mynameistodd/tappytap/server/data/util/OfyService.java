package com.mynameistodd.tappytap.server.data.util;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.ObjectifyFactory;
import com.mynameistodd.tappytap.server.data.Device;
import com.mynameistodd.tappytap.server.data.User;
import com.mynameistodd.tappytap.server.data.Enrollment;
import com.mynameistodd.tappytap.server.data.TappyTapMessage;
import com.mynameistodd.tappytap.server.data.MessageSend;
import com.mynameistodd.tappytap.server.data.MulticastMessage;

/**
 * User: ckempton
 * Date: 10/30/13
 */
public class OfyService {
    static {
        factory().register(Device.class);
        factory().register(User.class);
        factory().register(Enrollment.class);
        factory().register(TappyTapMessage.class);
        factory().register(MessageSend.class);
        factory().register(MulticastMessage.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}