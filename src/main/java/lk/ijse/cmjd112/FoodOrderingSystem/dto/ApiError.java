package lk.ijse.cmjd112.FoodOrderingSystem.dto;

import java.time.LocalDateTime;

/**
 * Standard API error response DTO.
 * Returned by GlobalExceptionHandler for all error responses.
 * Contains error details, timestamp, HTTP status, and request path for debugging.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
