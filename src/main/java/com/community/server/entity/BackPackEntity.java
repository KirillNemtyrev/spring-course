package com.community.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="backpack")
public class BackPackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long companyId;
    private Long count = 0L;

}
