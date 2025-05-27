package at.fhtw.tourplanner.DAL.repository;

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.dal.repository.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DALConfiguration.class)
@EnableJpaRepositories(basePackages = "at.fhtw.tourplanner.dal.repository")
@EntityScan(basePackages = "at.fhtw.tourplanner.model")
// @ActiveProfiles("test") // If you have a specific test application.properties/yml
public class TourRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TourRepository tourRepository;

    @Test
    public void whenSaveTour_thenTourIsPersisted() {
        Tour tour = new Tour("Summer Walk", "Vienna", "Berlin", "Walking", "A nice walk.");
        Tour savedTour = tourRepository.save(tour);

        assertThat(savedTour).isNotNull();
        assertThat(savedTour.getId()).isNotNull();
        assertThat(savedTour.getName()).isEqualTo("Summer Walk");
    }

    @Test
    public void whenFindById_thenReturnTour() {
        Tour tour = new Tour("Mountain Hike", "Alps", "Peak", "Hiking", "Challenging hike.");
        entityManager.persist(tour); // Use entityManager to prepare data
        entityManager.flush();
        Integer tourId = tour.getId();

        Optional<Tour> foundTour = tourRepository.findById(tourId);

        assertThat(foundTour).isPresent();
        assertThat(foundTour.get().getName()).isEqualTo("Mountain Hike");
    }

    @Test
    public void whenFindAll_thenReturnAllTours() {
        Tour tour1 = new Tour("City Tour", "Paris", "Eiffel Tower", "Bus", "Sightseeing.");
        Tour tour2 = new Tour("Beach Relax", "Maldives", "Beach", "None", "Relaxation.");

        entityManager.persist(tour1);
        entityManager.persist(tour2);
        entityManager.flush();

        List<Tour> tours = tourRepository.findAll();

        assertThat(tours).hasSize(2).contains(tour1, tour2);
    }

    @Test
    public void whenDeleteTour_thenTourIsRemoved() {
        Tour tour = new Tour("Forest Trail", "Black Forest", "Lake", "Biking", "Nature ride.");
        entityManager.persist(tour);
        entityManager.flush();
        Integer tourId = tour.getId();

        tourRepository.deleteById(tourId);
        entityManager.flush(); // ensure delete is processed
        entityManager.clear(); // clear persistence context to ensure fresh read

        Optional<Tour> deletedTour = tourRepository.findById(tourId);
        assertThat(deletedTour).isNotPresent();
    }
    
    @Test
    public void whenFindByKeywordInName_thenReturnMatchingTours() {
        Tour tour1 = new Tour("Vienna City Walk", "Stephansplatz", "Karlsplatz", "Walking", "Historic city center exploration.");
        Tour tour2 = new Tour("Danube Bike Ride", "Passau", "Vienna", "Biking", "Scenic river path.");
        entityManager.persist(tour1);
        entityManager.persist(tour2);
        entityManager.flush();

        List<Tour> foundTours = tourRepository.findBySearchText("Vienna City Walk");
        assertThat(foundTours).hasSize(1).containsExactly(tour1);

        foundTours = tourRepository.findBySearchText("Danube Bike Ride");
        assertThat(foundTours).hasSize(1).containsExactly(tour2);
    }

    @Test
    public void whenFindByKeywordInDescription_thenReturnMatchingTours() {
        Tour tour1 = new Tour("Alpine Adventure", "Chamonix", "Mont Blanc", "Climbing", "High altitude challenge.");
        Tour tour2 = new Tour("Coastal Drive", "Nice", "Monaco", "Car", "Scenic ocean views and luxury cars.");
        entityManager.persist(tour1);
        entityManager.persist(tour2);
        entityManager.flush();

        List<Tour> foundTours = tourRepository.findBySearchText("Scenic ocean views");
        assertThat(foundTours).hasSize(1).containsExactly(tour2);
    }

    @Test
    public void whenFindByKeywordInNameAndDescription_thenReturnMatchingTours() {
        Tour tour1 = new Tour("Historic Prague Walk", "Old Town Square", "Charles Bridge", "Walking", "Explore historic Prague landmarks.");
        Tour tour2 = new Tour("Prague Castle Tour", "Prague Castle", "St. Vitus Cathedral", "Walking", "A tour of the vast castle complex.");
        entityManager.persist(tour1);
        entityManager.persist(tour2);
        entityManager.flush();

        List<Tour> foundTours = tourRepository.findBySearchText("Prague");
        assertThat(foundTours).hasSize(2).contains(tour1, tour2);
    }

    @Test
    public void whenFindByNonExistingKeyword_thenReturnEmptyList() {
        Tour tour1 = new Tour("Kyoto Temple Run", "Fushimi Inari", "Kiyomizu-dera", "Running", "Spiritual journey through temples.");
        entityManager.persist(tour1);
        entityManager.flush();

        List<Tour> foundTours = tourRepository.findBySearchText("nonexistentkeyword");
        assertThat(foundTours).isEmpty();
    }
} 