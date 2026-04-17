package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
