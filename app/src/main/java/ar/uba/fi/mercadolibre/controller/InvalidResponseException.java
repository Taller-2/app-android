package ar.uba.fi.mercadolibre.controller;

public class InvalidResponseException extends Exception {
    public InvalidResponseException(String message) {
        super("Tried to access data from invalid API response: " + message);
    }
}
