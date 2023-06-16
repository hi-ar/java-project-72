package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
public class Url extends Model {
    @Id
    @GeneratedValue
    private long id;
    private String name;

    @WhenCreated
    private Instant createdAt;

    @OneToMany(cascade = ALL)
    private List<UrlCheck> urlCheckList;

    public Url(String name) {
        this.name = name;
    }
    public Url() { //for correct init
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
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * @return checks list
     */
    public List<UrlCheck> getUrlCheckList() {
        return urlCheckList;
    }
}
