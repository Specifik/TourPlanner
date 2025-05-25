package at.fhtw.tourplanner.model;

import java.io.Serializable;

public class Tour implements Serializable {
    private int id;
    private String name;
    private String from;
    private String to;
    private String transportType;
    private String description;

    // API data
    private double tourDistance;
    private int estimatedTime;
    private String routeImagePath;
    private String routeGeoJson;

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

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTourDistance() {
        return tourDistance;
    }

    public void setTourDistance(double tourDistance) {
        this.tourDistance = tourDistance;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getRouteImagePath() {
        return routeImagePath;
    }

    public void setRouteImagePath(String routeImagePath) {
        this.routeImagePath = routeImagePath;
    }

    public String getRouteGeoJson() {
        return routeGeoJson;
    }

    public void setRouteGeoJson(String routeGeoJson) {
        this.routeGeoJson = routeGeoJson;
    }

    @Override
    public String toString() {
        if (tourDistance > 0) {
            return name + String.format(" (%.1f km, %d min)", tourDistance, estimatedTime);
        }
        return name;
    }
}