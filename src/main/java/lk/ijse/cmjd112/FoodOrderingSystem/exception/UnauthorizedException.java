package lk.ijse.cmjd112.FoodOrderingSystem.exception;

/**
 * Exception thrown when a user lacks required permissions or authorization.
 * Typically results in HTTP 403 Forbidden response.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
