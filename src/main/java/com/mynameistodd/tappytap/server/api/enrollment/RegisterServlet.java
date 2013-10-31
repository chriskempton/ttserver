package com.mynameistodd.tappytap.server.api.enrollment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mynameistodd.tappytap.server.api.BaseServlet;
import com.mynameistodd.tappytap.server.data.Device;
import com.mynameistodd.tappytap.server.data.Enrollment;
import com.mynameistodd.tappytap.server.data.User;

/**
 * Servlet that registers a device, whose registration id is identified by
 * {@link #PARAMETER_REG_ID}.
 *
 * <p>
 * The client app should call this servlet every time it receives a
 * {@code com.google.android.c2dm.intent.REGISTRATION C2DM} intent without an
 * error or {@code unregistered} extra.
 */
@SuppressWarnings("serial")
public class RegisterServlet extends BaseServlet {

  private static final String PARAMETER_REG_ID = "regId";
  private static final String PARAMETER_USER_ID = "userId";
  private static final String PARAMETER_SENDER_ID = "senderId";

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException {

	String deviceId = getParameter(req, PARAMETER_REG_ID);
    String userId = getParameter(req, PARAMETER_USER_ID);
    String senderId = getParameter(req, PARAMETER_SENDER_ID);

    User theUser = new User();
    theUser.setEmail(userId);
    theUser.save();
    
    Device theDevice = new Device();
    theDevice.setDeviceId(deviceId);
    theDevice.setUser(theUser);
    theDevice.save();
    
    Enrollment theEnrollment = new Enrollment();
    theEnrollment.setRecipient(Device.findById(deviceId));
    theEnrollment.setSender(User.findByEmail(senderId));
    theEnrollment.save();
    
    setSuccess(resp);
  }

}
