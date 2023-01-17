package hexlet.code.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.annotation.WhenCreated;

import java.time.Instant;

@Entity
public class Url {
    @Id
    long id;

    String name;

    @WhenCreated
    private Instant createdAt;
}
