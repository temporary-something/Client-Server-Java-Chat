package server;

import pojo.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private ServerSocket server = null;
    private boolean isRunning = true;

    private static final HashMap<Long, User> users = new HashMap<>();
    private static final HashMap<Long, ClientProcessor> clients = new HashMap<>();

    static Collection<User> addUser(final User user, final ClientProcessor client) {
        final Collection<User> usersBefore = new LinkedList<>();
        synchronized (Server.users) {
            for (User u : Server.users.values()) {
                usersBefore.add(u.clone());
            }
            synchronized (Server.clients) {
                Server.users.put(user.getId(), user);
                Server.clients.put(user.getId(), client);
            }
        }
        return usersBefore;
    }

    static void removeUser(final long id) {
        synchronized (Server.users) {
            synchronized (Server.clients) {
                Server.users.remove(id);
                Server.clients.remove(id);
            }
        }
    }

    static ClientProcessor findClient(final long id) {
        synchronized (Server.clients) {
            return Server.clients.get(id);
        }
    }


    public Server(String host, int port){
        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open(){
        Thread t = new Thread(() -> {
            while(isRunning){
                try {
                    Socket client = server.accept();
                    System.out.println("Client Connexion received.");
                    Thread t1 = new Thread(new ClientProcessor(client));
                    t1.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                server = null;
            }
        });

        t.start();
    }

    public void close(){
        isRunning = false;
    }
}
