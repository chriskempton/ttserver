package com.mynameistodd.tappytap.server.data.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.mynameistodd.tappytap.server.data.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple implementation of a data store using standard Java collections.
 * <p>
 * This class is neither persistent (it will lost the data when the app is
 * restarted) nor thread safe.
 */
public final class DatastoreHelper {

  private static Objectify objectifyService = ObjectifyService.ofy();
  public static final int MULTICAST_SIZE = 1000;
  private static final String ENROLLMENT_TYPE = "Enrollment";
  private static final String DEVICE_REG_ID_PROPERTY = "registrationId";

  private static final String MULTICAST_TYPE = "Multicast";
  private static final String MULTICAST_REG_IDS_PROPERTY = "regIds";

  private static final FetchOptions DEFAULT_FETCH_OPTIONS = FetchOptions.Builder
      .withPrefetchSize(MULTICAST_SIZE).chunkSize(MULTICAST_SIZE);

  private static final Logger logger =
      Logger.getLogger(DatastoreHelper.class.getName());
  private static final DatastoreService datastore =
      DatastoreServiceFactory.getDatastoreService();

  private DatastoreHelper() {
    throw new UnsupportedOperationException();
  }

  /**
   * Unenrolls a device from all senders.
   *
   * @param regId device's registration id.
   */
  public static void unenrollDevice(String regId) {
    logger.info("Unenrolling " + regId + " from all senders");
    Transaction txn = datastore.beginTransaction();
    try {
      List<Entity> entities = findEnrollmentsByRegId(regId);
      if (entities == null || entities.size() == 0) {
        logger.warning("Device " + regId + " already unenrolled");
      } else {
        for (Entity entity:entities) {
        	Key key = entity.getKey();
        	datastore.delete(key);
        }
      }
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  /**
   * Updates the registration id of a device.
   */
  public static void updateRegistration(String oldId, String newId) {
    logger.info("Updating " + oldId + " to " + newId);
    Transaction txn = datastore.beginTransaction();
    try {
      Entity entity = findDeviceByRegId(oldId);
      if (entity == null) {
        logger.warning("No device for registration id " + oldId);
        return;
      }
      entity.setProperty(DEVICE_REG_ID_PROPERTY, newId);
      datastore.put(entity);
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  /**
   * Gets all registered devices.
   */
  public static List<Device> getDevices() {
      return objectifyService.load().type(Device.class).list();
  }

  /**
   * Gets the number of total devices.
   */
  public static int getTotalDevices() {
    return getDevices().size();
  }

  private static Entity findDeviceByRegId(String regId) {
      return null;
      // Device c = objectifyService.load().type(Device.class).filter("deviceId", regId);
  }

  private static List<Entity> findEnrollmentsByRegId(String regId) {
    Query query = new Query(ENROLLMENT_TYPE)
    	.addFilter(DEVICE_REG_ID_PROPERTY, FilterOperator.EQUAL, regId);
    PreparedQuery preparedQuery = datastore.prepare(query);
    List<Entity> entities = preparedQuery.asList(DEFAULT_FETCH_OPTIONS);
    int size = entities.size();
    if (size > 0) {
      logger.severe(
          "Found " + size + " entities for regId " + regId + ": " + entities);
    }
    return entities;
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
