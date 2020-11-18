package io.quarkus.qe.containers;

public class StorageServiceContainer extends BaseServiceContainer {

    public StorageServiceContainer() {
        super("storage-service", 8082);
    }

}