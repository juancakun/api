package com.gestion.eventos.api.mapper;

import com.gestion.eventos.api.DTO.EventRequestDto;
import com.gestion.eventos.api.DTO.EventResponseDto;
import com.gestion.eventos.api.DTO.EventSummaryDto;
import com.gestion.eventos.api.domain.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "speakers", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    Event toEntity(EventRequestDto eventRequestDto);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "speakers", source = "speakers")
    EventResponseDto toResponseDto(Event event);
    List<EventResponseDto> toEventResponseDtoList(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "speakers", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    void updateEventFromDto(EventRequestDto dto, @MappingTarget Event event);

    EventSummaryDto toSummaryDto(Event event);
    List<EventSummaryDto> toEventSummaryDtoList(List<Event> events);

}
