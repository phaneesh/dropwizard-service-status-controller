package io.dyuti.dropwizard.status;

import io.dyuti.dropwizard.ServiceStatusControllerBundle;
import io.dyuti.dropwizard.core.ServiceState;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Singleton
@Provider
@Priority(1)
public class StatusControllerFilter implements ContainerRequestFilter {

  private final AtomicReference<ServiceState> serviceState;

  private final Supplier<ServiceState> stateSupplier;

  public StatusControllerFilter(
      Supplier<ServiceState> stateSupplier,
      int initDelaySeconds,
      int delaySeconds) {
    this.stateSupplier = stateSupplier;
    this.serviceState = new AtomicReference<>(this.stateSupplier.get());
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleWithFixedDelay(
        () -> {
          var oldState = serviceState.getAndSet(this.stateSupplier.get());
          if (oldState != serviceState.get() && !ServiceStatusControllerBundle.getStateChangeListeners()
              .isEmpty()) {
            ServiceStatusControllerBundle.getStateChangeListeners()
                .forEach(listener -> listener.stateChanged(oldState, serviceState.get()));
          }
        },
        initDelaySeconds,
        delaySeconds,
        TimeUnit.SECONDS);
    Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (this.serviceState.get() == ServiceState.UNAVAILABLE) {
      requestContext.abortWith(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }
  }
}
