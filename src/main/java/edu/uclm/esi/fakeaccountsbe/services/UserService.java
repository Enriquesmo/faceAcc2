package edu.uclm.esi.fakeaccountsbe.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.User;

import java.util.Random;
import java.util.stream.Collectors;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserService {
		
	@Autowired //incluimos esto para los DAO siempre
	private UserDao userDao;
	

	public void registrar(String ip, User user) {
		if(this.userDao.findById(user.getEmail()).isPresent())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ya existe un usuario con ese correo electrónico");
		
		user.setIp(ip);
		user.setCreationTime(System.currentTimeMillis());
		this.userDao.save(user);
	}
	
	public ResponseEntity<Boolean> verificarCorreo(String email) {
	    Optional<User> userOpt = this.userDao.findById(email); 
	    if (userOpt.isPresent()) {
	        return ResponseEntity.ok(true);
	    }
	    return ResponseEntity.ok(false);
	}
	
	public ResponseEntity<Boolean> verificarVip(String email) {
		Optional<User> userOpt = this.userDao.findById(email); 
	    if (!userOpt.isPresent()) {
	        return ResponseEntity.ok(false);
	    }
	    User user = userOpt.get();
	    boolean isVip = user.getVip(); 
	    if (isVip) {
	    	boolean fecha = user.getVipFecha().isAfter(LocalDateTime.now());
		    boolean permitir=false;
		    if(isVip&&fecha) {
		    	permitir=true;
		    }
		    if(!fecha) {
		    	user.setVip(false);
		    	this.userDao.save(user);
		    }
		    return ResponseEntity.ok(permitir);
	    }
	    return ResponseEntity.ok(false);	    	    
	}

	public User find(String email, String pwd) {
		Optional<User> optUser=this.userDao.findById(email);
		if (!optUser.isPresent())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no encontrado");
		User user=optUser.get();
		if (!user.getPwd().equals(pwd))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales incorrectas ");
		return user;
	}
	 
	 @Autowired
		private EmailService emailService; 
		
		public ResponseEntity<String> recuperarContrasena(String email) throws MessagingException {
		    Optional<User> optUser = userDao.findById(email);
		    if (!optUser.isPresent()) {
		        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El correo electrónico no está registrado.");
		    }
		    User user = optUser.get();
		    String nuevaContrasena = generarContrasenaAleatoria();
		    emailService.enviarEmail(email, "Recuperación de Contraseña","Tu nueva contraseña es: " + nuevaContrasena);
		    user.setPwd(nuevaContrasena);
		    userDao.save(user);
		    return ResponseEntity.ok("Se ha enviado un correo con la nueva contraseña.");
		}


		private String generarContrasenaAleatoria() {
		    String mayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		    String minusculas = "abcdefghijklmnopqrstuvwxyz";
		    String numeros = "0123456789";

		    StringBuilder contrasena = new StringBuilder();
		    Random random = new Random();
		    
		    contrasena.append(mayusculas.charAt(random.nextInt(mayusculas.length())));
		    contrasena.append(minusculas.charAt(random.nextInt(minusculas.length())));
		    contrasena.append(numeros.charAt(random.nextInt(numeros.length())));

		    String todosCaracteres = mayusculas + minusculas + numeros;
		    int longitud = 8; 

		    for (int i = 3; i < longitud; i++) {
		        contrasena.append(todosCaracteres.charAt(random.nextInt(todosCaracteres.length())));
		    }


		    List<Character> caracteresMezclados = contrasena.chars()
		        .mapToObj(c -> (char) c)
		        .collect(Collectors.toList());
		    Collections.shuffle(caracteresMezclados);

		    StringBuilder contrasenaFinal = new StringBuilder();
		    caracteresMezclados.forEach(contrasenaFinal::append);

		    return contrasenaFinal.toString();
		}
		
		public void sendEmail(String to, String subject, String body) {
	        emailService.sendEmail(to, subject, body);
	    }
}














