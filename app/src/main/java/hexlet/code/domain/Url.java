package hexlet.code.domain;

import javax.persistence.*;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import kong.unirest.JsonObjectMapper;

import java.time.Instant;

@Entity
public class Url extends Model {
    @Id
    private long id;

    private String name;

    @WhenCreated
    private Instant created_at;

    @Lob
    private String jsonLastUrlCheck;

    public Url(String name) {
        this.name = name;
    }

    /**
     * @return returns id of url
     */
    public long getId() {
        return id;
    }

    /**
     * @return returns String url-address.
     */
    public String getName() {
        return name;
    }

    /**
     * @return returns instant of creation.
     */
    public Instant getCreated_at() {
        return created_at;
    }

    /**
     * @return returns instant of last url check.
     */
    public UrlCheck getLastUrlCheck() {
        UrlCheck lastUrlCheck = new JsonObjectMapper().readValue(jsonLastUrlCheck, UrlCheck.class);
        return lastUrlCheck;
    }
}
