package edu.uclm.esi.fakeaccountsbe.http;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("tokens")
@CrossOrigin(origins = { "https://localhost:4200","https://localhost:8080" }, allowCredentials = "true")
public class TokenController {
	
	@Autowired
	private UserDao userDao;
	
	
	@GetMapping("/validate-session")
	public ResponseEntity<Boolean> validateSession(HttpServletRequest request) {
	    // Buscar la cookie con el nombre "fakeUserId"
	    String fakeUserId = this.findCookie(request, "fakeUserId");
	    if (fakeUserId != null) {
	        boolean validado=validar(fakeUserId);
	        //User user = this.userDao.findByCookie(fakeUserId);
	        if (validado) {
	            return ResponseEntity.ok(true); // Sesión válida
	        }
	    }
	    return ResponseEntity.ok(false); // Sesión no válida o cookie no encontrada
	}

	@PutMapping("/validate")
	public boolean validar(String Token) {
		User user = this.userDao.findByCookie(Token);
		if (user != null) {
            return true; // Sesión válida
        }else {
        	return false;
        }
	}

	public String findCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies== null) {
			return null;
		}
		for (int i=0; i<cookies.length; i++) {
			if(cookies[i].getName().equals(cookieName))
				return cookies[i].getValue();
		}
		return null;
	}
}
















