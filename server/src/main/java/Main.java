import server.ServerServices;
import server.impl.ServerServicesImpl;

public class Main {

    public static void main(String[] args) {
        final String host = "0.0.0.0";
        final int port = 6666;

        ServerServices server = new ServerServicesImpl(host, port);
        server.open();
    }
}
