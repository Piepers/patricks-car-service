package com.ocs.pcs.application;

import com.ocs.pcs.reactivex.domain.CarService;
import io.reactivex.Completable;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.templ.FreeMarkerTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    private Integer port;

    private CarService carService;

    private final FreeMarkerTemplateEngine templateEngine = FreeMarkerTemplateEngine.create();

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        this.vertx = new io.vertx.reactivex.core.Vertx(vertx);

        this.port = Optional
                .ofNullable(context
                        .config()
                        .getJsonObject("http_server"))
                .map(o -> o.getInteger("port")).orElse(5000);

        this.carService = CarService.createProxy(super.vertx);
    }

    @Override
    public void start(Future<Void> future) throws Exception {

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/").handler(this::indexHandler);

        Router subRouter = Router.router(vertx);
        subRouter.route(HttpMethod.GET, "/find").handler(this::findHandler);
        subRouter.route(HttpMethod.POST, "/create").handler(this::createHandler);
        subRouter.route(HttpMethod.PUT, "/update").handler(this::updateHandler);
        subRouter.route(HttpMethod.POST, "/delete").handler(this::deleteHandler);
        router.mountSubRouter("/api", subRouter);

        this.vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(this.port)
                .subscribe(result -> {
                    LOGGER.debug("The server has been started on port {}", result.actualPort());
                    future.complete();
                }, throwable -> {
                    LOGGER.error("Something went wrong");
                    future.fail(throwable);
                });
    }

    private void indexHandler(RoutingContext routingContext) {
        carService
                .rxFindAll()
                .flatMapCompletable(cars -> {
                    routingContext.put("title", "Patrick's car service!");
                    routingContext.put("cars", cars);
//                    routingContext.put("cars", cars.stream().map(car -> car.getName()).collect(Collectors.toList()));
                    return Completable.complete();
                })
                .andThen(templateEngine.rxRender(routingContext, "templates", "/index.ftl"))
                .subscribe(result -> {
                    routingContext.response().putHeader("Content-Type", "text/html");
                    routingContext.response().end(result);
                }, throwable -> routingContext.fail(throwable));

    }

    private void deleteHandler(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        carService.rxDelete(id).subscribe(() -> {
            routingContext.response().setStatusCode(303);
            routingContext.response().putHeader("Location", "/");
            routingContext.response().end();
        }, throwable -> routingContext.fail(throwable));
    }

    private void updateHandler(RoutingContext routingContext) {
    }

    private void findHandler(RoutingContext routingContext) {
        carService
                .rxFindAll()
                .subscribe(cars -> {
                    JsonObject response = new JsonObject().put("cars", new JsonArray(cars));
                    routingContext
                            .response()
                            .putHeader("content-type", "application/json; charset=UTF-8")
                            .end(response.encode(), StandardCharsets.UTF_8.name());
                }, throwable -> {
                    LOGGER.error("Failure while trying to find cars.", throwable);
                    routingContext
                            .response()
                            .putHeader("Content-Type", "application/json; charset=UTF-8")
                            .end(new JsonObject()
                                    .put("Error", throwable.getMessage())
                                    .encode(), StandardCharsets.UTF_8.name());
                });
    }

    private void createHandler(RoutingContext routingContext) {
        String name = routingContext.request().getParam("name");
        String brand = routingContext.request().getParam("brand");
        String type = routingContext.request().getParam("type");
        Integer hbp = Integer.parseInt(routingContext.request().getParam("bhp"));

        carService
                .rxAdd(name, brand, type, hbp)
                .subscribe(car -> {
                    routingContext.response().setStatusCode(303);
                    routingContext.response().putHeader("Location", "/");
                    routingContext.response().end();
                }, throwable -> routingContext.fail(throwable));

    }
}
