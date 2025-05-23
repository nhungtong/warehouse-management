package com.techbytedev.warehousemanagement.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class StockOutRequest {
    private String code;
    private List<ProductOutRequest> products;
    private String destination;
    private String username;
    private String note;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<ProductOutRequest> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOutRequest> products) {
        this.products = products;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
