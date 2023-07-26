package com.challenge.backend.service;

import com.challenge.backend.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private WebClient userWebClient;

    public List<User> getUsers() {
        Mono<List<User>> monoUser = this.userWebClient.method(HttpMethod.GET)
                .uri("/users")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<User>>() {});
        return monoUser.block();
    }

    public User getUserById(Long id) {
        Mono<User> monoUser = this.userWebClient.method(HttpMethod.GET)
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(User.class);
        return monoUser.block();
    }
}
