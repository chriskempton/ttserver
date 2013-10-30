package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.Objectify;
import com.mynameistodd.tappytap.server.data.util.OfyService;

/**
 * Created with IntelliJ IDEA.
 * User: ckempton
 * Date: 10/29/13
 */
public class TappyTapData {
    @Ignore
    Objectify objectifyService = OfyService.ofy();

    public void save() {
        objectifyService.save().entity(this).now();
    }

    public void remove() {
        objectifyService.delete().entity(this);
    }
}
