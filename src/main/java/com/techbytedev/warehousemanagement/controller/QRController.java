package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.util.QRCodeGeneratorUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.web.bind.annotation.*;
import com.google.zxing.WriterException;

@RestController
@RequestMapping("/api/qrcode")
public class QRController {

    // API tạo QR, lưu file
    @PostMapping("/generate/{productCode}")
    public ResponseEntity<String> generateQRCode(@PathVariable String productCode) {
        String filePath = "uploads/qrcode/" + productCode + ".png";
        try {
            QRCodeGeneratorUtil.generateQRCodeFile(productCode, filePath);
            return ResponseEntity.ok("QR Code generated and saved to " + filePath);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error generating QR code: " + e.getMessage());
        }
    }

    // API lấy QR code theo mã sản phẩm (trả về ảnh PNG)
    @GetMapping(value = "/{productCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getProductQRCode(@PathVariable String productCode) {
        String filePath = "uploads/qrcode/" + productCode + ".png";
        try {
            byte[] image = Files.readAllBytes(Paths.get(filePath));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
        } catch (IOException e) {
            return ResponseEntity.status(404).build();
        }
    }
}

