package com.techbytedev.warehousemanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockOutFormDetailViewDTO {
    private Integer id;
    private String code;
    private String destination;
    private String username;
    private LocalDateTime createdAt;
    private String note;
    private List<StockOutDetailDTO> details;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<StockOutDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<StockOutDetailDTO> details) {
        this.details = details;
    }
}
