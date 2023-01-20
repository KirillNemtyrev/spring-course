package com.community.server.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(name="company")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min=2, max = 40)
    private String name;

    @NotBlank
    @Size(min=2, max = 40)
    private String owner;

    @Column(length=10485760)
    private String description;

    @NotNull
    private Long stock = 0L;

}
