package com.mynameistodd.tappytap.server.api.messaging;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.mynameistodd.tappytap.server.api.BaseServlet;
import com.mynameistodd.tappytap.server.data.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that adds a new message to all registered devices.
 * <p>
 * This servlet is used just by the browser (i.e., not device).
 */
@SuppressWarnings("serial")
public class SendAllMessagesServlet extends BaseServlet {

    static final String PARAMETER_SENDER = "senderId";
    static final String PARAMETER_MESSAGE = "message";

  /**
   * Processes the request to add a new message.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    User sender = User.findByEmail(req.getParameter(PARAMETER_SENDER));
    List<Enrollment> enrollees = sender.getEnrollees();
    String status;
    if (enrollees.isEmpty()) {
      status = "Message ignored as there is no device registered!";
    } else {
    	
      String messageText = req.getParameter(PARAMETER_MESSAGE);

      TappyTapMessage msg = new TappyTapMessage();
      msg.setMessageText(messageText);
      msg.setSender(sender);
      msg.save();
    	
      Queue queue = QueueFactory.getQueue("gcm");
      // NOTE: check below is for demonstration purposes; a real application
      // could always send a multicast, even for just one recipient
      if (enrollees.size() == 1) {
        // send a single message using plain post
        String device = enrollees.get(0).getRecipient().getDeviceId();
        queue.add(withUrl("/send").param(
            SendMessageServlet.PARAMETER_DEVICE, device).param(SendMessageServlet.PARAMETER_MESSAGE, messageText));
        status = "Single message queued for registration id " + device;
      } else {
        // send a multicast message using JSON
        // must split in chunks of 1000 devices (GCM limit)
        int total = enrollees.size();
        List<Device> partialDevices = new ArrayList<>(total);
        int counter = 0;
        int tasks = 0;
        for (Enrollment enrollment:enrollees) {
          counter++;
          partialDevices.add(enrollment.getRecipient());
          int partialSize = partialDevices.size();
          if (partialSize == 1000 || counter == total) {
              MulticastMessage multicastMessage = new MulticastMessage();
              multicastMessage.setSender(sender);
              multicastMessage.setRecipientDevices(partialDevices);
              multicastMessage.setTappyTapMessage(msg);
              multicastMessage.setMulticastIdentifier(UUID.randomUUID().toString());
              multicastMessage.save();
              logger.fine("Queuing " + partialSize + " devices on multicast " +
                multicastMessage.getMulticastIdentifier());
              TaskOptions taskOptions = TaskOptions.Builder
                .withUrl("/send")
                .param(SendMessageServlet.PARAMETER_MULTICAST, multicastMessage.getMulticastIdentifier())
                .method(Method.POST);
            queue.add(taskOptions);
            partialDevices.clear();
            tasks++;
          }
        }
        status = "Queued tasks to send " + tasks + " multicast messages to " +
            total + " devices";
      }
    }
      PrintWriter out = resp.getWriter();
      out.print(status);
  }

}
