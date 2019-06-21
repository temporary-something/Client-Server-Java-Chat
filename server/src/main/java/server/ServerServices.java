package server;

import client.ClientProcessor;
import model.User;

import java.util.Collection;

public interface ServerServices {

    void open();
    void close();
    ClientProcessor findClient(long id);
    void addUser(User user, ClientProcessor clientProcessor, Collection<User> clientUsers);
    void removeUser(long id);
}
