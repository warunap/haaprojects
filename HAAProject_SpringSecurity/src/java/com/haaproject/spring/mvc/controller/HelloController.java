/**
 *      Project:HAAProject_SpringMVC       
 *    File Name:HelloController.java	
 * Created Time:2013-4-27下午12:02:21
 */
package com.haaproject.spring.mvc.controller;


/**
 * @author Geln Yang
 *
 */
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
 
@Controller
@RequestMapping("/welcome")
public class HelloController {
 
	@RequestMapping(method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Spring 3 MVC Hello World");
		return "hello";
 
	}
 
}
