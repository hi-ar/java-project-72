package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
    @JoinColumn //(referencedColumnName = "id")
    private Url url;

    public UrlCheck(int statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }

    /**
     * @return id of url
     */
    public long getId() {
        return id;
    }

    /**
     * @return date and time of creation
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * @return HTTP status e.g. 200 or 404
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return title of html
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return first of the h1 header
     */
    public String getH1() {
        return h1;
    }

    /**
     * @return description, see parsing
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return url path (String)
     */
    public Url getUrl() {
        return url;
    }
}
