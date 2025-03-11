package com.NBE3_4_2_Team4.global.api.iamport.v1.authentication;

import java.util.Optional;

public interface IamportAuthenticationService {

    Optional<String> generateAccessToken(Long memberId);

    Optional<String> getAccessToken(Long memberId);
}