package ecommerce.ecommerce.exceptions;

public class UserNotFoundException extends CustomerException{
    public UserNotFoundException(String message) {
        super(message);
    }
}