package com.sasicodes.softwareTesting.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StripeApi {

    public Charge create(Map<String, Object> params, RequestOptions requestOptions) throws StripeException {
        // letting the client which invokes handles the exception which will occur, that why using throw
        return Charge.create(params,requestOptions);
    }
}
