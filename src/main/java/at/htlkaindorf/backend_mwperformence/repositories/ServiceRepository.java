package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:36
 */
public interface ServiceRepository extends JpaRepository<Service, Long> {

    // Sortiert nach sort-Spalte aufsteigend
    List<Service> findAllByOrderBySortAsc();

    // Suche nach Titel (wie findAllStudentsByLastname im Beispiel)
    @Query("SELECT s FROM Service s WHERE s.title LIKE :title")
    List<Service> findAllByTitle(String title);
}

