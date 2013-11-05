package com.mynameistodd.tappytap.server.api.messaging;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MockResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.mynameistodd.tappytap.server.data.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.powermock.api.mockito.PowerMockito.*;

import org.junit.*;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.logging.Logger;

/**
 * User: ckempton
 * Date: 11/3/13
 */
@PrepareForTest({
        GcmMessageService.class
})
public class GcmMessageServiceTest {
    public static final String chrisDeviceId = "Chrisuwyfgwuicnhqw9eu34";
    public static final String chrisEmail = "chriskempton97@gmail.com";
    public static final String toddEmail = "todd.deland@gmail.com";
    public static final String messageText = "Tappy Tap Test message";

    private Sender sender;
    private Logger logger;
    private GcmMessageService gcmMessageService = new GcmMessageService();
    private Result result;
    private static final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeClass
    public static void helperSetUp() {
        helper.setUp();
    }

    @AfterClass
    public static void helperTearDown() {
        helper.tearDown();
    }

    @Before
    public void setUp() throws Exception {
        sender = mock(Sender.class);
        logger = mock(Logger.class);
        Whitebox.setInternalState(gcmMessageService, "sender", sender);
        Whitebox.setInternalState(gcmMessageService, "logger", logger);
        result = MockResult.getMockResult();
        doReturn(result).when(sender).sendNoRetry((Message) anyObject(), anyString());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSend() throws Exception {
        User ToddDelandUser = new User();
        ToddDelandUser.setEmail(toddEmail);
        ToddDelandUser.save();

        User ChrisKemptonUser = new User();
        ChrisKemptonUser.setEmail(chrisEmail);
        ChrisKemptonUser.save();

        Device ChrisKemptonDevice = new Device();
        ChrisKemptonDevice.setDeviceId(chrisDeviceId);
        ChrisKemptonDevice.setUser(ChrisKemptonUser);
        ChrisKemptonDevice.save();

        TappyTapMessage theMessage = new TappyTapMessage();
        theMessage.setMessageText(messageText);
        theMessage.setSender(ToddDelandUser);
        theMessage.save();

        MessageSend messageSend = new MessageSend();
        messageSend.setSender(ToddDelandUser);
        messageSend.setRecipient(ChrisKemptonDevice);
        messageSend.setTappyTapMessage(theMessage);
        messageSend.save();

        gcmMessageService.send(messageSend);
        Mockito.verify(logger).info(contains("Success"));
    }

    @Test
    public void testSendMulticastReturnRetries() throws Exception {

    }
}
