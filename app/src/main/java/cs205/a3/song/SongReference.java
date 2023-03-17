package cs205.a3.song;

public class SongReference {
    private String name;
    private String id;
    private String data;
    private String audio;

    public SongReference(String name, String id, String data, String audio) {
        this.name = name;
        this.id = id;
        this.data = data;
        this.audio = audio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    @Override
    public String toString() {
        return "SongReference{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", data='" + data + '\'' +
                ", audio='" + audio + '\'' +
                '}';
    }
}
