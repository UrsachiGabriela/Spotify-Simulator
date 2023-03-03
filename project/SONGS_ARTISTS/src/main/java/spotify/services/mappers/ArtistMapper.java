package spotify.services.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import spotify.model.entities.ArtistEntity;
import spotify.view.requests.NewArtistRequest;
import spotify.view.responses.ArtistResponse;

import java.util.Set;
import java.util.UUID;

@Mapper(uses = {SongMapper.class})
public interface ArtistMapper {
    ArtistMapper INSTANCE = Mappers.getMapper(ArtistMapper.class);

    @Named("completeArtistMapper")
    @Mapping(target = "songs", qualifiedByName = "simpleSongDtoSet")
    @Mapping(target = "hasSongs", expression = "java(artistEntity.getSongs().isEmpty() ? false : true)")
    ArtistResponse toCompleteArtistDto(ArtistEntity artistEntity);

    @Named("artistWithSongs")
    default ArtistResponse toArtistWithSongsDto(ArtistEntity artistEntity) {
        return ArtistResponse.builder()
                .uuid(UUID.fromString(artistEntity.getUuid()))
                .hasSongs(!artistEntity.getSongs().isEmpty())
                .songs(SongMapper.INSTANCE.toSimpleSongDTOSet(artistEntity.getSongs()))
                .build();
    }

    @Named("artistWithoutSongs")
    default ArtistResponse toArtistWithoutSongsDto(ArtistEntity artistEntity) {
        return ArtistResponse.builder()
                .uuid(UUID.fromString(artistEntity.getUuid()))
                .name(artistEntity.getName())
                .active(artistEntity.isActive())
                .hasSongs(!artistEntity.getSongs().isEmpty())
                .build();
    }

    @Named("artistWithName")
    default ArtistResponse toArtistWithName(ArtistEntity artistEntity) {
        return ArtistResponse.builder()
                .uuid(UUID.fromString(artistEntity.getUuid()))
                .name(artistEntity.getName())
                .build();
    }

    ArtistEntity toArtistEntity(ArtistResponse artistResponse);

    default ArtistEntity toArtistEntity(NewArtistRequest newArtistRequest, UUID uuid){
        ArtistEntity artistEntity = toArtistEntity(newArtistRequest);
        artistEntity.setUuid(String.valueOf(uuid));
        return artistEntity;
    }

    ArtistEntity toArtistEntity(NewArtistRequest newArtistRequest);


    @IterableMapping(qualifiedByName = "completeArtistMapper")
    Set<ArtistResponse> toCompleteArtistDTOSet(Set<ArtistEntity> artistEntities);

    @IterableMapping(qualifiedByName = "artistWithoutSongs")
    Set<ArtistResponse> toArtistDTOWithoutSongsSet(Set<ArtistEntity> artistEntities);

    @IterableMapping(qualifiedByName = "artistWithSongs")
    Set<ArtistResponse> toArtistDTOWithSongsSet(Set<ArtistEntity> artistEntities);

    @IterableMapping(qualifiedByName = "artistWithName")
    Set<ArtistResponse> toArtistWithName(Set<ArtistEntity> artistEntities);
}
