package com.ocs.pcs.infrastructure;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.List;

import com.ocs.pcs.domain.Car;

@VertxGen
@ProxyGen
public interface CarRepository {
    String EVENT_BUS_ADDRESS = "com.ocs.pcs.infrastructure.CarRepository";

    static CarRepository create(Vertx vertx) {
        return new InMemoryCarRepository(vertx);
    }

    static CarRepository createProxy(Vertx vertx) {
        return new CarRepositoryVertxEBProxy(vertx, EVENT_BUS_ADDRESS);
    }

    void add(Car car, Handler<AsyncResult<Car>> resultHandler);

    void save(Car car, Handler<AsyncResult<Car>> resultHandler);

    void delete(String id, Handler<AsyncResult<Void>> resultHandler);

    void findOrError(String id, Handler<AsyncResult<Car>> resultHandler);

    void findAll(Handler<AsyncResult<List<Car>>> resultHandler);

    void findByNameOrError(String name, Handler<AsyncResult<Car>> resultHandler);
}
