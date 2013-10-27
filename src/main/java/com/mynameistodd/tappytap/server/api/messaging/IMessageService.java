package com.mynameistodd.tappytap.server.api.messaging;

import com.mynameistodd.tappytap.server.data.MessageSend;
import javax.servlet.ServletConfig;
import java.util.List;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ckempton
 * Date: 10/26/13
 * Time: 5:50 PM
 */
public interface IMessageService {
    public void send(MessageSend messageSend) throws IOException;
    public List<MessageSend> sendMulticastReturnRetries(List<MessageSend> messageSends) throws IOException;
    public void init(ServletConfig config);
}
