package edu.uclm.esi.fakeaccountsbe.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity 
@Table(name = "usuario") 
public class User {
	
	@Id @Column(length = 60)
	private String email;
	
	private String pwd;
	
	@JsonIgnore @Column(length = 36)
	private String token;
	
	@JsonIgnore @Transient
	private long creationTime;
	
	@Transient
	private String ip;
	
	private String cookie;
	private boolean vip;
	private LocalDateTime vipFecha;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = org.apache.commons.codec.digest.DigestUtils.sha512Hex(pwd);
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;		
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setCookie(String fakeUserId) {
		this.cookie = fakeUserId;
	}
	
	public String getCookie() {
		return cookie;
	}
	
	public boolean getVip() {
		return vip;
	}
	
	public void setVip(boolean vip) {
		this.vip = vip;
	}

    public LocalDateTime getVipFecha() {
        return vipFecha;
    }

    public void setVipFecha(LocalDateTime vipFecha) {
        this.vipFecha = vipFecha;
    }
}
