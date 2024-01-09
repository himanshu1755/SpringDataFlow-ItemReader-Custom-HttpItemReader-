package com.spring.data_flow_readers.http_reader.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public class RefreshTokenResponse {
    private String refresh_token;
    private String token_type;
    private String access_token;
    private String expires_in;
}
