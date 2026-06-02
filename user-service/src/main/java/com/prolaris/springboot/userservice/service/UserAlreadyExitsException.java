package com.prolaris.springboot.userservice.service;

public class UserAlreadyExitsException extends RuntimeException {
  public UserAlreadyExitsException(String message) {
    super(message);
  }
}
