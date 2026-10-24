package com.gestion.eventos.api.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private String location;


    @ManyToMany(fetch = FetchType.LAZY,  cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "event_speaker",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "speakers_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Speaker> speakers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(mappedBy = "attendedEvents", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> attendees = new HashSet<>();

    public void addSpeaker(Speaker speaker) {
        this.speakers.add(speaker);
        speaker.getEvents().add(this);
    }

    public void removeSpeaker(Speaker speaker) {
        this.speakers.remove(speaker);
        speaker.getEvents().remove(this);
    }

}
