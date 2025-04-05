package ru.sergalas.customer.client.exeception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class ClientBadRequestException extends RuntimeException {
    private final List<String> errorMessages;

    public ClientBadRequestException(Throwable cause, List<String> errorMessages) {
        super(cause);
        this.errorMessages = errorMessages;
    }
}
