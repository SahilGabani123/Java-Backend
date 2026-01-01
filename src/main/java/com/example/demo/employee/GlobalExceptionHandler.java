package com.example.demo.employee;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.JwtAuthenticationException;
import com.example.demo.exception.PhoneAlreadyExistsException;
import com.example.demo.exception.ProjectNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===================== VALIDATION =====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Validation failed", errors));
    }

    // ===================== MISSING PARAM =====================
    

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(
            MissingServletRequestParameterException ex) {
    	
        String param = ex.getParameterName().replace("_", " ");
        String message = Character.toUpperCase(param.charAt(0))
                + param.substring(1) + " is required params";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, message, null));
    }

    // ===================== TYPE MISMATCH =====================

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String message = "Invalid value for " + ex.getName();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, message, null));
    }

    // ===================== DATA INTEGRITY =====================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        String message = "Duplicate value already exists";

        if (ex.getCause() != null) {
            String cause = ex.getCause().getMessage();
            if (cause.contains("company_email")) {
                message = "Company email already exists";
            } else if (cause.contains("phone_number")) {
                message = "Phone number already exists";
            } else if (cause.contains("name")) {
                message = "Employee name already exists";
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, message, null));
    }

    // ===================== JWT / AUTH =====================

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(
            JwtAuthenticationException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        response.put("status_code", ex.getCode());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    // ===================== NOT FOUND =====================

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEmployeeNotFound(
            EmployeeNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiResponse> handleProjectNotFound(
            ProjectNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    // ===================== CUSTOM DUPLICATES =====================

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExists(
            EmailAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handlePhoneExists(
            PhoneAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    // ===================== HTTP METHOD =====================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiResponse(false, "HTTP method not supported", null));
    }

    // ===================== INVALID JSON =====================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Invalid request body", null));
    }

    // ===================== ILLEGAL ARG =====================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage(), null));
    }

    // ===================== FALLBACK (LAST) =====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllUnhandledExceptions(
            Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false,
                        "Something went wrong. Please try again later.", ex.getMessage()));
        
    }
    

    // ===================== ENDPOINT NOT FOUND (LAST) =====================
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse> handleEndpointNotFound(
            NoHandlerFoundException ex) {

        String message = "Endpoint not found";

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, message, null));
    }
}
