package it.devchallenge.sharepassword.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.devchallenge.sharepassword.model.SharedPasswordEntity;

public interface SharedPasswordRepository extends JpaRepository<SharedPasswordEntity, Long> {
}
