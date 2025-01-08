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
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
		}

	}
	
	@PutMapping("/login1")
	public ResponseEntity<?> login1(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {
		try {
		    String fakeUserId = token.findCookie(request, "fakeUserId");
		    String userEmail = token.findCookie(request, "userEmail");

		    if (fakeUserId == null || userEmail == null) {
		        // Validar las credenciales del usuario
		        user = this.userService.find(user.getEmail(), user.getPwd());
		        if (user == null) {
		            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
		        }

		        // Generar un nuevo ID para la cookie
		        fakeUserId = UUID.randomUUID().toString();

		        // Configurar la cookie de fakeUserId
		        Cookie idCookie = new Cookie("fakeUserId", fakeUserId);
		        idCookie.setMaxAge(3600 * 24 * 365); // 1 año
		        idCookie.setPath("/");
		        //idCookie.setHttpOnly(true); // Previene accesos desde JavaScript
		        idCookie.setSecure(true);   // Solo HTTPS
		        idCookie.setAttribute("SameSite", "Strict"); // Previene CSRF

		        // Configurar la cookie de userEmail
		        Cookie emailCookie = new Cookie("userEmail", user.getEmail());
		        emailCookie.setMaxAge(3600 * 24 * 365); // 1 año
		        emailCookie.setPath("/");
		        emailCookie.setHttpOnly(false); // Previene accesos desde JavaScript
		        emailCookie.setSecure(true);   // Solo HTTPS
		        emailCookie.setAttribute("SameSite", "Strict"); // Previene CSRF

		        // Añadir las cookies a la respuesta
		        response.addCookie(idCookie);
		        response.addCookie(emailCookie);

		        // Asociar la cookie con el usuario y generar un token
		        user.setCookie(fakeUserId);
		        user.setToken(UUID.randomUUID().toString());
		        this.userDao.save(user);
		    } else {
		        // Buscar al usuario por la cookie existente
		        user = this.userDao.findByCookie(fakeUserId);
		        if (user != null && user.getEmail().equals(userEmail)) {
		            // Generar un nuevo token para el usuario
		            user.setToken(UUID.randomUUID().toString());
		            this.userDao.save(user);
		        } else {
		            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cookie caducada o inválida");
		        }
		    }

		    // Retornar solo el token como string
		    return ResponseEntity.ok(user.getToken());
		}catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
		}

	}
	
	//@GetMapping("/verificar-correo")
	//public ResponseEntity<Boolean> verificarCorreo(@RequestParam String email) {
	  //  if (email == null || email.isEmpty()) {
	    //    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
	    //}
	    //return this.userService.verificarCorreo(email);
	//}
	
	//KEKE ESTO SE USA?????? NO LO ENCUENTRO
	@GetMapping("/checkCookie")
	public String checkCookie(HttpServletRequest request) {
		String fakeUserId = token.findCookie(request, "fakeUserId");
		if (fakeUserId!=null) {
			User user = this.userDao.findByCookie(fakeUserId);
			if (user!=null) {
				user.setToken(UUID.randomUUID().toString());
				this.userDao.save(user);
				return user.getToken();
			}
		}
		return null;
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
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			                .body(Map.of("message", "Error al verificar su sesión."));
			 }

		}catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
		}
		
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(Map.of("message", "Usuario no autenticado."));
		
		
		

	}

	

	


	

	//@DeleteMapping("/delete2")
	//public ResponseEntity<Map<String, String>> deleteUser(HttpServletRequest request, @RequestParam String email) {

//		String fakeUserId = token.findCookie(request, "fakeUserId");
	//	 if (fakeUserId != null) {
		//	 boolean validado=token.validar(fakeUserId);
			// if (validado) {
				// Map<String, String> response = new HashMap<>();
				  //  try {
				    //    userService.delete(email);
				      //  response.put("message", "Usuario eliminado exitosamente");
				       // return ResponseEntity.ok(response);
				   // } catch (Exception e) {
				     //   response.put("message", "Error al eliminar el usuario");
				      //  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
				    //} 
			 //}
			// return ResponseEntity.status(404).body(null);
		 //}
		 //return ResponseEntity.status(404).body(null);
			
		
	//}


	//@GetMapping("/info")
	//public ResponseEntity<User> getUserInfo(HttpServletRequest request,@RequestParam String email) {
		// String fakeUserId = token.findCookie(request, "fakeUserId");
		// if (fakeUserId != null) {
			// boolean validado=token.validar(fakeUserId);
			// if (validado) {
				// User user = userService.getUserInfo(email);
				  //  if (user != null) {
				    //    return ResponseEntity.ok(user); // Devolvemos el usuario si existe
				    //} else {
				      //  return ResponseEntity.status(404).body(null); // Si no se encuentra el usuario, devolvemos un 404
				   // } 
		        //}
			// return ResponseEntity.status(404).body(null);
			 
		// }
		// return ResponseEntity.status(404).body(null);
	   
	//}
	
	
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
	public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> emailDataMap) {
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
	

	
}
















