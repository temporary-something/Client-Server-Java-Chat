package model.enums;

import java.io.Serializable;

public enum ResponseType implements Serializable {
    //User successfully connected, initialize the context with all the currently connected users.
    CONNECTED,
    //Text message received.
    MESSAGE,
    //Text message successfully sent.
    MESSAGE_SENT,
    //Prepare the user to receive a file.
    PREPARE_RECEIVE_FILE,
    //File chunk received (chunks of bytes representing a file).
    FILE_CHUNK,
    //File message received (name of a file).
    FILE_MESSAGE,
    //Can send the file.
    CAN_SEND_FILE,
    //File successfully sent.
    FILE_SENT,
    //A user just connected.
    ADD_USER,
    //A user just disconnected.
    REMOVE_USER,
    //Errors.
    WRONG_PARAMETERS, DESTINATION_NOT_FOUND, INTERNAL_SERVER_ERROR, INSUFFICIENT_MEMORY
}
