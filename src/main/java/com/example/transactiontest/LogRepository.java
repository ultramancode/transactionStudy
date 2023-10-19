package com.example.transactiontest;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LogRepository {

    private final EntityManager em;

    public static String TERM = "로그 예외";
    @Transactional
    public void save(Log logMessage){
        log.info("log 저장");
        em.persist(logMessage);

        if(logMessage.getMessage().contains(TERM)) {
            log.info("log 저장시 예외 발생");
            throw new RuntimeException("예외 발생");
        }
    }
}
