package com.community.server.body;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class SignInBody {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
