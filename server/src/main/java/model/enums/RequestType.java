package model.enums;

import java.io.Serializable;

public enum RequestType implements Serializable {
    //To request a connection to the server.
    CONNECT,
    //To alert the server of the client's will to disconnect.
    DISCONNECT,
    //To send a message.
    SEND_MESSAGE,
    //To send a file.
    SEND_FILE,
    //To request a file.
    REQUEST_FILE
}
