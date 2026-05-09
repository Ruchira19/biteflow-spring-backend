package lk.ijse.cmjd112.FoodOrderingSystem.exception;

/**
 * Exception thrown when a request contains invalid data or violates business rules.
 * Typically results in HTTP 400 Bad Request response.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
