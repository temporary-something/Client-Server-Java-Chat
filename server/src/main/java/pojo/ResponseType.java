package pojo;

import java.io.Serializable;

public enum ResponseType implements Serializable {
    CONNECTED,
    MESSAGE, SUCCESS,
    FILE_MESSAGE, FILE_SENT,
    FILE,
    ADD_USER, REMOVE_USER,
    WRONG_PARAMETERS, DESTINATION_NOT_FOUND, INTERNAL_SERVER_ERROR;

}
