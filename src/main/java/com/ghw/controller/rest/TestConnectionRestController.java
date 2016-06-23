package com.ghw.controller.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/testConnection")
public class TestConnectionRestController {

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public boolean list() {
		return true;

	}
}
