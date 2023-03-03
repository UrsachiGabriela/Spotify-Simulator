package spotify.model.entities;


import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "artists")
@Table(name = "artists")
public class ArtistEntity {
    private @Id
    @Column(name = "UUID") String uuid;

    @NonNull
    @Column(unique = true)
    private String name;
    @Nullable
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "artists_songsAlbums",
            inverseJoinColumns = {@JoinColumn(name = "song_ID", referencedColumnName = "SID", nullable = false, updatable = false)},
            joinColumns = {@JoinColumn(name = "artist_ID", referencedColumnName = "UUID", nullable = false, updatable = false)}
    )
    @ElementCollection(targetClass = SongEntity.class)
    private Set<SongEntity> songs = new HashSet<SongEntity>();

    public ArtistEntity() {
        name = "";
    }

    public ArtistEntity(String name) {
        this.name = name;
    }

    public ArtistEntity(String uuid, @NonNull String name, boolean active) {
        this.uuid = uuid;
        this.name = name;
        this.active = active;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public Set<SongEntity> getSongs() {
        return songs;
    }

    public void setSongs(Set<SongEntity> songs) {
        this.songs = songs;
    }

//    @PreRemove
//    public void checkSongAssociationBeforeRemoval() {
//        if (!this.songs.isEmpty()) {
//            throw new ConflictException(ErrorMessages.PARENT_REMOVAL);
//        }
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistEntity artistEntity = (ArtistEntity) o;
        return active == artistEntity.active && Objects.equals(uuid, artistEntity.uuid) && Objects.equals(name, artistEntity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, active);
    }

    @Override
    public String toString() {
        return "Artists{" +
                "UUID=" + uuid +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
