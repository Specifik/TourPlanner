package at.fhtw.tourplanner.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour_logs")
public class TourLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    @JsonBackReference
    private Tour tour;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(length = 1000)
    private String comment;

    @Column(length = 50)
    private String difficulty;

    @Column(name = "total_distance")
    private double totalDistance;

    @Column(name = "total_time")
    private int totalTime;  // in minutes

    @Column(nullable = false)
    private int rating;     // 1-5 stars

    public TourLog() {}

    public TourLog(int id, Tour tour, LocalDateTime dateTime, String comment, String difficulty,
                   double totalDistance, int totalTime, int rating) {
        this.id = id;
        this.tour = tour;
        this.dateTime = dateTime;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
    }

    public TourLog(int id, int tourId, LocalDateTime dateTime, String comment, String difficulty,
                   double totalDistance, int totalTime, int rating) {
        this.id = id;
        this.dateTime = dateTime;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
    }

    public TourLog(Tour tour, LocalDateTime dateTime, String comment, String difficulty,
                   double totalDistance, int totalTime, int rating) {
        this.tour = tour;
        this.dateTime = dateTime;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public int getTourId() {
        return tour != null ? tour.getId() : 0;
    }

    public void setTourId(int tourId) {
    }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public int getTotalTime() { return totalTime; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    @Override
    public String toString() {
        return "Log #" + id + " (" + dateTime.toLocalDate() + ")";
    }
}