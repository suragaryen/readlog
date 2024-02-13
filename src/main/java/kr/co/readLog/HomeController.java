package kr.co.readLog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;


	@Controller
	public class HomeController {
		
		@RequestMapping(value = "/home", method=RequestMethod.GET)
		public String goHome(HttpServletRequest request) {
			return "content/home";
		}
		
		@RequestMapping(value = "/mainPage", method=RequestMethod.GET)
		public String goMainpage(HttpServletRequest request) {
			return "content/mainPage";
		}
		
		
		
		
	}//class end
	
