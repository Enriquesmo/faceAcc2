package edu.uclm.esi.fakeaccountsbe.services;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PagoService {
	
	static {
		Stripe.apiKey = "sk_test_51Q7a2A02ojBChuHywiSB12ol4s70za8Au33JznMLdR3E4FH29skG1KwtkYyv23VoT1HXsY7RD1lmt9Xu6vNvAvRn00y93qKS3B";
	}
	

	public String prepararTransaccion(long importe) {
		PaymentIntentCreateParams params = new PaymentIntentCreateParams.Builder().setCurrency("eur").setAmount(importe).build();
		PaymentIntent intent;
		try {
			intent = PaymentIntent.create(params);
			JSONObject jso = new JSONObject(intent.toJson());
			String clientSecret = jso.getString("client_secret");
			return clientSecret;
		} catch (StripeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

	}

}
