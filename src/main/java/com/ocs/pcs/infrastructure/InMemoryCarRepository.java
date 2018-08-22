package com.ocs.pcs.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocs.pcs.domain.Car;

public class InMemoryCarRepository implements CarRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCarRepository.class);

    private io.vertx.reactivex.core.Vertx rxVertx;
    private Map<String, Car> cars;

    public InMemoryCarRepository(Vertx vertx) {
        this.rxVertx = new io.vertx.reactivex.core.Vertx(vertx);
        this.cars = new HashMap<>();
    }


    @Override
    public void add(Car car, Handler<AsyncResult<Car>> resultHandler) {
        // Some validation
        if (Objects.isNull(car.getId())) {
            resultHandler.handle(ServiceException.fail(503, "Unable to create a car due to missing id."));
        } else if (cars.containsKey(car.getId())) {
            resultHandler.handle(ServiceException.fail(503, "A car with the given id already exists."));
        } else {
            // Store it
            cars.put(car.getId(), car);
            resultHandler.handle(Future.succeededFuture(cars.get(car.getId())));
        }
    }

    @Override
    public void save(Car car, Handler<AsyncResult<Car>> resultHandler) {
        Car previousValue = cars.put(car.getId(), car);

        if (Objects.nonNull(previousValue)) {
            LOGGER.debug("Replaced:\n{} \nwith:\n{}", previousValue.toJson().encodePrettily(), car.toJson().encodePrettily());
        } else {
            LOGGER.debug("New car saved:\n", car.toJson().encodePrettily());
        }

        resultHandler.handle(Future.succeededFuture(cars.get(car.getId())));
    }

    @Override
    public void delete(String id, Handler<AsyncResult<Void>> resultHandler) {
        if (Objects.isNull(id)) {
            resultHandler.handle(ServiceException.fail(503, "Unable to delete car due to missing car id"));
        } else {
            Car removed = cars.remove(id);
            if (Objects.isNull(removed)) {
                resultHandler.handle(ServiceException.fail(404, "Unable to remove Car with id " + id));
            } else {
                resultHandler.handle(Future.succeededFuture());
            }
        }
    }

    @Override
    public void findOrError(String id, Handler<AsyncResult<Car>> resultHandler) {
        if (Objects.isNull(id)) {
            resultHandler.handle(ServiceException.fail(503, "Unable to find a car due to missing id."));
        } else {
            Car slot = cars.get(id);
            if (Objects.nonNull(slot)) {
                resultHandler.handle(Future.succeededFuture(slot));
            } else {
                resultHandler.handle(ServiceException.fail(404, "Unable to find a slot with id " + id));
            }
        }
    }

    @Override
    public void findAll(Handler<AsyncResult<List<Car>>> resultHandler) {
        if (this.cars.isEmpty()) {
            resultHandler.handle(Future.succeededFuture(Collections.EMPTY_LIST));
        } else {
            resultHandler.handle(Future.succeededFuture(new ArrayList(cars.values())));
        }
    }

    @Override
    public void findByNameOrError(String name, Handler<AsyncResult<Car>> resultHandler) {
        try {
            Car car = this.cars.values().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().get();
            resultHandler.handle(Future.succeededFuture(car));
        } catch (NoSuchElementException e) {
            resultHandler.handle(ServiceException.fail(404, "Unable to find car with name " + name));
        }
    }
}
