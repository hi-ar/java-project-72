package hexlet.code.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    List<Integer> checks = new ArrayList<>();

    public void addCheck(int newCheck) {
        checks.add(newCheck);
    }

    public List<Integer> getChecksIds() {
        return checks;
    }
}
