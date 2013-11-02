package com.mynameistodd.tappytap.server.data;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static final String chrisDeviceId = "Chrisuwyfgwuicnhqw9eu34";
    public static final String toddDeviceId = "Todduwyfgwuicsadasdasdnhqw9eu34";
    public static final String ajDeviceId = "AJuwyfgwuicsadasdasdnhqw9eu34";
    public static final String chrisEmail = "chriskempton97@gmail.com";
    public static final String toddEmail = "todd.deland@gmail.com";
    public static final String ajEmail = "kemptonaj@gmail.com";
    public static final String messageText = "Tappy Tap Test message";
    public static final String multicastKey = UUID.randomUUID().toString();
    @Test
    public void testInsert() {
        User ChrisKemptonUser = new User();
        ChrisKemptonUser.setEmail(chrisEmail);
        ChrisKemptonUser.save();

        User ToddDelandUser = new User();
        ToddDelandUser.setEmail(toddEmail);
        ToddDelandUser.save();

        User AJKemptonUser = new User();
        AJKemptonUser.setEmail(ajEmail);
        AJKemptonUser.save();

        Device ChrisKemptonDevice = new Device();
        ChrisKemptonDevice.setDeviceId(chrisDeviceId);
        ChrisKemptonDevice.setUser(ChrisKemptonUser);
        ChrisKemptonDevice.save();

        Device ToddDelandDevice = new Device();
        ToddDelandDevice.setDeviceId(toddDeviceId);
        ToddDelandDevice.setUser(ToddDelandUser);
        ToddDelandDevice.save();

        Device AJKemptonDevice = new Device();
        AJKemptonDevice.setDeviceId(ajDeviceId);
        AJKemptonDevice.setUser(AJKemptonUser);
        AJKemptonDevice.save();

        Enrollment theEnrollment = new Enrollment();
        theEnrollment.setSender(ToddDelandUser);
        theEnrollment.setRecipient(ChrisKemptonDevice);
        theEnrollment.save();

        Enrollment theEnrollment2 = new Enrollment();
        theEnrollment2.setSender(AJKemptonUser);
        theEnrollment2.setRecipient(ToddDelandDevice);
        theEnrollment2.save();

        TappyTapMessage theMessage = new TappyTapMessage();
        theMessage.setSender(ToddDelandUser);
        theMessage.setMessageText(messageText);
        theMessage.save();

        MulticastMessage multicastMessage = new MulticastMessage();
        multicastMessage.setMulticastIdentifier(multicastKey);
        multicastMessage.setSender(ToddDelandUser);
        multicastMessage.setTappyTapMessage(theMessage);
        List<Device> recipients = new ArrayList<>();
        recipients.add(ChrisKemptonDevice);
        recipients.add(AJKemptonDevice);
        multicastMessage.setRecipientDevices(recipients);
        multicastMessage.save();

        System.out.println("Input Device Id: " + ChrisKemptonDevice.getDeviceId());
        System.out.println("Input User Email: " + ChrisKemptonDevice.getUser().getEmail());
    }

    @Test
    public void testGetOneDevice() {
        System.out.println();
        System.out.println("Selecting a single Device by Id");
        System.out.println("-------------------------");
        Device theDevice = ofy().load().type(Device.class).id(chrisDeviceId).now();
        if(theDevice != null) {
            System.out.println("Output Device Id: " + theDevice.getDeviceId());
            System.out.println("Output User chrisEmail: " + theDevice.getUser().getEmail());
        } else {
            System.out.println("Output is null!");
        }
        assertNotNull(theDevice);
    }

    @Test
    public void getGetAllDevices() {
        System.out.println();
        System.out.println("Selecting all Devices");
        System.out.println("-------------------------");
        List<Device> devices = ofy().load().type(Device.class).list();
        for(Device theDevice:devices) {
            if(theDevice != null) {
                System.out.println("Output Device Id: " + theDevice.getDeviceId());
                System.out.println("Output User Email: " + theDevice.getUser().getEmail());
            } else {
                System.out.println("Output is null!");
            }
        }
        assertTrue(devices.size() == 3);
    }

    @Test
    public void getEnrollmentsByDeviceId() {
        System.out.println();
        System.out.println("Selecting all Enrollments for the device " + chrisDeviceId);
        System.out.println("-------------------------");
        List<Enrollment> enrollments = ofy().load().type(Enrollment.class).filter("recipient", Device.findById(chrisDeviceId)).list();
        for(Enrollment enrollment:enrollments) {
            if(enrollment != null){
                System.out.println("Recipient Email: " + enrollment.getRecipient().getUser().getEmail());
                System.out.println("Recipient Device Id: " + enrollment.getRecipient().getDeviceId());
                System.out.println("Sender Email: " + enrollment.getSender().getEmail());
            } else {
                System.out.println("Output is null!");
            }
        }
        assertTrue(enrollments.size() == 1);
        System.out.println();
        System.out.println("Selecting all Enrollments for the device " + toddDeviceId);
        System.out.println("-------------------------");
        List<Enrollment> enrollments2 = ofy().load().type(Enrollment.class).filter("recipient", Device.findById(toddDeviceId)).list();
        for(Enrollment enrollment:enrollments2) {
            if(enrollment != null){
                System.out.println("Recipient Email: " + enrollment.getRecipient().getUser().getEmail());
                System.out.println("Recipient Device Id: " + enrollment.getRecipient().getDeviceId());
                System.out.println("Sender Email: " + enrollment.getSender().getEmail());
            } else {
                System.out.println("Output is null!");
            }
        }
        assertTrue(enrollments2.size() == 1);
    }

    @Test
    public void getMulticastMessage() {
        System.out.println();
        System.out.println("Selecting MulticastMessage for the key " + multicastKey);
        System.out.println("-------------------------");
        MulticastMessage multicastMessage = MulticastMessage.findById(multicastKey);
        for(Device device:multicastMessage.getRecipientDevices()) {
            System.out.println("Recipient Device Id: " + device.getDeviceId());
            assertTrue(multicastMessage.getSender().getEmail().equals(toddEmail));
        }
        assertTrue(multicastMessage.getRecipientDevices().size() == 2);
        System.out.println("Sender: " + multicastMessage.getSender().getEmail());
        assertTrue(multicastMessage.getSender().getEmail().equals(toddEmail));
        System.out.println("Message Text: " + multicastMessage.getTappyTapMessage().getMessageText());
        assertTrue(multicastMessage.getTappyTapMessage().getMessageText().equals(messageText));
        System.out.println("-------------------------");
    }

    @Test
    public void getUserEnrollees() {
        System.out.println();
        System.out.println("Selecting Enrollees for the user " + toddEmail);
        System.out.println("-------------------------");
        for(Enrollment enrollment:User.findByEmail(toddEmail).getEnrollees()) {
            System.out.println("Recipient Email: " + enrollment.getRecipient().getUser().getEmail());
            System.out.println("Recipient Device Id: " + enrollment.getRecipient().getDeviceId());
            assertTrue(enrollment.getRecipient().getDeviceId().equals(chrisDeviceId));
        }
    }

    @Test
    public void getDevices() {
        System.out.println();
        System.out.println("Selecting Devices for the user " + toddEmail);
        System.out.println("-------------------------");
        for(Device device:User.findByEmail(toddEmail).getDevices()) {
            System.out.println("Device Id: " + device.getDeviceId());
            assertTrue(device.getDeviceId().equals(toddDeviceId));
        }
    }
}
