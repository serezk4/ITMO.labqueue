package com.serezka.telegram.bot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Router for executor services
 * Helps load balancing
 * @version 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class ExecutorServiceRouter {
    List<ExecutorService> services;
    int size;

    @NonFinal boolean shutdown = false;

    public ExecutorServiceRouter(int size) {
        services = new ArrayList<>(size);
        IntStream.range(0, size).mapToObj(i -> Executors.newSingleThreadExecutor()).forEach(services::add);

        this.size = size;

        log.info("created executor service router with {} services", this.size);
    }

    /**
     * Check if all services are shut down
     * @return true if all services are shut down
     */
    public boolean isShutdown() {
        return !(!shutdown || !(shutdown = services.stream().allMatch(ExecutorService::isShutdown)));
    }


    /**
     * Route task to executor
     * @param id task id
     * @param runnable task
     */
    public void route(long id, Runnable runnable) {
        log.info("sent task#{} to executor#{}", id, (int) ((id) % size));
        services.get((int) ((id) % size)).execute(runnable);
    }

    /**
     * Shut down all services
     */
    public void shutdown() {
        log.info("shutting down...");
        shutdown = true;
        services.forEach(ExecutorService::shutdown);
        log.info("turned off successfully");
    }

}