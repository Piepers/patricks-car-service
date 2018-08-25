package com.ocs.pcs.infrastructure;

import com.ocs.pcs.domain.Car;
import com.ocs.pcs.domain.CarService;
import com.ocs.pcs.reactivex.infrastructure.CarRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceException;

import java.util.List;
import java.util.UUID;

public class CarServiceImpl implements CarService {

    private final CarRepository repository;
    private final io.vertx.reactivex.core.Vertx rxVertx;

    public CarServiceImpl(Vertx vertx) {
        this.rxVertx = new io.vertx.reactivex.core.Vertx(vertx);
        this.repository = CarRepository.createProxy(rxVertx);
    }

    @Override
    public void add(String name, String brand, String type, Integer hbp, Handler<AsyncResult<Car>> resultHandler) {
        this.repository
                .rxFindByNameOrError(name)
                .subscribe(car ->
                                resultHandler
                                        .handle(ServiceException
                                                .fail(503, "The car you add already exists in the collection.")),
                        throwable -> this.repository
                                .rxAdd(new Car(UUID.randomUUID().toString(), name, brand, type, hbp))
                                .subscribe(c -> resultHandler.handle(Future.succeededFuture(c)),
                                        addError -> resultHandler.handle(Future.failedFuture(addError))));
    }

    @Override
    public void delete(String id, Handler<AsyncResult<Void>> resultHandler) {
        this.repository
                .rxDelete(id)
                .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                        throwable -> resultHandler.handle(Future.failedFuture(throwable)));
    }

    @Override
    public void findAll(Handler<AsyncResult<List<Car>>> resultHandler) {
        this.repository
                .rxFindAll()
                .subscribe(cars -> resultHandler.handle(Future.succeededFuture(cars)),
                        throwable -> resultHandler.handle(Future.failedFuture(throwable)));
    }
}
