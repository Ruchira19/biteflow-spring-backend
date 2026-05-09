package lk.ijse.cmjd112.FoodOrderingSystem.exception;

/**
 * Exception thrown when a requested resource cannot be found in the database.
 * Typically results in HTTP 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
