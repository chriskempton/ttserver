package com.mynameistodd.tappytap.server.api;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mynameistodd.tappytap.server.data.Message;

import java.io.FileInputStream;
import java.util.logging.Logger;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Context initializer that loads the API key from the App Engine datastore.
 */
public class ApiKeyInitializer implements ServletContextListener {

  private static final String ENTITY_KIND = "Settings";
  private static final String ENTITY_KEY = "MyKey";
  private static final String ACCESS_KEY_FIELD = "ApiKey";

  private final Logger logger = Logger.getLogger(getClass().getName());

  public void contextInitialized(ServletContextEvent event) {
	  //register classes for objectify
	  ObjectifyService.register(Message.class);
	  
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key key = KeyFactory.createKey(ENTITY_KIND, ENTITY_KEY);

    Entity entity;
    try {
      entity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      entity = new Entity(key);
      // NOTE: it's not possible to change entities in the local server, so
      // it will be necessary to hardcode the API key below if you are running
      // it locally.
      entity.setProperty(ACCESS_KEY_FIELD,
    		  getAccessKey());
      datastore.put(entity);
      logger.severe("Created fake key. Please go to App Engine admin "
          + "console, change its value to your API Key (the entity "
          + "type is '" + ENTITY_KIND + "' and its field to be changed is '"
          + ACCESS_KEY_FIELD + "'), then restart the server!");
    }
    String accessKey = (String) entity.getProperty(ACCESS_KEY_FIELD);
    event.getServletContext().setAttribute(getAccessKey(), accessKey);
  }
  
  public static String getAccessKey(){
      Properties props = new Properties();
      try {
          props.load(new FileInputStream("config.properties"));
      } catch (Exception e) {
          return "";
      }
      if(props.getProperty("APIKey") != null) {
          return props.getProperty("APIKey");
      }
      return "";
  }


  public void contextDestroyed(ServletContextEvent event) {
  }

}
