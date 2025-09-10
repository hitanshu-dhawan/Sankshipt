package com.hitanshudhawan.sankshipt.repositories;

import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {

    Long countByUrlShortCode(String shortCode);

    Page<Click> findByUrl(URL url, Pageable pageable);

}
