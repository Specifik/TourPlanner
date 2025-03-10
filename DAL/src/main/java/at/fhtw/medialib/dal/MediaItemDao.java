package at.fhtw.medialib.dal;

import at.fhtw.medialib.model.MediaItem;

import java.util.*;

public class MediaItemDao implements Dao<MediaItem> {
    private final List<MediaItem> mediaItems = new ArrayList<>();
    private int nextId = 1;

    public MediaItemDao() {
        // some test data
        mediaItems.add(new MediaItem(nextId++, "ACDC - Highway to Hell", 300.0, "I'm on the highway to hell\nOn the highway to hell\nHighway to hell\nI'm on the highway to hell"));
        mediaItems.add(new MediaItem(nextId++, "Rolling Stones - Satisfaction", 260.0, "I can't get no satisfaction\nI can't get no satisfaction\n'Cause I try, and I try, and I try, and I try\nI can't get no, I can't get no"));
        mediaItems.add(new MediaItem(nextId++, "Scorpions - Still loving you", 280.0, "Fight\nBabe, I'll fight\nTo win back your love again\nI will be there\nI will be there"));
    }

    @Override
    public Optional<MediaItem> get(int id) {
        return Optional.ofNullable(mediaItems.get(id));
    }

    @Override
    public List<MediaItem> getAll() {
        return mediaItems;
    }

    @Override
    public MediaItem create() {
        var tour = new MediaItem(nextId, "New Media " + nextId,0.0,"");
        mediaItems.add(tour);
        nextId++;
        return tour;
    }

    @Override
    public void update(MediaItem mediaItem, List<?> params) {
        System.out.println(params);
        mediaItem.setName(Objects.requireNonNull(params.get(1), "Name cannot be null").toString());
        mediaItem.setDuration(Double.parseDouble(params.get(2).toString()));
        mediaItem.setContent((params.get(3)==null)?"":params.get(3).toString());
    }

    @Override
    public void delete(MediaItem mediaItem) {
        mediaItems.remove(mediaItem);
    }
}
