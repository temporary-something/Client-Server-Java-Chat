package model.model;

import model.Content;

public class Credentials implements Content {

    private String username;

    public static model.Credentials newInstance(final String username) {
        return new model.Credentials(username);
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
