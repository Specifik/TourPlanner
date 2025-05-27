package at.fhtw.tourplanner.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
public class Tour implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "from_location", length = 100)
    private String from;

    @Column(name = "to_location", length = 100)
    private String to;

    @Column(name = "transport_type", length = 50)
    private String transportType;

    @Column(length = 500)
    private String description;

    // API data
    @Column(name = "tour_distance")
    private double tourDistance;

    @Column(name = "estimated_time")
    private int estimatedTime;

    @Column(name = "route_image_path")
    private String routeImagePath;

    @Column(name = "route_geo_json", columnDefinition = "TEXT")
    private String routeGeoJson;

    // One-to-many relationship with TourLog
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TourLog> tourLogs = new ArrayList<>();

    // Constructors
    public Tour() {}

    public Tour(int id, String name, String from, String to) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public Tour(int id, String name, String from, String to, String transportType, String description) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
        this.transportType = transportType;
        this.description = description;
    }

    // Constructor for tests (ID will be auto-generated)
    public Tour(String name, String from, String to, String transportType, String description) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.transportType = transportType;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTourDistance() { return tourDistance; }
    public void setTourDistance(double tourDistance) { this.tourDistance = tourDistance; }

    public int getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }

    public String getRouteImagePath() { return routeImagePath; }
    public void setRouteImagePath(String routeImagePath) { this.routeImagePath = routeImagePath; }

    public String getRouteGeoJson() { return routeGeoJson; }
    public void setRouteGeoJson(String routeGeoJson) { this.routeGeoJson = routeGeoJson; }

    public List<TourLog> getTourLogs() { return tourLogs; }
    public void setTourLogs(List<TourLog> tourLogs) { this.tourLogs = tourLogs; }

    @Override
    public String toString() {
        if (tourDistance > 0) {
            return name + String.format(" (%.1f km, %d min)", tourDistance, estimatedTime);
        }
        return name;
    }
}