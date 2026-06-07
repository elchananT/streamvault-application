package com.streamsault.apigateway.graphql;

import com.streamsault.apigateway.dto.AuthPayload;
import com.streamvault.proto.user.LoginRequest;
import com.streamvault.proto.user.RegisterRequest;
import com.streamvault.proto.user.UserGrpcServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthResolver {
    private final UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub;

    @MutationMapping
    public AuthPayload register(
            @Argument String password,
            @Argument String username,
            @Argument String email) {
        var response = userStub.register(
                RegisterRequest.newBuilder()
                        .setUsername(username)
                        .setEmail(email)
                        .setPassword(password)
                        .build());
        return new AuthPayload(response.getToken(),  response.getUsername(), response.getEmail());
    }

    @MutationMapping
    public AuthPayload login(
            @Argument String email,
            @Argument String password
    ) {
        var response = userStub.login(
                LoginRequest.newBuilder()
                        .setEmail(email)
                        .setPassword(password)
                        .build());

        return new AuthPayload(response.getToken(), response.getUsername(), response.getEmail());
    }
}
