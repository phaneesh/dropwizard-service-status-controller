package io.dyuti.dropwizard.tasks;

import io.dropwizard.servlets.tasks.Task;
import io.dyuti.dropwizard.core.ServiceState;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GetServiceStateTask extends Task {

  private Supplier<ServiceState> stateSupplier;

  public GetServiceStateTask(Supplier<ServiceState> stateSupplier) {
    super("service-state");
    this.stateSupplier = stateSupplier;
  }

  @Override
  public void execute(Map<String, List<String>> map, PrintWriter printWriter) {
    printWriter.println(stateSupplier.get().name());
  }

}
