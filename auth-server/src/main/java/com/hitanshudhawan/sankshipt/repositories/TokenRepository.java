package com.hitanshudhawan.sankshipt.repositories;

import com.hitanshudhawan.sankshipt.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByValueAndIsExpired(String value, boolean isExpired);

    Optional<Token> findByValueAndIsExpiredAndExpiryDateAfter(String value, boolean isExpired, Date date);

}
