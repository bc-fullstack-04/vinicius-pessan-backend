package br.com.sysmap.bootcamp.web;


import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private  final AlbumService albumService;
    private  final UsersService usersService;

    @Operation(summary = "Get albums from artist")
    @GetMapping("/all")
    public ResponseEntity<List<AlbumModel>> getAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(search));
    }

    @Operation(summary = "Get my collection")
    @GetMapping("/my-collection")
    public ResponseEntity<List<Album>> getMyCollection(@RequestParam("id") Long id){
        Users users = usersService.findById(id);
        return ResponseEntity.ok(this.albumService.getMyCollection(users));
    }

    @Operation(summary = "Save album")
    @PostMapping("/sale")
    public ResponseEntity<Album> saveAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(this.albumService.saveAlbum(album));
    }

    @Operation(summary = "Remove  album")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeAlbum(@PathVariable Long id){
        albumService.removeAlbum(id);
        return ResponseEntity.noContent().build();
    }

}