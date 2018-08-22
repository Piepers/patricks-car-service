package com.ocs.pcs;

import io.reactivex.Completable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocs.pcs.application.HttpServerVerticle;
import com.ocs.pcs.domain.CarService;
import com.ocs.pcs.infrastructure.CarRepository;

public class MainVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Future<Void> future) {
        final ConfigStoreOptions mainConfigStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "app-conf.json"));

        final ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(mainConfigStore);

        final ConfigRetriever configRetriever = ConfigRetriever.create(this.vertx, options);

        configRetriever
                .rxGetConfig()
                .flatMapCompletable(configuration -> {
                    new ServiceBinder(vertx.getDelegate()).setAddress(CarRepository.EVENT_BUS_ADDRESS)
                            .register(CarRepository.class, CarRepository.create(vertx.getDelegate()));
                    new ServiceBinder(vertx.getDelegate()).setAddress(CarService.EVENT_BUS_ADDRESS)
                            .register(CarService.class, CarService.create(vertx.getDelegate()));
                    return Completable.
                            fromAction(() -> LOGGER.debug("Registered services, deploying backend components"))
                            .andThen(this.vertx
                                    .rxDeployVerticle(HttpServerVerticle.class.getName(), new DeploymentOptions().setConfig(configuration))
                                    .toCompletable());
                })
                .subscribe(() -> {
                            LOGGER.info("Your application has been deployed successfully");
                            future.complete();
                        },
                        throwable -> {
                            LOGGER.error("Something went wrong");
                            future.fail(throwable);
                        });

    }

}
