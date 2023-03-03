package spotify.services.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import spotify.model.entities.SongEntity;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;
import spotify.utils.errorhandling.customexceptions.UnprocessableContentException;
import spotify.view.requests.NewSongRequest;
import spotify.view.requests.SongForUpdateRequest;
import spotify.view.responses.SongResponse;

import java.util.Set;

@Mapper
public interface SongMapper {
    SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);

    @Named("completeSong")
    @Mapping(source = "songEntity.parent.id", target = "parentId")
    @Mapping(source = "songEntity.songEntities", target = "songs", qualifiedByName = "simpleSongDtoSet")
    SongResponse toCompleteSongDto(SongEntity songEntity);

    @Named("simpleSong")
    default SongResponse toSimpleSongDto(SongEntity songEntity) {
        return SongResponse.builder()
                .id(songEntity.getId())
                .name(songEntity.getName())
                //.genre(songEntity.getGenre())
                .build();
    }

    default SongEntity toSongEntity(SongForUpdateRequest songForUpdateRequest){
        SongEntity songEntity = new SongEntity();

        songEntity.setName(songForUpdateRequest.getName());
        try{
            songEntity.setGenre(MusicGenre.valueOf(songForUpdateRequest.getGenre()));
        }
        catch (Exception e){
            throw new UnprocessableContentException("Invalid genre");
        }

        if (songForUpdateRequest.getYear() != null) {
            songEntity.setYear(songForUpdateRequest.getYear());
        }

        return songEntity;
    }

    @Mapping(source = "newSongRequest.parentId", target = "parent.id")
    default SongEntity toSongEntity(NewSongRequest newSongRequest, SongEntity album) {
        SongEntity songEntity = new SongEntity();

        songEntity.setName(newSongRequest.getName());
        try{
            songEntity.setGenre(MusicGenre.valueOf(newSongRequest.getGenre()));
        }
        catch (Exception e){
            throw new UnprocessableContentException("Invalid genre");
        }

        if (newSongRequest.getYear() != null) {
            songEntity.setYear(newSongRequest.getYear());
        }

        try{
            songEntity.setType(MusicType.valueOf(newSongRequest.getType()));
        }
        catch (Exception e){
            throw new UnprocessableContentException("Invalid type");

        }
        songEntity.setParent(album);

        return songEntity;
    }

    @IterableMapping(qualifiedByName = "completeSong")
    Set<SongResponse> toCompleteSongDTOSet(Set<SongEntity> songEntities);

    @Named("simpleSongDtoSet")
    @IterableMapping(qualifiedByName = "simpleSong")
    Set<SongResponse> toSimpleSongDTOSet(Set<SongEntity> songEntities);
}
