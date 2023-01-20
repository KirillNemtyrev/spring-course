package com.community.server.body;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class BuyBody {

    @NotNull
    private Long id;

    @NotNull
    private Long count;

}
