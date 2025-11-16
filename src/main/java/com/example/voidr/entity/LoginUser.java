package com.example.voidr.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 */
@Data
@AllArgsConstructor
public class LoginUser implements UserDetails {

	private final Account account; // ← Userエンティティを内包


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
    	return List.of(new SimpleGrantedAuthority(this.account.getAuthority().getName()));
    }

	@Override
	public String getPassword() {
		return this.account.getPassword();
	}

	@Override
	public String getUsername() {
		return this.account.getUsername();
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

	// 退会フラグと連動
	@Override
	public boolean isEnabled() {
		return this.account.isEnabled();
	}

	public String getDisplayName()
	{
		return this.account.getDisplayName();

	}
	
	public Long getId()
	{
		return this.account.getId();
	}
}
