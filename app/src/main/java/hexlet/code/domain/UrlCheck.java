package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.Instant;

@Entity
public class UrlCheck extends Model {
    @Id
    long id;

    @WhenCreated
    Instant createdAt;

    int statusCode;

    String title;

    String h1;

    @Lob
    String description;

    String url;

    public UrlCheck(int statusCode, String title, String h1, String description, String url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }

    /**
     * @return returns id of url
     */
    public long getId() {
        return id;
    }
//
//    /**
//     * @return returns String url-address.
//     */
//    public String getUrl() {
//        return url;
//    }
//
//    /**
//     * @return returns instant of creation.
//     */
//    public Instant getCreatedAt() {
//        return createdAt;
//    }
}
