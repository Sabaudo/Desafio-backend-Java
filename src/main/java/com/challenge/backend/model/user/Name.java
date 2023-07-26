package com.challenge.backend.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Name {
    private String firstname;
    private String lastname;

    public String getName(){
        return this.firstname + " " + this.lastname;
    }
}
