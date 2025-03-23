package at.fhtw.tourplanner.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TourLog implements Serializable {
    private int id;
    private int tourId;
    private LocalDateTime dateTime;
    private String comment;
    private String difficulty;
    private double totalDistance;
    private int totalTime;  // in minutes
    private int rating;     // 1-5 stars

    public TourLog() {
    }

    public TourLog(int id, int tourId, LocalDateTime dateTime, String comment, String difficulty,
                   double totalDistance, int totalTime, int rating) {
        this.id = id;
        this.tourId = tourId;
        this.dateTime = dateTime;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Log #" + id + " (" + dateTime.toLocalDate() + ")";
    }
}