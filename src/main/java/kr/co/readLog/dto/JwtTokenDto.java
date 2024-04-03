package kr.co.readLog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtTokenDto {
	
	private String accessToken;
	private String refreshToken;
	

}
