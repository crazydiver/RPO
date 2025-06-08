package org.ui3.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "paintings")
public class Painting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @ManyToOne
    @JoinColumn(name = "artistid", referencedColumnName = "id")
    @JsonIgnoreProperties({"paintings"})  // ← игнорировать поле "paintings" у artist
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "museumid", referencedColumnName = "id")
    @JsonIgnoreProperties({"paintings", "users"})  // ← то же самое
    private Museum museum;

    @Column(name = "year")
    private int year;
}