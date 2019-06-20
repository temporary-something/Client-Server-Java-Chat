package model;

import java.util.List;

public class ContextContent implements Content {

    private List<User> users;

    public ContextContent(List<User> users) {
        super();
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ContextContent{" +
                "users=" + users +
                '}';
    }
}
