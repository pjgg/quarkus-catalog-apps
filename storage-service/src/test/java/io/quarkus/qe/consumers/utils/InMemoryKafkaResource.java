package io.quarkus.qe.consumers.utils;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.qe.model.channels.Channels;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;

public class InMemoryKafkaResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        env.putAll(InMemoryConnector.switchIncomingChannelsToInMemory(Channels.NEW_REPOSITORY));
        env.putAll(InMemoryConnector.switchIncomingChannelsToInMemory(Channels.UPDATE_REPOSITORY));
        env.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory(Channels.ENRICH_REPOSITORY));
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}