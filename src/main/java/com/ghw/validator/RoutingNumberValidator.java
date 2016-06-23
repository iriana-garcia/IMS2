package com.ghw.validator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;

import com.ghw.util.Context;

@FacesValidator("routingNumberValidator")
public class RoutingNumberValidator implements Validator, ClientValidator {

	public RoutingNumberValidator() {

	}

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		String content = value.toString();

		int n = 0;
		for (int i = 0; i < content.length(); i += 3) {
			n += (Character.getNumericValue(content.charAt(i)) * 3)
					+ (Character.getNumericValue(content.charAt(i + 1)) * 7)
					+ Character.getNumericValue(content.charAt(i + 2));
		}
		// If the resulting sum is an even multiple of ten (but not zero),
		// the aba routing number is good.
		if (!(n != 0 && n % 10 == 0)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					Context.getUIMsg("routing_number_invalid"),
					Context.getUIMsg("routing_number_invalid")));
		}

	}

	public Map<String, Object> getMetadata() {
		return null;
	}

	public String getValidatorId() {
		return "custom.routingNumberValidator";
	}

}
