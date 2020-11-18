package io.quarkus.qe.containers;

public class RestApiServiceContainer extends BaseServiceContainer {

    public RestApiServiceContainer() {
        super("rest-api", 8081);
    }

}