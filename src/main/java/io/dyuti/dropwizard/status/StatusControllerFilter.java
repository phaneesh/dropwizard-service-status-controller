package io.dyuti.dropwizard.status;

import io.dyuti.dropwizard.core.ServiceState;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class StatusControllerFilter implements ContainerRequestFilter {

  private AtomicReference<ServiceState> serviceState;

  private final ScheduledExecutorService executor;

  private Supplier<ServiceState> stateSupplier;

  public StatusControllerFilter(
      Supplier<ServiceState> stateSupplier, int initDelaySeconds, int delaySeconds) {
    this.executor = Executors.newScheduledThreadPool(1);
    this.stateSupplier = stateSupplier;
    this.serviceState = new AtomicReference<>(this.stateSupplier.get());
    this.executor.scheduleWithFixedDelay(
        () -> serviceState.getAndSet(this.stateSupplier.get()),
        initDelaySeconds,
        delaySeconds,
        TimeUnit.SECONDS);
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (this.serviceState.get() == ServiceState.UNAVAILABLE) {
      requestContext.abortWith(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }
  }
}
