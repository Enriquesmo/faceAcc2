package edu.uclm.esi.fakeaccountsbe.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.User;

@Service
public class UserService {
		
	@Autowired //incluimos esto para los DAO siempre
	private UserDao userDao;
	
	//private Map<String, User> users = new ConcurrentHashMap<>();
	//private Map<String, List<User>> usersByIp = new ConcurrentHashMap<>();

	public void registrar(String ip, User user) {
		if(this.userDao.findById(user.getEmail()).isPresent())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ya existe un usuario con ese correo electrónico");

		//if (this.users.get(user.getEmail())!=null)
		//	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ya existe un usuario con ese correo electrónico");
		
		//List<User> users = this.usersByIp.get(ip);
		//if (users==null) 
		//	users = new ArrayList<>();
		
		//if (users.size()>10)
		//	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes crear más de 10 usuarios");
		
		//user.setIp(ip);
		//users.add(user);
		
		//this.usersByIp.put(ip, users);
		//this.users.put(user.getEmail(), user);
		
		user.setIp(ip);
		user.setCreationTime(System.currentTimeMillis());
		this.userDao.save(user);
	}

	public void login(User tryingUser) {
		this.find(tryingUser.getEmail(), tryingUser.getPwd());
	}

	public void clearAll() {
		//this.usersByIp.clear();
		//this.users.clear();
		this.userDao.deleteAll();
	}

	public Iterable<User> getAllUsers() {
		//return this.users.values();
		return this.userDao.findAll();
	}

	public User find(String email, String pwd) {
		//User user = this.users.get(email);
		//if (user==null || !user.getPwd().equals(pwd))
		//	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales incorrectas");
		//return user;
		Optional<User> optUser=this.userDao.findById(email);
		
		if (!optUser.isPresent())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales incorrectas1");
		
		User user=optUser.get();
		if (!user.getPwd().equals(pwd))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales incorrectas2 " + user.getPwd() + " " + pwd);
		return user;
	}

	public void delete(String email) {
		//User user = this.users.remove(email);
		//List<User> users = this.usersByIp.get(user.getIp());
		//users.remove(user);
		//if (users.isEmpty())
		//	this.usersByIp.remove(user.getIp());
		
		this.userDao.deleteById(email);
	}

	public synchronized void clearOld() {
		//long time = System.currentTimeMillis();
		//for (User user : this.users.values())
		//	if (time> 600_000 + user.getCreationTime())
		//		this.delete(user.getEmail());
	}

}














