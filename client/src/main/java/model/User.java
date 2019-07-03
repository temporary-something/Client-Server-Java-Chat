package model;

import java.util.Objects;

public class User extends BasePojo {

    private static final long serialVersionUID = 4335390648613369110L;

    private String ipAddress;
    private String name;

    public static User newInstance(String ipAddress, String name) {
        return new User(ipAddress, name);
    }

    private User(String ipAddress, String name) {
        super();
        this.ipAddress = ipAddress;
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
