package com.week7.taskmanager;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class EntityLifecycleRunner implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        System.out.println("========== ENTITY LIFECYCLE DEMO ==========");

        User user = new User();
        user.setUsername("stephaniemhay_deleon");
        user.setEmail("stephaniemhay.deleon@example.com");
        user.setCreatedAt(LocalDateTime.now());
        System.out.println("[1] TRANSIENT -> tracked by JPA? " + entityManager.contains(user)
                + " | id = " + user.getId());

        entityManager.persist(user);
        entityManager.flush();
        System.out.println("[2] MANAGED   -> tracked by JPA? " + entityManager.contains(user)
                + " | id = " + user.getId());

        entityManager.detach(user);
        System.out.println("[3] DETACHED  -> tracked by JPA? " + entityManager.contains(user));

        user.setEmail("stephanie.deleon@example.com");
        System.out.println("[4] Java object email is now: " + user.getEmail());

        System.out.println("[5] merge() NOT called -> database should still have: stephaniemhay.deleon@example.com");
        System.out.println("===========================================");
    }
}