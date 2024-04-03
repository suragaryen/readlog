package kr.co.readLog.Repository;



import kr.co.readLog.entity.MemberEntity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails  implements UserDetails {

    private final MemberEntity userEntity;

    public CustomUserDetails(MemberEntity userEntity) {

        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 사용자의 권한(role)을 가져옵니다.
        String role = userEntity.getRole();

        // 권한(role)이 null이 아니고, 빈 문자열이 아닌 경우에만 authorities에 추가합니다.
        if (role != null && !role.isEmpty()) {
            // 권한(role)을 SimpleGrantedAuthority 객체로 변환하여 authorities에 추가합니다.
            authorities.add(new SimpleGrantedAuthority(role));
        } else {
            // 사용자의 권한(role)이 올바르게 설정되지 않은 경우, 기본 권한으로 설정할 수 있습니다.
            // 예를 들어, "ROLE_USER" 권한을 기본으로 설정합니다.
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // authorities를 반환합니다.
        return authorities;
        
    }
       

        @Override
        public String getPassword() {

            return userEntity.getPassword();
        }

        @Override
        public String getUsername() {

            return userEntity.getUsername();
        }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }




	
}
