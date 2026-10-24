package com.gestion.eventos.api.service;

import com.gestion.eventos.api.DTO.SpeakerRequestDto;
import com.gestion.eventos.api.domain.Speaker;

import java.util.List;

public interface SpeakerService {

    Speaker save(SpeakerRequestDto speakerRequestDto);

    Speaker findById(Long id);

    List<Speaker> findAll();

    Speaker update(Long id, SpeakerRequestDto speakerRequestDto);

    void deleteById(Long id);

}
