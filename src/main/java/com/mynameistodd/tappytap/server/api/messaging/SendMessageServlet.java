package com.mynameistodd.tappytap.server.api.messaging;

import com.mynameistodd.tappytap.server.api.BaseServlet;
import com.mynameistodd.tappytap.server.data.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that sends a message to a device.
 * <p>
 * This servlet is invoked by AppEngine's Push Queue mechanism.
 */
@SuppressWarnings("serial")
public class SendMessageServlet extends BaseServlet {

  private static final String HEADER_QUEUE_COUNT = "X-AppEngine-TaskRetryCount";
  private static final String HEADER_QUEUE_NAME = "X-AppEngine-QueueName";
  private static final int MAX_RETRY = 3;

  static final String PARAMETER_DEVICE = "device";
  static final String PARAMETER_MULTICAST = "multicastKey";
  static final String PARAMETER_SENDER = "senderId";
  static final String PARAMETER_MESSAGE = "message";

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  /**
   * Indicates to App Engine that this task should be retried.
   */
  private void retryTask(HttpServletResponse resp) {
    resp.setStatus(500);
  }

  /**
   * Indicates to App Engine that this task is done.
   */
  private void taskDone(HttpServletResponse resp) {
    resp.setStatus(200);
  }

  /**
   * Processes the request to add a new message.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (req.getHeader(HEADER_QUEUE_NAME) == null) {
      throw new IOException("Missing header " + HEADER_QUEUE_NAME);
    }
    String retryCountHeader = req.getHeader(HEADER_QUEUE_COUNT);
    logger.fine("retry count: " + retryCountHeader);
    if (retryCountHeader != null) {
      int retryCount = Integer.parseInt(retryCountHeader);
      if (retryCount > MAX_RETRY) {
          logger.severe("Too many retries, dropping task");
          taskDone(resp);
          return;
      }
    }
    String message = req.getParameter(PARAMETER_MESSAGE);
    String sender = req.getParameter(PARAMETER_SENDER);
    String regId = req.getParameter(PARAMETER_DEVICE);
    TappyTapMessage theTappyTapMessage = new TappyTapMessage();
    theTappyTapMessage.setMessageText(message);
    theTappyTapMessage.setSender(User.findByEmail(sender));
    theTappyTapMessage.save();

    MessageSend messageSend = new MessageSend();
    messageSend.setTappyTapMessage(theTappyTapMessage);
    messageSend.setRecipient(Device.findById(regId));
    messageSend.setSender(User.findByEmail(sender));
    if (regId != null) {
      IMessageService messageService = MessageServiceFactory.getInstance();
      try {
          messageService.send(messageSend);
      } catch (IOException ioe) {
          taskDone(resp);
      } catch (NullPointerException npe) {
          retryTask(resp);
      }
      return;
    }
    String multicastKey = req.getParameter(PARAMETER_MULTICAST);
    if (multicastKey != null) {
      sendMulticastMessage(multicastKey, sender, theTappyTapMessage, resp);
      return;
    }
    logger.severe("Invalid request!");
    taskDone(resp);
  }

  private void sendMulticastMessage(String multicastKey, String senderId, TappyTapMessage tappyTapMessage, HttpServletResponse resp) {
    // Recover registration ids from datastore
    List<Device> recipientDevices = MulticastMessage.findById(multicastKey).getRecipientDevices();
    List<MessageSend> messageSends = new ArrayList<>();
    for(Device device:recipientDevices) {
        MessageSend messageSend = new MessageSend();
        messageSend.setTappyTapMessage(tappyTapMessage);
        messageSend.setSender(User.findByEmail(senderId));
        messageSend.setRecipient(device);
        messageSends.add(messageSend);
    }
    List<MessageSend> messageSendsOutput = null;
    try {
      IMessageService messageService = MessageServiceFactory.getInstance();
      messageSendsOutput = messageService.sendMulticastReturnRetries(messageSends);
    } catch (IOException e) {
      multicastDone(resp, multicastKey);
    } finally {
        if (messageSendsOutput == null || messageSendsOutput.size() == 0) {
          multicastDone(resp, multicastKey);
        } else {
          retryTask(resp);
        }
    }
  }

  private void multicastDone(HttpServletResponse resp, String encodedKey) {
    MulticastMessage.findById(encodedKey).remove();
    taskDone(resp);
  }

}
