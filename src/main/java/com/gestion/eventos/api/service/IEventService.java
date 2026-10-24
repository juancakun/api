package com.gestion.eventos.api.service;

import com.gestion.eventos.api.DTO.EventRequestDto;
import com.gestion.eventos.api.DTO.EventResponseDto;
import com.gestion.eventos.api.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEventService{

    Page<EventResponseDto> findAll(String name, Pageable pageable);

    Event save(EventRequestDto event);

    Event findById(Long id);

    Event update(Long id, EventRequestDto eventRequestDto);

    void deleteById(Long id);

    List<Event> getAllEventsAndTheirDetailsProblematic();

    List<Event> getAllEventsAndTheirDetailsOptimizedWithJoinFetch();

}
