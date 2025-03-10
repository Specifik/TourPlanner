package at.fhtw.medialib.model;


import java.io.Serializable;

public class MediaItem implements Serializable {
    private int id;
    private String name;
    private double duration;
    private String content;

    public MediaItem(int id, String name, double duration, String content) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.content = content;
    }

    public MediaItem() {
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return name;
    }
}
