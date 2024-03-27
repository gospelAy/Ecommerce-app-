package ecommerce.ecommerce.exceptions;

public class InvalidOTPException extends UserNotFoundException{
    public InvalidOTPException(String message) {
        super(message);
    }
}
