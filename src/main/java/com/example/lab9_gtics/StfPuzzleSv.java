package com.example.lab9_gtics;

import jakarta.persistence.*;

@Entity
@Table(name = "stf_puzzle_sv")
public class StfPuzzleSv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stf_game_board_structure", nullable = false)
    private String stfGameBoardStructure;

    @Lob
    @Column(name = "image", columnDefinition = "LONGTEXT", nullable = false)
    private String image; // Stocké en Base64

    public StfPuzzleSv() {}

    public StfPuzzleSv(String stfGameBoardStructure, String image) {
        this.stfGameBoardStructure = stfGameBoardStructure;
        this.image = image;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStfGameBoardStructure() { return stfGameBoardStructure; }
    public void setStfGameBoardStructure(String stfGameBoardStructure) { this.stfGameBoardStructure = stfGameBoardStructure; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}