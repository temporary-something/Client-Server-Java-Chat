package model;

import java.util.List;

public class ContextContent implements Content {

    private static final long serialVersionUID = 1058388056805703738L;

    private List<User> users;

    public static ContextContent newInstance(List<User> users) {
        return new ContextContent(users);
    }

    private ContextContent(List<User> users) {
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
