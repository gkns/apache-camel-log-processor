package com.example.flat_file_http_api.repositories;

import com.example.flat_file_http_api.models.Session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionDataRepository extends JpaRepository<Session, String> {}
