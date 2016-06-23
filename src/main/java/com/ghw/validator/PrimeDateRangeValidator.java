package com.ghw.validator;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;

import com.ghw.util.Context;

@FacesValidator("primeDateRangeValidator")
public class PrimeDateRangeValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		// Leave the null handling of startDate to required="true"
		Object startDateValue = component.getAttributes().get("startDate");
		if (startDateValue == null) {
			return;
		}

		RequestContext cont = RequestContext.getCurrentInstance();

		Date startDate = (Date) startDateValue;
		Date endDate = (Date) value;
		if (endDate.before(startDate)) {
			cont.addCallbackParam("error", true);
			Context.addErrorMessageFromMSG("aaaaaaaaaaaaaaaaa");
			// throw new ValidatorException(new FacesMessage(
			// FacesMessage.SEVERITY_ERROR,
			// "The end date cannot be less than start date", ""));

		} else
			cont.addCallbackParam("error", false);
	}
}
