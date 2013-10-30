package com.mynameistodd.tappytap.server.api.enrollment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mynameistodd.tappytap.server.api.BaseServlet;
import com.mynameistodd.tappytap.server.data.util.DatastoreHelper;
import com.mynameistodd.tappytap.server.data.Enrollment;

/**
 * Servlet that unregisters a device, whose registration id is identified by
 * {@link #PARAMETER_REG_ID}.
 * <p>
 * The client app should call this servlet every time it receives a
 * {@code com.google.android.c2dm.intent.REGISTRATION} with an
 * {@code unregistered} extra.
 */
@SuppressWarnings("serial")
public class UnregisterServlet extends BaseServlet {

  private static final String PARAMETER_REG_ID = "regId";
  private static final String PARAMETER_SENDER_ID = "senderId";

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException {
    String regId = getParameter(req, PARAMETER_REG_ID);
    String senderId = getParameter(req, PARAMETER_SENDER_ID);

    Enrollment theEnrollment = new Enrollment();
    theEnrollment.setRecipientID(regId);
    theEnrollment.setSenderID(senderId);
    theEnrollment.remove();
    
    setSuccess(resp);
  }

}
