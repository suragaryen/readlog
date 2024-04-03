package kr.co.readLog.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kr.co.readLog.Repository.UserRepository;
import kr.co.readLog.dto.JoinDTO;
import kr.co.readLog.entity.MemberEntity;




@Service
public class JoinService {
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	
	public void joinnProcess(JoinDTO joinDTO) {
		
		 String username = joinDTO.getUsername();
	     String password = joinDTO.getPassword();
		
		Boolean isExist = userRepository.existsByUsername(username);
		
		if(isExist) {
			
			return;
			
		}
	
		MemberEntity data = new MemberEntity();
		
	
	     	data.setUsername(username);
	        data.setPassword(bCryptPasswordEncoder.encode(password));
	        data.setRole("ROLE_ADMIN");

		
		userRepository.save(data);
		
	}
	
	
}
