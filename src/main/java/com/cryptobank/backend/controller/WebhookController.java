//package com.cryptobank.backend.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.google.api.client.util.Value;
//
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
//@RestController
//@RequestMapping("/api")
//public class WebhookController {
//
//    //@Value("${svix.secret}")
//    private String svixSecret="whsec_HCzsdXnjPTfyKEY3EogywPpDdzzmE0I3";
//
//    @PostMapping("/webhook/payos")
//    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
//                                                @RequestHeader("svix-id") String svixId,
//                                                @RequestHeader("svix-timestamp") String svixTimestamp,
//                                                @RequestHeader("svix-signature") String svixSignature) {
//        // Xác minh chữ ký
//        if (!verifySignature(payload, svixId, svixTimestamp, svixSignature, svixSecret)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chữ ký không hợp lệ");
//        }
//
//        // Xử lý payload, ví dụ: phân tích JSON và lưu vào cơ sở dữ liệu
//        return ResponseEntity.ok("Webhook đã được nhận thành công");
//    }
//
//    private boolean verifySignature(String payload, String svixId, String svixTimestamp, String svixSignature, String secret) {
//        // Triển khai xác minh chữ ký bằng HMAC-SHA256
//        // Đây là phần giữ chỗ; tham khảo tài liệu Svix để triển khai thực tế
//        return true; // Thay thế bằng logic thực tế
//    }
//}