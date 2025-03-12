package at.fhtw.tourplanner.model;


import java.io.Serializable;

public class Tour implements Serializable {
    private int id;
    private String name;
    private String from;
    private String to;

    public Tour(int id, String name, String from, String to) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public Tour() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return name;
    }
}
