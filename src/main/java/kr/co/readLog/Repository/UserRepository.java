package kr.co.readLog.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.readLog.entity.MemberEntity;
import java.util.List;


public interface UserRepository extends JpaRepository<MemberEntity, Integer>{
	
	Boolean existsByUsername(String username);
	
	//nickname를 받아 DB테이블에서 회원을 조회하는 메소드 작성  
	
	MemberEntity findByUsername(String username);
	
	
	//MemberEntity findByUsername(String id);
	//JpaRepository의 findById 메서드는 Optional을 반환하도록 설계되어 있음. 
	//이는 메소드가 호출된 곳에서 값의 존재 여부를 더욱 명시적으로 다룰 수 있도록 하기 위함이다. 

}
