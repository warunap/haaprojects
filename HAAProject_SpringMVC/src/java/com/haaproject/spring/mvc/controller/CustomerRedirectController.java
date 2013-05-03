/**
 *      Project:HAAProject_SpringMVC       
 *    File Name:CustomerRedirectController.java	
 * Created Time:2013-5-3下午11:38:46
 */
package com.haaproject.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

/**
 * @author Geln Yang
 * 
 */
@Controller
@RequestMapping("/customerRedirect")
public class CustomerRedirectController {


	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit( BindingResult result, SessionStatus status) {

		if (result.hasErrors()) {
			// if validator failed
			return "CustomerForm";
		} else {
			// form success
			return "CustomerSuccess";
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String initForm(ModelMap model) {
		return "CustomerSuccess";
	}
}
