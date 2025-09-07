package com.hitanshudhawan.sankshipt.repositories;

import com.hitanshudhawan.sankshipt.models.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {

    Long countByUrlShortCode(String shortCode);

}
