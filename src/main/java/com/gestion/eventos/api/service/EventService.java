package com.gestion.eventos.api.service;

import com.gestion.eventos.api.DTO.EventRequestDto;
import com.gestion.eventos.api.DTO.EventResponseDto;
import com.gestion.eventos.api.domain.Category;
import com.gestion.eventos.api.domain.Event;
import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.exception.ResourceNotFoundException;
import com.gestion.eventos.api.mapper.EventMapper;
import com.gestion.eventos.api.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService{

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final SpeakerService speakerService;

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponseDto> findAll(String name, Pageable pageable) {

        Page<Event> eventsPage;

        if(name!=null && !name.trim().isEmpty()){

            eventsPage = eventRepository.findByNameContainingIgnoreCase(name, pageable);

        }else {
            eventsPage = eventRepository.findAll(pageable);
        }

        List<EventResponseDto> dtos = eventsPage.getContent().stream()
                .map(eventMapper::toResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, eventsPage.getTotalElements());

    }

    @Override
    public Event save(EventRequestDto requestDto) {

        Event savedEvent = eventMapper.toEntity(requestDto);

        Category category = categoryService.findById(requestDto.getCategoryId());
        savedEvent.setCategory(category);

        if(requestDto.getSpeakerIds()!=null && !requestDto.getSpeakerIds().isEmpty()){

            Set<Speaker> speakers = requestDto.getSpeakerIds().stream()
                    .map(speakerService::findById)
                    .collect(Collectors.toSet());

            speakers.forEach( savedEvent::addSpeaker);

        }

        return eventRepository.save(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Evento no encontrado con ese id: " + id));
    }

    @Override
    public Event update(Long id, EventRequestDto requestDto) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Evento no encontrado con ese id: " + id));
        eventMapper.updateEventFromDto(requestDto, existingEvent);

        if (!existingEvent.getCategory().getId().equals(requestDto.getCategoryId())) {
            Category category = categoryService.findById(requestDto.getCategoryId());
            existingEvent.setCategory(category);
        }

        Set<Speaker> updatedSpeakers;
        if(requestDto.getSpeakerIds()!=null && !requestDto.getSpeakerIds().isEmpty()){

            updatedSpeakers = requestDto.getSpeakerIds().stream()
                    .map(speakerService::findById)
                    .collect(Collectors.toSet());
            new HashSet<>(existingEvent.getSpeakers())
                    .forEach(currentSpeaker -> {
                        if(!updatedSpeakers.contains(currentSpeaker)){
                            existingEvent.removeSpeaker(currentSpeaker);
                        }
                    });
        } else {
            updatedSpeakers = new HashSet<>();
        }

        updatedSpeakers.forEach(newSpeaker -> {
            if(!existingEvent.getSpeakers().contains(newSpeaker)){
                existingEvent.addSpeaker(newSpeaker);
            }
        });

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Event eventToDelete = this.findById(id);
        eventRepository.delete(eventToDelete);
    }

    @Transactional
    public List<Event> getAllEventsAndTheirDetailsProblematic(){

        List<Event> events = eventRepository.findAll();

        events.forEach(event -> {

            event.getSpeakers().size();
            event.getSpeakers().stream().map(Speaker::getName).collect(Collectors.toSet());
            event.getCategory().getName();
            event.getAttendees().size();

        });

        return events;

    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEventsAndTheirDetailsOptimizedWithJoinFetch() {
        List<Event> events = eventRepository.findAllWithCategoryAndSpeakers();

        events.forEach(event -> {

            System.out.println("Event: " + event.getName());
            System.out.println("Event: " + event.getCategory().getName());
            System.out.println("Event: " + event.getSpeakers().stream().map(Speaker::getName).collect(Collectors.joining(", ")));

        });

        return events;
    }
}
