package com.mynameistodd.tappytap.server.api.messaging;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.mynameistodd.tappytap.server.api.BaseServlet;
import com.mynameistodd.tappytap.server.data.User;
import com.mynameistodd.tappytap.server.data.util.MulticastDatastoreHelper;
import com.mynameistodd.tappytap.server.data.Message;
import com.mynameistodd.tappytap.server.data.Device;
import com.mynameistodd.tappytap.webclient.HomeServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    List<Device> devices = Device.getAll();
    String status;
    if (devices.isEmpty()) {
      status = "Message ignored as there is no device registered!";
    } else {
    	
      String messageText = req.getParameter(PARAMETER_MESSAGE);
      String sender = req.getParameter(PARAMETER_SENDER);

      Message msg = new Message();
      msg.setMessage(messageText);
      msg.setSender(User.findByEmail(sender));
      msg.save();
    	
      Queue queue = QueueFactory.getQueue("gcm");
      // NOTE: check below is for demonstration purposes; a real application
      // could always send a multicast, even for just one recipient
      if (devices.size() == 1) {
        // send a single message using plain post
        String device = devices.get(0).getDeviceId();
        queue.add(withUrl("/send").param(
            SendMessageServlet.PARAMETER_DEVICE, device).param(SendMessageServlet.PARAMETER_MESSAGE, messageText));
        status = "Single message queued for registration id " + device;
      } else {
        // send a multicast message using JSON
        // must split in chunks of 1000 devices (GCM limit)
        int total = devices.size();
        List<String> partialDevices = new ArrayList<>(total);
        int counter = 0;
        int tasks = 0;
        for (Device device:devices) {
          counter++;
          partialDevices.add(device.getDeviceId());
          int partialSize = partialDevices.size();
          if (partialSize == MulticastDatastoreHelper.MULTICAST_SIZE || counter == total) {
            String multicastKey = MulticastDatastoreHelper.createMulticast(partialDevices);
            logger.fine("Queuing " + partialSize + " devices on multicast " +
                multicastKey);
            TaskOptions taskOptions = TaskOptions.Builder
                .withUrl("/send")
                .param(SendMessageServlet.PARAMETER_MULTICAST, multicastKey)
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
    req.setAttribute(HomeServlet.ATTRIBUTE_STATUS, status);
    getServletContext().getRequestDispatcher("/home").forward(req, resp);
  }

}
