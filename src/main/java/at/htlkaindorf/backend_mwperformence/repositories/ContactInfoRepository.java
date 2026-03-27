package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:35
 */
public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {

    @Query("SELECT c FROM ContactInfo c ORDER BY c.sort ASC")
    List<ContactInfo> findAllByOrderBySortAsc();
}
