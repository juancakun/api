package com.gestion.eventos.api.repository;

import com.gestion.eventos.api.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT e FROM Event e JOIN FETCH e.category LEFT JOIN FETCH e.speakers")
    List<Event> findAllWithCategoryAndSpeakers();

    @Query("SELECT e FROM Event e JOIN FETCH e.category LEFT JOIN FETCH e.speakers WHERE e.id = :id")
    Optional<Event> findByIdWithCategoryAndSpeakers(Long id);

}
