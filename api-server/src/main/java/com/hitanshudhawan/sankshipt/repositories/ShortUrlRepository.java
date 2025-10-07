package com.hitanshudhawan.sankshipt.repositories;

import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<URL, Long> {

    List<URL> findAllByUser(User user);

    Optional<URL> findByShortCode(String shortCode);

    Optional<URL> findByShortCodeAndUser(String shortCode, User user);

}
