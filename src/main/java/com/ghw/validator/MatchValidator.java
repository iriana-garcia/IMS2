package com.ghw.validator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.validate.ClientValidator;

@FacesValidator("matchValidator")
public class MatchValidator implements Validator, ClientValidator {

	public MatchValidator() {

	}

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		String content = value.toString();

		UIInput input = (UIInput) component.getAttributes().get("other");
		if (input != null) {
			String otherContent = (String) input.getSubmittedValue();

			otherContent = StringUtils.isBlank(otherContent) ? (String) input
					.getValue() : otherContent;

			if (content != null && otherContent != null
					&& !content.equals(otherContent))
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Value don't match",
						"Value don't match"));
		}

	}

	public Map<String, Object> getMetadata() {
		return null;
	}

	public String getValidatorId() {
		return "custom.matchValidator";
	}

}
