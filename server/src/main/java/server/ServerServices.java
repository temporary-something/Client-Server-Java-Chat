package server;

import client.ClientProcessor;
import model.User;

import java.util.Collection;

public interface ServerServices {

    void open();
    void close();
    ClientProcessor findClient(long id);
    Collection<User> addUser(User user, ClientProcessor clientProcessor);
    void removeUser(long id);
}
