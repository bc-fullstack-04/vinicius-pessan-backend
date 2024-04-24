package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {

    private final Queue queue;
    private final RabbitTemplate template;
    private final SpotifyApi spotifyApi;
    private final AlbumRepository albumRepository;
    private final UsersService usersService;

//    public void teste() {
//        log.info("Teste");
//        WalletDto walletDto = new WalletDto();
//        walletDto.setTeste("Teste: Vinicius");
//        this.template.convertAndSend(queue.getName(), walletDto);
//    }

    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        return this.spotifyApi.getAlbums(search);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Album saveAlbum(Album album) {
        Users users = getUser();
        Optional<Album> albumOptional = albumRepository.findByUsersAndIdSpotify(users, album.getIdSpotify());
        if(albumOptional.isPresent()){
            throw new RuntimeException("This album already save");
        }

        album.setUsers(getUser());
        Album albumSaved = albumRepository.save(album);

        WalletDto walletDto = new WalletDto(albumSaved.getUsers().getEmail(), albumSaved.getValue());
        this.template.convertAndSend(queue.getName(), walletDto);

        return albumSaved;

    }

    private Users getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return usersService.findByEmail(username);
    }

    public List<Album> getMyCollection(Users users){
        return albumRepository.findAllByUsers(users);
    }

    public void removeAlbum(Long id) {

        Album album = albumRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no album with the id provided"));
        albumRepository.delete(album);
    }

}
