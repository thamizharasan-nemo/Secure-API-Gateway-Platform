package com.backend.API_Gateway.discovery;

import java.util.List;

public interface ServiceRegistry {

    List<String> getInstances(String serviceName);
}
