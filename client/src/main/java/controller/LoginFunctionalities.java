package controller;

import model.Credentials;

public interface LoginFunctionalities {

    void connect(Credentials credentials, String host, int port);
}
