package com.example.lab9_gtics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StfPuzzleSvRepository extends JpaRepository<StfPuzzleSv, Long> {
}