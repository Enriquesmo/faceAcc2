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
	public ResponseEntity<Boolean> verificarCorreo(String email) {
	    // Comprobamos si el correo existe en la base de datos
	    Optional<User> userOpt = this.userDao.findById(email); 

	    // Si el correo ya existe, devolvemos "true"
	    if (userOpt.isPresent()) {
	        return ResponseEntity.ok(true);
	    }
	    
	    // Si no existe, devolvemos "false"
	    return ResponseEntity.ok(false);
	}
	public ResponseEntity<Boolean> verificarVip(String email) {
	    // Comprobamos si el correo existe en la base de datos
	    Optional<User> userOpt = this.userDao.findById(email); 
	    
	    // Si el correo no existe, devolvemos false
	    if (!userOpt.isPresent()) {
	        return ResponseEntity.ok(false);
	    }
	    
	    // Si el correo existe, verificamos si el usuario es VIP
	    User user = userOpt.get();
	    
	    // Comprobamos el estado VIP del usuario (asumiendo que existe un método 'isVip' en el modelo User)
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
	    // Devolvemos true si el usuario es VIP, de lo contrario false

	    
	    
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
		//this.userDao.deleteByCreador(email);
		this.userDao.deleteById(email);
		
	}

	public synchronized void clearOld() {
		//long time = System.currentTimeMillis();
		//for (User user : this.users.values())
		//	if (time> 600_000 + user.getCreationTime())
		//		this.delete(user.getEmail());
	}
	 public User getUserInfo(String email) {
	        Optional<User> optUser = userDao.findById(email);
	        if (optUser.isPresent()) {
	            return optUser.get(); // Devolvemos el usuario si se encuentra en la base de datos
	        } else {
	            return null; // Devolvemos null si no se encuentra el usuario
	        }
	    }
	 
	 @Autowired
		private EmailService emailService; // Servicio para enviar emails
		
		public ResponseEntity<String> recuperarContrasena(String email) throws MessagingException {
			System.out.println("PATATAAAAAAAAAAAAAAAAAAAAAAAAA");
		    Optional<User> optUser = userDao.findById(email);
		    if (!optUser.isPresent()) {
		        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El correo electrónico no está registrado.");
		    }

		    User user = optUser.get();
		    String nuevaContrasena = generarContrasenaAleatoria();
		    System.out.println("Patata 2 /"+nuevaContrasena);

		    
		    emailService.enviarEmail(email, "Recuperación de Contraseña",
		            "Tu nueva contraseña es: " + nuevaContrasena);
		    
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

		    // Añadir al menos una letra mayúscula, una minúscula y un número
		    contrasena.append(mayusculas.charAt(random.nextInt(mayusculas.length())));
		    contrasena.append(minusculas.charAt(random.nextInt(minusculas.length())));
		    contrasena.append(numeros.charAt(random.nextInt(numeros.length())));

		    // Mezclar los caracteres restantes aleatoriamente
		    String todosCaracteres = mayusculas + minusculas + numeros;
		    int longitud = 8; // Longitud de la contraseña

		    for (int i = 3; i < longitud; i++) {
		        contrasena.append(todosCaracteres.charAt(random.nextInt(todosCaracteres.length())));
		    }

		    // Mezclar los caracteres para evitar un patrón predecible
		    List<Character> caracteresMezclados = contrasena.chars()
		        .mapToObj(c -> (char) c)
		        .collect(Collectors.toList());
		    Collections.shuffle(caracteresMezclados);

		    StringBuilder contrasenaFinal = new StringBuilder();
		    caracteresMezclados.forEach(contrasenaFinal::append);

		    return contrasenaFinal.toString();
		}
}














