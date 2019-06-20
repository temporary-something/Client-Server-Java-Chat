package pojo;

import java.io.Serializable;

public class User implements Serializable {

    private static long count = 0;

    private long id;
    private String ipAddress;
    private String name;

    public static synchronized User newInstance(String ipAddress, String name) {
        return new User(++count, ipAddress, name);
    }

    private User(long id, String ipAddress, String name) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.name = name;
    }

    public synchronized long getId() {
        return id;
    }

    public synchronized String getIpAddress() {
        return ipAddress;
    }

    public synchronized void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public User clone() {
        return new User(id, ipAddress, name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;
        return (((User) obj).getId() == id);
    }
}
