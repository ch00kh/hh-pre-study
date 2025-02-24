package site.ch00kh.domain.account.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.ch00kh.domain.account.dao.account.Account;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class AccountDetails implements UserDetails {

    private final Account account;

    // ROLE 값을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(account::getRole);

        return collection;
    }

    // username 반환
    @Override
    public String getUsername() {
        return account.getUsername();
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return account.getPassword();
    }

    // 계정 만료 여부 (기본값 : true)
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // 계정 잠김 여부 (기본값 : true)
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 자격 증명 만료 여부 (기본값 : true)
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 사용 여부 (기본값 : true)
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
