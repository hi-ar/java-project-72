package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class UrlCheck extends Model {
    @Id
    private long id;

    @WhenCreated
    private Instant createdAt;

    private int statusCode;

    private String title;

    private String h1;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private long urlId;

    public UrlCheck(int statusCode, String title, String h1, String description, long urlId) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.urlId = urlId;
    }

    /**
     * @return returns id of url
     */
    public long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getH1() {
        return h1;
    }

    public String getDescription() {
        return description;
    }

    public long getUrlId() {
        return urlId;
    }
}
