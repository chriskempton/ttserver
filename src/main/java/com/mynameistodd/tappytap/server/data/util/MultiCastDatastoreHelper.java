package com.mynameistodd.tappytap.server.data.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple implementation of a data store using standard Java collections.
 * <p>
 * This class is neither persistent (it will lost the data when the app is
 * restarted) nor thread safe.
 */
public final class MulticastDatastoreHelper {

  public static final int MULTICAST_SIZE = 1000;

  private static final String MULTICAST_TYPE = "Multicast";
  private static final String MULTICAST_REG_IDS_PROPERTY = "regIds";

  private static final Logger logger =
      Logger.getLogger(MulticastDatastoreHelper.class.getName());
  private static final DatastoreService datastore =
      DatastoreServiceFactory.getDatastoreService();

  private MulticastDatastoreHelper() {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a persistent record with the devices to be notified using a
   * multicast message.
   *
   * @param devices registration ids of the devices.
   * @return encoded key for the persistent record.
   */
  public static String createMulticast(List<String> devices) {
    logger.info("Storing multicast for " + devices.size() + " devices");
    String encodedKey;
    Transaction txn = datastore.beginTransaction();
    try {
      Entity entity = new Entity(MULTICAST_TYPE);
      entity.setProperty(MULTICAST_REG_IDS_PROPERTY, devices);
      datastore.put(entity);
      Key key = entity.getKey();
      encodedKey = KeyFactory.keyToString(key);
      logger.fine("multicast key: " + encodedKey);
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
    return encodedKey;
  }

  /**
   * Gets a persistent record with the devices to be notified using a
   * multicast message.
   *
   * @param encodedKey encoded key for the persistent record.
   */
  public static List<String> getMulticast(String encodedKey) {
    Key key = KeyFactory.stringToKey(encodedKey);
    Entity entity;
    Transaction txn = datastore.beginTransaction();
    try {
      entity = datastore.get(key);
      @SuppressWarnings("unchecked")
      List<String> devices =
          (List<String>) entity.getProperty(MULTICAST_REG_IDS_PROPERTY);
      txn.commit();
      return devices;
    } catch (EntityNotFoundException e) {
      logger.severe("No entity for key " + key);
      return Collections.emptyList();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  /**
   * Updates a persistent record with the devices to be notified using a
   * multicast message.
   *
   * @param encodedKey encoded key for the persistent record.
   * @param devices new list of registration ids of the devices.
   */
  public static void updateMulticast(String encodedKey, List<String> devices) {
    Key key = KeyFactory.stringToKey(encodedKey);
    Entity entity;
    Transaction txn = datastore.beginTransaction();
    try {
      try {
        entity = datastore.get(key);
      } catch (EntityNotFoundException e) {
        logger.severe("No entity for key " + key);
        return;
      }
      entity.setProperty(MULTICAST_REG_IDS_PROPERTY, devices);
      datastore.put(entity);
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  /**
   * Deletes a persistent record with the devices to be notified using a
   * multicast message.
   *
   * @param encodedKey encoded key for the persistent record.
   */
  public static void deleteMulticast(String encodedKey) {
    Transaction txn = datastore.beginTransaction();
    try {
      Key key = KeyFactory.stringToKey(encodedKey);
      datastore.delete(key);
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

}
