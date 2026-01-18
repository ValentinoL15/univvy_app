package com.unnivy.unnivy_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;
    private String description;
    @Column(nullable = false)
    private Integer value;
    @ManyToOne
    @JoinColumn(name = "from_client_id")
    private Client from;
    @ManyToOne
    @JoinColumn(name = "to_supplier_id")
    private Supplier to;

}
