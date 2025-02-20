# Dropwizard Status Controller Bundle

This bundle adds status control to your drop wizard application which allows one 
to control the availability of the service. This will ensure that the service remains in
passive mode and does not serve any request. This is useful when the service is under maintenance or
when setup in Active-Passive mode.
If the status is set to UNAVAILABLE all the calls to the application will send a 
503 Service Unavailable. Once the service state is set to AVAILABLE the service will start serving requests.


## Usage
This bundle makes it simple to control the availability of the service. 
Please note that the /healthcheck will continue to function as is and only requests coming directly 
to the service will return 503 if the service is made unavailable. 
 
### Build instructions
  - Clone the source:

        git clone github.com/phaneesh/dropwizard-service-status-controller

  - Build

        mvn install

### Maven Dependency
* Use the following maven dependency:
```
<dependency>
    <groupId>io.dyuti</groupId>
    <artifactId>dropwizard-service-status-controller</artifactId>
    <version>2.1.12-3</version>
</dependency>
```

### Using Health Check Extras bundle

#### Bootstrap

```java
    import io.dyuti.dropwizard.core.ServiceState;
import io.dyuti.dropwizard.listener.ServiceStateChangeListener;

ServiceStatusControllerBundle<Configuration> stateControllerBundle;

@Override
public void initialize(final Bootstrap bootstrap) {
  stateControllerBundle = new ServiceStatusControllerBundle<Configuration>() {

    public int initDelaySeconds(MyAppConfiguration configuration) {
      return configuration.getServiceStatusControllerConfiguration().getInitDelaySeconds();
    }

    public int checkIntervalSeconds(MyAppConfiguration configuration) {
      return configuration.getServiceStatusControllerConfiguration().getCheckIntervalSeconds();
    }

    public Supplier<ServiceState> stateSupplier() {
      () -> {
        //Your logic to get the state of the service.
        //Example: A value that can be dynamically changed by supplying through a configuration 
        //source that can be dynamically refreshed
        return ServiceStateMonitor.currentState();
      }
    }
  };
  bootstrap.addBundle(stateControllerBundle);
}

@Override
public void run(final MyAppConfiguration configuration, final Environment environment) {
  //Register a listener to get notified when the service state changes
  stateControllerBundle.registerListener((ServiceStateChangeListener) (oldState, newState) -> {
    //Your logic to handle the state change
  });

  //Call getCurrentServiceState to get the current state of the service
  if(stateControllerBundle.getCurrentServiceState() == ServiceState.AVAILABLE){
    //Your logic to handle the service being available
  }
}
```