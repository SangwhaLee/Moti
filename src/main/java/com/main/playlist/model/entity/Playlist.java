package com.main.playlist.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistId;
    @Column(unique = true,nullable = false)
    private String playlistName;

    private String playlistDesc;
    
    @OneToMany(mappedBy = "playlist")
    private List<PlaylistMission> playlistMissions = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="flowerId")
    private Flower flower;
}
