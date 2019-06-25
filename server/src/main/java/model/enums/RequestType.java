package model.enums;

import java.io.Serializable;

public enum RequestType implements Serializable {
    //To request a connection to the server.
    CONNECT,
    //To alert the server of the client's will to disconnect.
    DISCONNECT,
    //To send a message.
    SEND_MESSAGE,
    //Prepare the server to send a file.
    PREPARE_SEND_FILE,
    //To send a file.
    SEND_FILE,
    //To request information about a file.
    PREPARE_REQUEST_FILE,
    //To request a file.
    REQUEST_FILE,
    //Prepare the server to send an audio file.
    PREPARE_SEND_AUDIO,
    //To send an audio file.
    SEND_AUDIO,
    //To request information about an audio file.
    PREPARE_REQUEST_AUDIO,
    //To request an audio file.
    REQUEST_AUDIO
}
