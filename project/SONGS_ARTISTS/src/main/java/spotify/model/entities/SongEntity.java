package spotify.model.entities;


import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity(name = "songsAlbums")
@Table(name = "songsAlbums")
public class SongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SID")
    private Integer id;

    @NonNull
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;

    @NonNull
    private int year;

    @NonNull
    @Enumerated(EnumType.STRING)
    private MusicType type;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "parent")
    private SongEntity parent;


    //@OneToMany(mappedBy="parent", fetch=FetchType.EAGER)
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent")
    private Set<SongEntity> songEntities;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "songsAlbums")
//    @ElementCollection(targetClass = ArtistEntity.class)
//    private Set<ArtistEntity> artists = new HashSet<ArtistEntity>();


    public SongEntity() {
    }


    public SongEntity(@NonNull String name, @NonNull MusicGenre genre, int year, @NonNull MusicType type) {
        this.name = name;
        this.genre = genre;
        this.year = year;
        this.type = type;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(@NonNull MusicGenre genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @NonNull
    public MusicType getType() {
        return type;
    }

    public void setType(@NonNull MusicType type) {
        this.type = type;
    }

    @Nullable
    public SongEntity getParent() {
        return parent;
    }

    public void setParent(@Nullable SongEntity parent) {
        this.parent = parent;
    }


//    public Set<ArtistEntity> getArtists() {
//        return artists;
//    }
//
//    public void setArtists(Set<ArtistEntity> artists) {
//        this.artists = artists;
//    }

    public Set<SongEntity> getSongEntities() {
        return songEntities;
    }

    public void setSongEntities(Set<SongEntity> songEntities) {
        this.songEntities = songEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongEntity that = (SongEntity) o;
        return id == that.id && year == that.year && parent == that.parent && name.equals(that.name) && genre == that.genre && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, genre, year, type, parent);
    }

    @Override
    public String toString() {
        return "SongsAlbums{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genre=" + genre +
                ", year=" + year +
                ", type=" + type +
                ", parent=" + parent +
                '}';
    }
}
