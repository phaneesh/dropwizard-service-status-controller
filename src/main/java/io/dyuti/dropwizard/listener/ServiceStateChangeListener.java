package io.dyuti.dropwizard.listener;

import io.dyuti.dropwizard.core.ServiceState;

/**
 * Service state change listener to subscribe to service state changes
 */
public interface ServiceStateChangeListener {

  void stateChanged(ServiceState oldState, ServiceState newState);
}
