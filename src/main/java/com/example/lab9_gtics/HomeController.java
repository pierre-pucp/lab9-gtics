package com.example.lab9_gtics;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
public class HomeController {

    private final StfPuzzleSvRepository repository;

    public HomeController(StfPuzzleSvRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<StfPuzzleSv> list = repository.findAll();
        StfPuzzleSv game;
        if (list.isEmpty()) {
            // État initial par défaut (ex: 3x3 -> 12345678X)
            game = new StfPuzzleSv("12345678X", "");
            repository.save(game);
        } else {
            game = list.get(0);
        }
        model.addAttribute("game", game);
        return "html";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".jpg")) {
            return "error: Format invalide, seul le format .jpg est autorisé.";
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
            return base64Image;
        } catch (IOException e) {
            return "error: Erreur lors du téléchargement.";
        }
    }

    @PostMapping("/start")
    public String startGame(@RequestParam("dimensions") String dimensions) {
        // Validation du format des dimensions (ex: 3x3, 2x3, etc.)
        if (!dimensions.matches("\\d+x\\d+")) {
            return "redirect:/?error=format";
        }
        
        String[] parts = dimensions.split("x");
        int rows = Integer.parseInt(parts[0]);
        int cols = Integer.parseInt(parts[1]);
        int totalCells = rows * cols;

        // Générer la structure ordonnée (ex: 123456...X)
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < totalCells; i++) {
            sb.append(i);
        }
        sb.append("X"); // Case vide

        // Mélanger automatiquement (au moins 8 mouvements valides en arrière)
        String scrambled = scrambleBoard(sb.toString(), rows, cols, 10);

        List<StfPuzzleSv> list = repository.findAll();
        StfPuzzleSv game = list.isEmpty() ? new StfPuzzleSv() : list.get(0);
        game.setStfGameBoardStructure(scrambled);
        repository.save(game);

        return "redirect:/";
    }

    @PostMapping("/move")
    @ResponseBody
    public String makeMove(@RequestParam("index") int index, @RequestParam("rows") int rows, @RequestParam("cols") int cols) {
        List<StfPuzzleSv> list = repository.findAll();
        if (list.isEmpty()) return "error";
        StfPuzzleSv game = list.get(0);
        String board = game.getStfGameBoardStructure();

        int emptyIndex = board.indexOf('X');
        int r1 = index / cols, c1 = index % cols;
        int r2 = emptyIndex / cols, c2 = emptyIndex % cols;

        // Valider si la case cliquée est contiguë à la case vide
        if (Math.abs(r1 - r2) + Math.abs(c1 - c2) == 1) {
            char[] chars = board.toCharArray();
            chars[emptyIndex] = chars[index];
            chars[index] = 'X';
            board = new String(chars);
            game.setStfGameBoardStructure(board);
            
            // Vérifier si le jeu est terminé (ordonné 123...X)
            StringBuilder winning = new StringBuilder();
            for (int i = 1; i < rows * cols; i++) winning.append(i);
            winning.append("X");

            if (board.equals(winning.toString())) {
                // Réinitialiser le jeu si terminé
                game.setStfGameBoardStructure(winning.toString());
                repository.save(game);
                return "finished";
            }

            repository.save(game);
            return board;
        }

        return "invalid";
    }

    private String scrambleBoard(String solved, int rows, int cols, int steps) {
        String current = solved;
        Random rand = new Random();
        for (int s = 0; s < steps; s++) {
            int emptyIndex = current.indexOf('X');
            int r = emptyIndex / cols, c = emptyIndex % cols;
            // Trouver les voisins valides
            java.util.List<Integer> neighbors = new java.util.ArrayList<>();
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