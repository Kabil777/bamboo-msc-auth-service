package com.kabi.auth_msc.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public class ErrorResponseGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ErrorResponseGenerator() {}

    public static void writeError(
            HttpServletResponse response,
            int status,
            String name,
            String message,
            Map<String, Object> details)
            throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body =
                Map.of(
                        "error",
                        Map.of(
                                "name", name,
                                "message", message,
                                "details", details));

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    public static ResponseEntity<Map<String, Object>> of(
            HttpStatus status, String name, String message, Map<String, Object> details) {
        return ResponseEntity.status(status)
                .body(
                        Map.of(
                                "error",
                                Map.of(
                                        "name", name,
                                        "message", message,
                                        "details", details)));
    }
}
