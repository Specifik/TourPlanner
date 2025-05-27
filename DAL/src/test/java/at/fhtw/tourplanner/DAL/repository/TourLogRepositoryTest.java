package at.fhtw.tourplanner.DAL.repository; // Test package with uppercase DAL

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.dal.repository.TourLogRepository;
import at.fhtw.tourplanner.dal.repository.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DALConfiguration.class)
@EnableJpaRepositories(basePackages = "at.fhtw.tourplanner.dal.repository") // Direct config
@EntityScan(basePackages = "at.fhtw.tourplanner.model")          // Direct config
public class TourLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TourLogRepository tourLogRepository;

    @Autowired
    private TourRepository tourRepository;

    private Tour testTour;

    @BeforeEach
    void setUp() {
        testTour = new Tour("Test Tour for Logs", "Start", "End", "Walk", "Desc");
        entityManager.persist(testTour);
        entityManager.flush();
    }

    @Test
    public void whenSaveTourLog_thenLogIsPersisted() {
        TourLog log = new TourLog(testTour, LocalDateTime.now(), "Great day", "Easy", 5.0, 60, 5);
        TourLog savedLog = tourLogRepository.save(log);

        assertThat(savedLog).isNotNull();
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getComment()).isEqualTo("Great day");
        assertThat(savedLog.getTour().getId()).isEqualTo(testTour.getId());
    }

    @Test
    public void whenFindById_thenReturnLog() {
        TourLog log = new TourLog(testTour, LocalDateTime.now(), "Finding this log", "Medium", 2.0, 30, 3);
        entityManager.persist(log);
        entityManager.flush();
        Integer logId = log.getId();

        Optional<TourLog> foundLog = tourLogRepository.findById(logId);

        assertThat(foundLog).isPresent();
        assertThat(foundLog.get().getComment()).isEqualTo("Finding this log");
    }

    @Test
    public void whenFindByTour_Id_thenReturnLogsForThatTour() {
        TourLog log1 = new TourLog(testTour, LocalDateTime.now().minusDays(1), "Log 1", "Easy", 1.0, 10, 1);
        TourLog log2 = new TourLog(testTour, LocalDateTime.now(), "Log 2", "Hard", 2.0, 20, 2);
        entityManager.persist(log1);
        entityManager.persist(log2);

        Tour otherTour = new Tour("Other Tour", "A", "B", "Car", "Other Desc");
        entityManager.persist(otherTour);
        entityManager.persist(new TourLog(otherTour, LocalDateTime.now(), "Other Log", "Easy", 1.0, 5, 5));
        entityManager.flush();

        List<TourLog> logs = tourLogRepository.findByTour_Id(testTour.getId());

        assertThat(logs).hasSize(2);
        assertThat(logs).extracting(TourLog::getComment).containsExactlyInAnyOrder("Log 1", "Log 2");
    }

    @Test
    public void whenDeleteTourLog_thenLogIsRemoved() {
        TourLog log = new TourLog(testTour, LocalDateTime.now(), "To be deleted", "Easy", 1.0, 10, 1);
        entityManager.persist(log);
        entityManager.flush();
        Integer logId = log.getId();

        tourLogRepository.deleteById(logId);
        entityManager.flush();
        entityManager.clear();

        Optional<TourLog> deletedLog = tourLogRepository.findById(logId);
        assertThat(deletedLog).isNotPresent();
    }
    
    @Test
    public void whenFindBySearchTextInComment_thenReturnMatchingLogs() {
        entityManager.persist(new TourLog(testTour, LocalDateTime.now(), "Unique comment here", "Easy", 1.0, 10, 1));
        entityManager.persist(new TourLog(testTour, LocalDateTime.now(), "Another log", "Medium", 2.0, 20, 2));
        entityManager.flush();

        List<TourLog> foundLogs = tourLogRepository.findBySearchText("Unique comment");
        assertThat(foundLogs).hasSize(1);
        assertThat(foundLogs.get(0).getComment()).isEqualTo("Unique comment here");
    }

    @Test
    public void whenFindBySearchTextInDifficulty_thenReturnMatchingLogs() {
        entityManager.persist(new TourLog(testTour, LocalDateTime.now(), "A log", "SuperHard", 1.0, 10, 1));
        entityManager.persist(new TourLog(testTour, LocalDateTime.now(), "Another log", "Easy", 2.0, 20, 2));
        entityManager.flush();

        List<TourLog> foundLogs = tourLogRepository.findBySearchText("SuperHard");
        assertThat(foundLogs).hasSize(1);
        assertThat(foundLogs.get(0).getDifficulty()).isEqualTo("SuperHard");
    }

    @Test
    public void whenFindBySearchTextNonExisting_thenReturnEmptyList() {
        entityManager.persist(new TourLog(testTour, LocalDateTime.now(), "A log", "Easy", 1.0, 10, 1));
        entityManager.flush();

        List<TourLog> foundLogs = tourLogRepository.findBySearchText("nonexistenttext");
        assertThat(foundLogs).isEmpty();
    }
} 