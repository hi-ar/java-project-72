package hexlet.code.domain;

import javax.persistence.*;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import java.time.Instant;
import java.util.List;

@Entity
public class Url extends Model {
    @Id
    @OneToMany(mappedBy="urlId")
    private long id;

    private String name;

    @WhenCreated
    private Instant created_at;

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
}
