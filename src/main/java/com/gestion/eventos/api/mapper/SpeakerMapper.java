package com.gestion.eventos.api.mapper;

import com.gestion.eventos.api.DTO.SpeakerRequestDto;
import com.gestion.eventos.api.DTO.SpeakerResponseDto;
import com.gestion.eventos.api.domain.Speaker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpeakerMapper {

    SpeakerResponseDto toDto(Speaker speaker);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Speaker toEntity(SpeakerRequestDto speakerDto);

    List<SpeakerResponseDto> toResponseDtoList(List<Speaker> speakers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    void updateSpeakerFromDto(SpeakerRequestDto requestDto, @MappingTarget Speaker speaker);

}
