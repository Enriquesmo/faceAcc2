package edu.uclm.esi.fakeaccountsbe.http;

import java.util.Collection;

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

@RestController
@RequestMapping("users")

public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserDao userDao;
	
	@GetMapping("/checkCookie")
	public String checkCookie(HttpServletRequest request) {
		String fakeUserId = this.findCookie(request, "fakeUserId");
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

	
	@PostMapping("/registrar1")
	public void registrar1(HttpServletRequest req, @RequestBody CredencialesRegistro cr) {
		cr.comprobar();
		User user = new User();
		user.setEmail(cr.getEmail());
		user.setPwd(cr.getPwd1());
		user.setVip(cr.getVip());
		this.userService.registrar(req.getRemoteAddr(), user);
	}
	@GetMapping("/verificar-correo")
	public ResponseEntity<Boolean> verificarCorreo(@RequestParam String email) {
	    if (email == null || email.isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
	    }
	    return this.userService.verificarCorreo(email);
	}
	@GetMapping("/verificar-vip")
	public ResponseEntity<Boolean> verificarVip(@RequestParam String email) {
	    if (email == null || email.isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
	    }
	    return this.userService.verificarVip(email);
	}



	@GetMapping("/registrar2")
	public void registrar2(HttpServletRequest req, @RequestParam String email, @RequestParam String pwd1, @RequestParam String pwd2) {
		CredencialesRegistro cr = new CredencialesRegistro();
		cr.setEmail(email);
		cr.setPwd1(pwd1);
		cr.setPwd2(pwd2);
		cr.comprobar();
		User user = new User();
		user.setEmail(cr.getEmail());
		user.setPwd(cr.getPwd1());
		
		this.userService.registrar(req.getRemoteAddr(), user);
	}
	
	@GetMapping("/registrarMuchos")
	public void registrarMuchos(HttpServletRequest req, @RequestParam String name, @RequestParam Integer n) {
		for (int i=0; i<n; i++)
			this.registrar2(req, name + i + "@pepe.com", "Pepe1234", "Pepe1234");
	}
	
	@PutMapping("/login1")
	public ResponseEntity<String> login1(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {
	    String fakeUserId = this.findCookie(request, "fakeUserId");
	    String userEmail = this.findCookie(request, "userEmail");

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
	}

	
	
	private String findCookie(HttpServletRequest request, String cookieName) {
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
	
	@GetMapping("/validate-session")
	public ResponseEntity<Boolean> validateSession(HttpServletRequest request) {
	    // Buscar la cookie con el nombre "fakeUserId"
	    String fakeUserId = this.findCookie(request, "fakeUserId");
	    if (fakeUserId != null) {
	        // Verificar si el usuario asociado a la cookie existe en la base de datos
	        User user = this.userDao.findByCookie(fakeUserId);
	        if (user != null) {
	            return ResponseEntity.ok(true); // Sesión válida
	        }
	    }
	    return ResponseEntity.ok(false); // Sesión no válida o cookie no encontrada
	}

	
	@GetMapping("/login2")
	public User login2(HttpServletResponse response, @RequestParam String email, @RequestParam String pwd) {
		User user = this.userService.find(email, pwd);
		user.setToken(UUID.randomUUID().toString());
		response.setHeader("token", user.getToken());
		return user;
	}
	
	@GetMapping("/login3/{email}")
	public User login3(HttpServletResponse response, @PathVariable String email, @RequestParam String pwd) {
		return this.login2(response, email, pwd);
	}
	
	@GetMapping("/getAllUsers")
	public Iterable<User>  getAllUsers() {
		return this.userService.getAllUsers();
	}
	
	@DeleteMapping("/delete")
	public void delete(HttpServletRequest request, @RequestParam String email, @RequestParam String pwd) {
		User user = this.userService.find(email, pwd);
		
		String token = request.getHeader("token");
		if (!token.equals(user.getToken()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token " + token + " inválido");
		
		this.userService.delete(email);
	}
	
	@DeleteMapping("/clearAll")
	public void clearAll(HttpServletRequest request) {
		String sToken = request.getHeader("prime");
		Integer token = Integer.parseInt(sToken);
		if (!isPrime(token.intValue()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Debes pasar un número primo en la cabecera");
		if (sToken.length()!=3)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El nº primo debe tener tres cifras");
		this.userService.clearAll();
	}
	
	private boolean isPrime(int n) {
	    if (n <= 1) return false;
	    for (int i = 2; i <= Math.sqrt(n); i++) {
	        if (n % i == 0) return false;
	    }
	    return true;
	}
	
	
	
}
















