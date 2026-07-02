package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.licensePlate = :licensePlate ORDER BY v.id ASC")
    List<Vehicle> findAllByLicensePlateOrderByIdAsc(String licensePlate);

    default Optional<Vehicle> findByLicensePlate(String licensePlate) {
        List<Vehicle> matches = findAllByLicensePlateOrderByIdAsc(licensePlate);
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
    }

    List<Vehicle> findAllByUserId(Long userId);
}