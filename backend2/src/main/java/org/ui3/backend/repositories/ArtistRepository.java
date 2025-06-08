package org.ui3.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ui3.backend.models.Artist;
import org.ui3.backend.models.Countries;
import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByCountry(Countries country);
}