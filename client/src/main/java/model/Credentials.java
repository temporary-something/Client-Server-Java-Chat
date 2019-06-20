package model;

public class Credentials implements Content {

    private String username;

    public static Credentials newInstance(final String username) {
        return new Credentials(username);
    }

    private Credentials(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "username='" + username + '\'' +
                '}';
    }
}
