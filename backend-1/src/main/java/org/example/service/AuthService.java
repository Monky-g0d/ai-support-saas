package org.example.service;

import org.example.dto.AuthRequest;
import org.example.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse login(AuthRequest request);
}
