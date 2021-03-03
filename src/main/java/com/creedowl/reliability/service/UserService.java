package com.creedowl.reliability.service;

import com.creedowl.reliability.dto.UserDataDTO;
import com.creedowl.reliability.entity.User;
import com.creedowl.reliability.exception.CustomException;
import com.creedowl.reliability.repository.UserRepository;
import com.creedowl.reliability.security.JWTUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserService(ModelMapper modelMapper, UserRepository userRepository, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public User register(UserDataDTO registerUser) {
        if (null != this.userRepository.findByUsername(registerUser.getUsername())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "User Existed");
        }
        var user = this.modelMapper.map(registerUser, User.class);
        user.setPassword(this.jwtUtil.encodePassword(user.getPassword()));
        user.setIsAdmin(false);
        this.userRepository.save(user);
        return user;
    }

    public String login(UserDataDTO loginUser) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(),
                loginUser.getPassword()));
        this.logger.info("user login: {}", loginUser.getUsername());
        return this.jwtUtil.generateJWT(this.modelMapper.map(loginUser, User.class));
    }

    public User getMe() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return this.userRepository.findByUsername(auth.getName());
    }
}
