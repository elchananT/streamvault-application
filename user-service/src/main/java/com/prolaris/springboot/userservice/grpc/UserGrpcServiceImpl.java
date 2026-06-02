package com.prolaris.springboot.userservice.grpc;

import com.prolaris.springboot.userservice.domain.User;
import com.prolaris.springboot.userservice.domain.UserRepository;
import com.prolaris.springboot.userservice.security.JwtService;
import com.streamvault.proto.user.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserGrpcServiceImpl extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> observer) {
        try {
            String email = jwtService.extractEmail(request.getToken());
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null && jwtService.isTokenValid(request.getToken(),  user)) {
                observer.onNext(ValidateTokenResponse.newBuilder()
                        .setValid(true)
                        .setUserId(String.valueOf(user.getId()))
                        .setEmail(user.getEmail())
                        .setUsername(user.getDisplayUsername())
                        .build());
            } else {
                observer.onNext(ValidateTokenResponse.newBuilder().setValid(false).build());
            }
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            observer.onNext(ValidateTokenResponse.newBuilder().setValid(false).build());
        }

        observer.onCompleted();
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse>  observer) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                observer.onNext(UserResponse.newBuilder()
                        .setUserId(String.valueOf(user.getId()))
                        .setUsername(user.getDisplayUsername())
                        .setEmail(user.getEmail())
                        .setFound(true)
                        .build());
            } else {
                observer.onNext(UserResponse.newBuilder().setFound(false).build());
            }
        } catch (Exception e) {
            observer.onNext(UserResponse.newBuilder().setFound(false).build());
        }

        observer.onCompleted();
    }
}
