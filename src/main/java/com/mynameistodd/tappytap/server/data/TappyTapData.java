package com.mynameistodd.tappytap.server.data;

import com.googlecode.objectify.annotation.Entity;
import static com.mynameistodd.tappytap.server.data.util.OfyService.ofy;

/**
 * Created with IntelliJ IDEA.
 * User: ckempton
 * Date: 10/29/13
 */
@Entity
public class TappyTapData {

    public void save() {
        ofy().save().entity(this).now();
    }

    public void remove() {
        ofy().delete().entity(this).now();
    }
}
