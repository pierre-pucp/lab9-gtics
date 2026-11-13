package com.example.lab9_gtics;

import jakarta.persistence.*;
import java.io.File;

@Entity 
@Table (name = "stf_puzzle_sv")
public class StfPuzzleSv {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpuzzle")
    private Integer idpuzzle;

    @Column(name = "stf_game_board_structure")
    private Integer stfGameBoardStructure;

    @Column(name = "image")
    private File image;

}
