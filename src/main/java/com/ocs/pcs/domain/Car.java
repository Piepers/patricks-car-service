package com.ocs.pcs.domain;


import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
public class Car {
    private String id;
    private String name;
    private String brand;
    private String type;
    private Integer hbp;

    public Car(String id, String name, String brand, String type, Integer hbp) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.hbp = hbp;
    }

    public Car(JsonObject jsonObject) {
        this.id = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
        this.brand = jsonObject.getString("brand");
        this.type = jsonObject.getString("type");
        this.hbp = jsonObject.getInteger("hbp");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public Integer getHbp() {
        return hbp;
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(id, car.id) &&
                Objects.equals(name, car.name) &&
                Objects.equals(brand, car.brand) &&
                Objects.equals(type, car.type) &&
                Objects.equals(hbp, car.hbp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brand, type, hbp);
    }
}
