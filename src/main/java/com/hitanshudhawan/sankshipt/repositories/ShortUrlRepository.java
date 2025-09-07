package com.hitanshudhawan.sankshipt.repositories;

import com.hitanshudhawan.sankshipt.models.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<URL, Long> {

    Optional<URL> findByShortCode(String shortCode);

}
