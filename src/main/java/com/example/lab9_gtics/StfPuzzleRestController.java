package com.example.lab9_gtics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/puzzle")
public class StfPuzzleRestController {

    private final StfPuzzleSvRepository repository;

    public StfPuzzleRestController(StfPuzzleSvRepository repository) {
        this.repository = repository;
    }

    // API pour obtenir l'état actuel du jeu
    getMapping("/state")
    public ResponseEntity<StfPuzzleSv> getState() {
        List<StfPuzzleSv> list = repository.findAll();
        if (list.isEmpty()) {
            StfPuzzleSv initial = new StfPuzzleSv("12345678X", "");
            return ResponseEntity.ok(repository.save(initial));
        }
        return ResponseEntity.ok(list.get(0));
    }

    // API pour uploader l'image .jpg via Web Service
    @PostMapping("/upload")
    public ResponseEntity> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".jpg")) {
            response.put("status", "error");
            response.put("message", "Formato inválido, solo se permite formato .jpg");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(file.getBytes());
            List<StfPuzzleSv> list = repository.findAll();
            StfPuzzleSv game = list.isEmpty() ? new StfPuzzleSv() : list.get(0);
            game.setImage(base64Image);
            if (game.getStfGameBoardStructure() == null || game.getStfGameBoardStructure().isEmpty()) {
                game.setStfGameBoardStructure("12345678X");
            }
            repository.save(game);
            
            response.put("status", "success");
            response.put("image", base64Image);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Error al procesar la imagen");
            return ResponseEntity.status(500).body(response);
        }
    }

    // API pour démarrer / mélanger le jeu
    @PostMapping("/start")
    public ResponseEntity<StfPuzzleSv> startGame(@RequestParam("dimensions") String dimensions) {
        if (!dimensions.matches("\\d+x\\d+")) {
            return ResponseEntity.badRequest().build();
        }
        
        String[] parts = dimensions.split("x");
        int rows = Integer.parseInt(parts[0]);
        int cols = Integer.parseInt(parts[1]);
        int totalCells = rows * cols;

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < totalCells; i++) {
            sb.append(i);
        }
        sb.append("X");

        String scrambled = scrambleBoard(sb.toString(), rows, cols, 10);

        List<StfPuzzleSv> list = repository.findAll();
        StfPuzzleSv game = list.isEmpty() ? new StfPuzzleSv() : list.get(0);
        game.setStfGameBoardStructure(scrambled);
        repository.save(game);

        return ResponseEntity.ok(game);
    }

    // API pour effectuer un mouvement et valider
    @PostMapping("/move")
    public ResponseEntity> makeMove(@RequestParam("index") int index, 
                                                        @RequestParam("rows") int rows, 
                                                        @RequestParam("cols") int cols) {
        List<StfPuzzleSv> list = repository.findAll();
        Map<String, Object> response = new HashMap<>();
        if (list.isEmpty()) {
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }

        StfPuzzleSv game = list.get(0);
        String board = game.getStfGameBoardStructure();

        int emptyIndex = board.indexOf('X');
        int r1 = index / cols, c1 = index % cols;
        int r2 = emptyIndex / cols, c2 = emptyIndex % cols;

        // Validation des cases contiguës
        if (Math.abs(r1 - r2) + Math.abs(c1 - c2) == 1) {
            char[] chars = board.toCharArray();
            chars[emptyIndex] = chars[index];
            chars[index] = 'X';
            board = new String(chars);
            game.setStfGameBoardStructure(board);

            StringBuilder winning = new StringBuilder();
            for (int i = 1; i < rows * cols; i++) winning.append(i);
            winning.append("X");

            if (board.equals(winning.toString())) {
                game.setStfGameBoardStructure(winning.toString());
                repository.save(game);
                response.put("status", "finished");
                response.put("board", board);
                return ResponseEntity.ok(response);
            }

            repository.save(game);
            response.put("status", "success");
            response.put("board", board);
            return ResponseEntity.ok(response);
        }

        response.put("status", "invalid");
        return ResponseEntity.ok(response);
    }

    private String scrambleBoard(String solved, int rows, int cols, int steps) {
        String current = solved;
        Random rand = new Random();
        for (int s = 0; s < steps; s++) {
            int emptyIndex = current.indexOf('X');
            int r = emptyIndex / cols, c = emptyIndex % cols;
            List<Integer> neighbors = new ArrayList<>();
            if (r > 0) neighbors.add((r - 1) * cols + c);
            if (r < rows - 1) neighbors.add((r + 1) * cols + c);
            if (c > 0) neighbors.add(r * cols + (c - 1));
            if (c < cols - 1) neighbors.add(r * cols + (c + 1));

            int randomNeighbor = neighbors.get(rand.nextInt(neighbors.size()));
            char[] chars = current.toCharArray();
            chars[emptyIndex] = chars[randomNeighbor];
            chars[randomNeighbor] = 'X';
            current = new String(chars);
        }
        return current;
    }
}