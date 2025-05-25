package at.fhtw.tourplanner.dal.repository;

import at.fhtw.tourplanner.model.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourLogRepository extends JpaRepository<TourLog, Integer> {

    // Find all logs for a specific tour using property expression
    List<TourLog> findByTour_Id(int tourId);

    @Query("SELECT tl FROM TourLog tl WHERE " +
            "LOWER(COALESCE(tl.comment, '')) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(COALESCE(tl.difficulty, '')) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<TourLog> findBySearchText(@Param("searchText") String searchText);
}