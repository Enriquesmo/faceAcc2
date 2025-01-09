package edu.uclm.esi.fakeaccountsbe.http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.CredencialesRegistro;
import edu.uclm.esi.fakeaccountsbe.model.User;
import edu.uclm.esi.fakeaccountsbe.services.UserService;

import jakarta.mail.MessagingException;
import edu.uclm.esi.fakeaccountsbe.model.EmailData;
import edu.uclm.esi.fakeaccountsbe.services.EmailService;

@RestController
@RequestMapping("users")

public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TokenController token;
	
	@PostMapping("/registrar1")
	public ResponseEntity<?> registrar1(HttpServletRequest req, @RequestBody CredencialesRegistro cr) {
		try {
			cr.comprobar();
			User user = new User();
			user.setEmail(cr.getEmail());
			user.setPwd(cr.getPwd1());
			this.userService.registrar(req.getRemoteAddr(), user);
			return ResponseEntity.ok(true);
		}catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
		}
	}
	
	@PutMapping("/login1")
	public ResponseEntity<?> login1(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {
		try {
		    String fakeUserId = token.findCookie(request, "fakeUserId");
		    String userEmail = token.findCookie(request, "userEmail");
		    if (fakeUserId == null || userEmail == null) {
		        user = this.userService.find(user.getEmail(), user.getPwd());
		        if (user == null) {
		            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
		        }
		        fakeUserId = UUID.randomUUID().toString();
		        Cookie idCookie = new Cookie("fakeUserId", fakeUserId);
		        idCookie.setMaxAge(3600 * 24 * 365); 
		        idCookie.setPath("/");

		        idCookie.setSecure(true);   
		        idCookie.setAttribute("SameSite", "Strict"); 

		        Cookie emailCookie = new Cookie("userEmail", user.getEmail());
		        emailCookie.setMaxAge(3600 * 24 * 365); 
		        emailCookie.setPath("/");
		        emailCookie.setHttpOnly(false); 
		        emailCookie.setSecure(true);   
		        emailCookie.setAttribute("SameSite", "Strict"); 

		        response.addCookie(idCookie);
		        response.addCookie(emailCookie);

		        user.setCookie(fakeUserId);
		        user.setToken(UUID.randomUUID().toString());
		        this.userDao.save(user);
		    } else {
		        user = this.userDao.findByCookie(fakeUserId);
		        if (user != null && user.getEmail().equals(userEmail)) {
		            user.setToken(UUID.randomUUID().toString());
		            this.userDao.save(user);
		        } else {
		            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cookie caducada o inválida");
		        }
		    }

		    return ResponseEntity.ok(user.getToken());
		}catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
		}
	}

	@GetMapping("/verificar-vip")
	public ResponseEntity<?> verificarVip(HttpServletRequest request,@RequestParam String email) {
		try {
			String fakeUserId = token.findCookie(request, "fakeUserId");
			 if (fakeUserId != null) {
				 boolean validado=token.validar(fakeUserId);
				 if (validado) {
					    if (email == null || email.isEmpty()) {
					        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
					    }
					    return this.userService.verificarVip(email);
				 }
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error al verificar su sesión."));
			 }

		}catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
		}
	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Usuario no autenticado."));
		
	}
	
	@PostMapping("/recuperarContrasena")
	public ResponseEntity<String> recuperarContrasena(@RequestBody Map<String, String> request) {
	    String email = request.get("email");
	    try {
	        userService.recuperarContrasena(email);
	        return ResponseEntity.ok("Se ha enviado una nueva contraseña a tu correo electrónico.");
	    } catch (ResponseStatusException e) {
	        return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getReason());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error desconocido: " + e.getMessage());
	    }
	}
	
	
	@PostMapping("/send-email")
	public ResponseEntity<Map<String, String>> sendEmail(HttpServletRequest request,@RequestBody Map<String, String> emailDataMap) {
		 String fakeUserId = token.findCookie(request, "fakeUserId");
		    if (fakeUserId != null) {
		        boolean validado = token.validar(fakeUserId);
		        if (validado) {
		    		String destinatario = emailDataMap.get("destinatario");
		    	    String asunto = emailDataMap.get("asunto");
		    	    String cuerpo = emailDataMap.get("cuerpo");

		    	    Map<String, String> response = new HashMap<>();
		    	    try {
		    	    	
		    	        userService.sendEmail(destinatario, asunto, cuerpo);
		    	        response.put("message", "Correo enviado exitosamente");
		    	        return ResponseEntity.ok(response);
		    	    } catch (Exception e) {
		    	        response.put("message", "Error al enviar el correo");
		    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		    	    }
		        }
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error al verificar su sesión."));
		    }
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Usuario no autenticado."));
	}
	

	
}
















