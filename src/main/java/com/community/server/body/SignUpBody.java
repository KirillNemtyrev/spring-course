package com.community.server.body;

import lombok.Getter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class SignUpBody {

    @NotNull
    @Size(min=6, max=40)
    private String name;

    @NotNull
    @Size(min=6, max=40)
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min=6, max=40)
    private String password;
}
