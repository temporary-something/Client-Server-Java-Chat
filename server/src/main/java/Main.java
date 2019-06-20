import server.Server;

public class Main {

    public static void main(String[] args) {

        final String host = "0.0.0.0";
        final int port = 6666;

        Server server = new Server(host, port);
        server.open();
        System.out.println("Serveur initialisé.");
    }
}
