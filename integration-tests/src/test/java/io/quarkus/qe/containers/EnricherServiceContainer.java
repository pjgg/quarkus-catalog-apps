package io.quarkus.qe.containers;

public class EnricherServiceContainer extends BaseServiceContainer {

    public EnricherServiceContainer() {
        super("enricher", 8083);
    }

}