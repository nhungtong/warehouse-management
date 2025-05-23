package com.techbytedev.warehousemanagement.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class StockInRequest {
    private String code;
    private List<ProductInRequest> products;
    private String username;
    private String note;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ProductInRequest> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInRequest> products) {
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
