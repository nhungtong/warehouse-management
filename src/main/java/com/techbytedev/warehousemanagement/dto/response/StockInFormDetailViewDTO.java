package com.techbytedev.warehousemanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockInFormDetailViewDTO {
    private Integer id;
    private String code;
    private String note;
    private LocalDateTime createdAt;
    private String username;
    private String invoiceFile;
    private List<StockInDetailDTO> details;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getInvoiceFile() {
        return invoiceFile;
    }

    public void setInvoiceFile(String invoiceFile) {
        this.invoiceFile = invoiceFile;
    }

    public List<StockInDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<StockInDetailDTO> details) {
        this.details = details;
    }
}
