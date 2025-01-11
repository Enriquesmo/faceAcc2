package edu.uclm.esi.fakeaccountsbe.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.User;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PagoService {
	@Autowired 
	private UserDao userDao;
    static {
        Stripe.apiKey = "sk_test_51Q7a1xAINUUPHMJgF2JCHcQ3rpBp2n43DJ0504Pf59y9jk8khtvDiT1Iq0MgoL4ADjsZh89x7j6eWiQgcXBnPwKx00Fg7YafKM";
    }

    public String prepararTransaccion(long importe) {
        PaymentIntentCreateParams params = new PaymentIntentCreateParams.Builder()
            .setCurrency("eur")
            .setAmount(importe)
            .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getClientSecret();  
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el PaymentIntent: " + e.getMessage());
        }
    }

    public String confirmarPago(String paymentMethodId, String email, String clientSecret) {
        try {
            String paymentIntentId = extractPaymentIntentId(clientSecret);

            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(paymentMethodId)
                .build();

            PaymentIntent confirmedIntent = intent.confirm(params);

            if ("succeeded".equals(confirmedIntent.getStatus())) {
        		Optional<User> optUser=this.userDao.findById(email);
        		
        		if (!optUser.isPresent())
        			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales incorrectas1");
        		
        		User user=optUser.get();
        		user.setVip(true);
        		user.setVipFecha(LocalDateTime.now().plus(1, ChronoUnit.YEARS));
        		this.userDao.save(user);
                return "Pago confirmado exitosamente para el email: " + email;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago no se pudo confirmar.");
            }

        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al confirmar el pago: " + e.getMessage());
        }
    }


    private String extractPaymentIntentId(String clientSecret) {
        if (clientSecret == null || !clientSecret.startsWith("pi_")) {
            throw new IllegalArgumentException("El clientSecret no es válido.");
        }
        return clientSecret.split("_secret")[0]; 
    }

}
