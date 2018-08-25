package com.ocs.pcs.domain;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.List;

import com.ocs.pcs.infrastructure.CarServiceImpl;

@VertxGen
@ProxyGen
public interface CarService {
    String EVENT_BUS_ADDRESS = "com.ocs.pcs.domain.CarService";

    static CarService create(Vertx vertx) {
        return new CarServiceImpl(vertx);
    }

    static CarService createProxy(Vertx vertx) {
        return new CarServiceVertxEBProxy(vertx, EVENT_BUS_ADDRESS);
    }

    void add(String name, String brand, String type, Integer hbp, Handler<AsyncResult<Car>> resultHandler);

    void delete(String id, Handler<AsyncResult<Void>> resultHandler);

    void findAll(Handler<AsyncResult<List<Car>>> resultHandler);
}
