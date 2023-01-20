package hexlet.code.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import java.time.Instant;

@Entity
public class Url extends Model {
    @Id
    long id;

    String name;

    @WhenCreated
    private Instant created_at;

    public Url(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreated_at() {
        return created_at;
    }
}
