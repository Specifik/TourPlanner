package at.fhtw.tourplanner.dal.repository;

import at.fhtw.tourplanner.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {

    // Custom query methods for search functionality
    @Query("SELECT DISTINCT t FROM Tour t WHERE " +
            "LOWER(COALESCE(t.name, '')) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(COALESCE(t.from, '')) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(COALESCE(t.to, '')) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(COALESCE(t.transportType, '')) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(COALESCE(t.description, '')) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Tour> findBySearchText(@Param("searchText") String searchText);

    // Find tours that have logs matching search criteria
    @Query("SELECT DISTINCT t FROM Tour t JOIN t.tourLogs tl WHERE " +
            "LOWER(COALESCE(tl.comment, '')) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(COALESCE(tl.difficulty, '')) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Tour> findByTourLogSearchText(@Param("searchText") String searchText);

    // fetch all tours with their logs eagerly for export/serialization
    @Query("SELECT DISTINCT t FROM Tour t LEFT JOIN FETCH t.tourLogs")
    List<Tour> findAllWithLogs();
}