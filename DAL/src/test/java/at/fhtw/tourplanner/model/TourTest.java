package at.fhtw.tourplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TourTest {

    private Tour tour;

    @BeforeEach
    public void setUp() {
        tour = new Tour(1, "Donauinsel Runde", "Floridsdorfer Brücke", "Reichsbrücke", "Biking", "Gemütliche Radtour mit schöner Aussicht");
    }

    @Test
    public void testTourCreation() {
        assertEquals(1, tour.getId());
        assertEquals("Donauinsel Runde", tour.getName());
        assertEquals("Floridsdorfer Brücke", tour.getFrom());
        assertEquals("Reichsbrücke", tour.getTo());
        assertEquals("Biking", tour.getTransportType());
        assertEquals("Gemütliche Radtour mit schöner Aussicht", tour.getDescription());
    }

    @Test
    public void testTourModification() {
        tour.setName("Donaukanal Spaziergang");
        tour.setFrom("Schwedenplatz");
        tour.setTo("Urania");
        tour.setTransportType("Walking");
        tour.setDescription("Kurzer Abendspaziergang entlang des Wassers");

        assertEquals("Donaukanal Spaziergang", tour.getName());
        assertEquals("Schwedenplatz", tour.getFrom());
        assertEquals("Urania", tour.getTo());
        assertEquals("Walking", tour.getTransportType());
        assertEquals("Kurzer Abendspaziergang entlang des Wassers", tour.getDescription());
    }
}