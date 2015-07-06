package main.Model;

/**
 * Created by User on 30.06.2015.
 */

public class News {
    private long id;
    private String title;
    private String img_url;
    private String content;

    public News(long id, String title, String img_url, String content) {
        this.id = id;
        this.title = title;
        this.img_url = img_url;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


