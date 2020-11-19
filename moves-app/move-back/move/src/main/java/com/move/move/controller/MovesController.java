package com.move.move.controller;

import com.move.move.exception.MovesException;
import com.move.move.service.MovesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.http.MediaType.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class MovesController {

    @Autowired
    MovesService movesService;

    @PostMapping(value = "/moves", produces = TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] calculateMoves(@RequestParam("file") MultipartFile file,
                          @RequestParam("id") long id) throws IOException {
        try {
            String routeResultFile = movesService.calculateMoves(file, id);
            ByteArrayResource res = new ByteArrayResource(Files.readAllBytes(Path.of(routeResultFile)));
            return res.getByteArray();
        } catch(MovesException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }
}
