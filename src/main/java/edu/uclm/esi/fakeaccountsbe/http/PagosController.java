package edu.uclm.esi.fakeaccountsbe.http;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.fakeaccountsbe.services.PagoService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("pagos")
@CrossOrigin(origins = { "https://localhost:4200" }, allowCredentials = "true")
public class PagosController {

    @Autowired
    private PagoService service;

	@Autowired
	private TokenController token;
    
	@PutMapping("/prepararTransaccion")
	public ResponseEntity<?> prepararTransaccion(HttpServletRequest request,@RequestBody float importe) {
		String fakeUserId = token.findCookie(request, "fakeUserId");
		 if (fakeUserId != null) {
			 boolean validado=token.validar(fakeUserId);
			 if (validado) {
				 try {
					 String transaccion=this.service.prepararTransaccion((long) (importe*100));
					 return ResponseEntity.ok(transaccion);
				 }catch (ResponseStatusException e) {
		                return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
				 }
			 }
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error al verificar su sesión."));
		 }
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Usuario no autenticado."));
	}

	
	@PostMapping("/confirmarPago")
	public ResponseEntity<Map<String, String>> confirmarPago(HttpServletRequest request,@RequestParam String paymentMethodId,@RequestParam String email,@RequestParam String clientSecret) {
		String fakeUserId = token.findCookie(request, "fakeUserId");
		 if (fakeUserId != null) {
			 boolean validado=token.validar(fakeUserId);
			 if (validado) {
				    Map<String, String> response = new HashMap<>();
				    try {
				        String message = service.confirmarPago(paymentMethodId, email, clientSecret);
				        response.put("message", message);
				        return ResponseEntity.ok(response);
				    } catch (Exception e) {
				        response.put("error", e.getMessage());
				        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
				    }		   
			 }
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error al verificar su sesión."));
		 }
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Usuario no autenticado."));
	}

}
