package pojo;

import java.io.Serializable;

public enum RequestType implements Serializable {
    CONNECT, DISCONNECT, SEND_MESSAGE, SEND_FILE, REQUEST_FILE
}
