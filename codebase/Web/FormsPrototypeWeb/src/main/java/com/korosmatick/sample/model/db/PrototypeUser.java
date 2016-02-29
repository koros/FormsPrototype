package com.korosmatick.sample.model.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class PrototypeUser implements Serializable, UserDetails {
	
	private static final long serialVersionUID = 6230550001733109788L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Email
	private String email;
	
	@Size(min = 10, max = 12)
	//@Digits(fraction = 0, integer = 12)
	private String phoneNumber;
	
	private String firstName;
	
	private String otherNames;
	
	private int enabled;
	
	private int isDeleted;
	
	private String encryptedPassword;
	
	private String salt;
	
	private String role;
	
	private int initialized;
	
	private String accessToken;
	
	private Long createdBy;
	
	private String onaAccountName;
	
	private String onaAccountPassword;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getOtherNames() {
		return otherNames;
	}

	public void setOtherNames(String otherNames) {
		this.otherNames = otherNames;
	}

	//Spring security method
	public boolean isEnabled() {
		return enabled == 1;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int isDeleted() {
		return isDeleted;
	}

	public void setDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public int getInitialized() {
		return initialized;
	}
	
	public void setInitialized(int initialized) {
		this.initialized = initialized;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	
	//==================================================================================================
	//  Spring security stuff
	//==================================================================================================
	
	public String getOnaAccountName() {
		return onaAccountName;
	}

	public void setOnaAccountName(String onaAccountName) {
		this.onaAccountName = onaAccountName;
	}

	public String getOnaAccountPassword() {
		return onaAccountPassword;
	}

	public void setOnaAccountPassword(String onaAccountPassword) {
		this.onaAccountPassword = onaAccountPassword;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		String[] s = generateRoleString();
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(s);
		return authorities;
	}

	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

	public String getPassword() {
		return encryptedPassword;
	}
	
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return enabled == 1;
	}

	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return enabled == 1;
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return enabled == 1;
	}
	
	//================================================================================
	private String[] generateRoleString() {
		// TODO Auto-generated method stub
		if (role.equalsIgnoreCase("Root")) {
			return new String[]{"ROLE_ROOT", "ROLE_SUPERVISOR", "ROLE_ADMIN", "ROLE_EDITOR"};
		}
		
		return null;
	}
}
