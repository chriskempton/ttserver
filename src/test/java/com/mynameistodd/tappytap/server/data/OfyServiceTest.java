package com.mynameistodd.tappytap.server.data;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * User: ckempton
 * Date: 10/31/13
 */
public class OfyServiceTest {

    private static final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeClass
    public static void setUp() {
        helper.setUp();
    }

    @AfterClass
    public static void tearDown() {
        helper.tearDown();
    }

    public static final String deviceId = "uwyfgwuicnhqw9eu34";
    public static final String email = "chriskempton97@gmail.com";

    @Test
    public void testInsert() {
        User theUser = new User();
        theUser.setEmail(email);
        theUser.save();

        Device theDevice = new Device();
        theDevice.setDeviceId(deviceId);
        theDevice.setUser(theUser);
        theDevice.save();
        System.out.println("Input Device Id: " + theDevice.getDeviceId());
        System.out.println("Input User email: " + theDevice.getUser().getEmail());
    }

    @Test
    public void testGetOneDevice() {
        System.out.println("Selecting a single Device");
        System.out.println("-------------------------");
        Device theDevice = ofy().load().type(Device.class).id(deviceId).now();
        if(theDevice != null) {
            System.out.println("Output Device Id: " + theDevice.getDeviceId());
            System.out.println("Output User email: " + theDevice.getUser().getEmail());
        } else {
            System.out.println("Output is null!");
        }
        assertNotNull(theDevice);
    }

    @Test
    public void getGetAllDevices() {
        System.out.println("Selecting all Devices");
        System.out.println("-------------------------");
        List<Device> devices = ofy().load().type(Device.class).list();
        for(Device theeDevice:devices) {
            if(theeDevice != null) {
                System.out.println("Output Device Id: " + theeDevice.getDeviceId());
                System.out.println("Output User email: " + theeDevice.getUser().getEmail());
            } else {
                System.out.println("Output is null!");
            }
        }
        assertTrue(devices.size() > 0);
    }
}
