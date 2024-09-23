/*
 * Copyright 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dyuti.dropwizard;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dyuti.dropwizard.core.ServiceState;
import io.dyuti.dropwizard.status.StatusControllerFilter;
import io.dyuti.dropwizard.tasks.GetServiceStateTask;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

/** Bundle that allows controlling service availability status */
@Slf4j
public abstract class ServiceStatusControllerBundle<T extends Configuration>
    implements ConfiguredBundle<T> {

  @Override
  public void initialize(Bootstrap<?> bootstrap) {}

  @Override
  public void run(T configuration, Environment environment) {
    environment
        .jersey()
        .register(
            new StatusControllerFilter(
                stateSupplier(), initDelaySeconds(configuration), delaySeconds(configuration)));
    environment.admin().addTask(new GetServiceStateTask(stateSupplier()));
  }

  public abstract Supplier<ServiceState> stateSupplier();

  public abstract int initDelaySeconds(Configuration configuration);

  public abstract int delaySeconds(Configuration configuration);
}
