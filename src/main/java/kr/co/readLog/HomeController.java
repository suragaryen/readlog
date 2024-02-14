package kr.co.readLog;

import java.net.URI;
import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.readLog.naverAPI.BookVO;
import kr.co.readLog.naverAPI.NaverResultVO;


	@Controller
	public class HomeController {
		
		@RequestMapping(value = "/home", method=RequestMethod.GET)
		public String goHome(HttpServletRequest request) {
			return "content/home";
		}
		
		
		
		//@RequestMapping(value = "/mainPage", method=RequestMethod.GET)
		@GetMapping("/mainPage")
		public String goMainpage(//String text, 
								//ModelAndView mav
								//Model model
								) {
			
			ModelAndView mav = new ModelAndView();
			
			// 네이버 검색 API 요청
			String clientId = "a1_WahDd7nyERBCyaRSv"; 		
			String clientSecret = "Uw7lvHeD8e";
			
			String text = "스프링";
			
			 //String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;    // JSON 결과
			URI uri = UriComponentsBuilder
			        .fromUriString("https://openapi.naver.com")
			        .path("/v1/search/book.json")
			        .queryParam("query", text)
			        .queryParam("display", 10)
			        .queryParam("start", 1)
			        .queryParam("sort", "sim")
			        .encode()
			        .build()
			        .toUri();
			
			// Spring 요청 제공 클래스 
			RequestEntity<Void> req = RequestEntity
                    .get(uri)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .build();
			
			 // Spring 제공 restTemplate
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
			
			 // JSON 파싱 (Json 문자열을 객체로 만듦, 문서화)
	        ObjectMapper om = new ObjectMapper();
	        NaverResultVO resultVO = null;
	        
	       
	        try {
	        	resultVO = om.readValue(resp.getBody(), NaverResultVO.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
	        
	        List<BookVO> books =resultVO.getItems();	// books를 list.html에 출력 -> model 선언
	        
	        System.out.println(books);
	        mav.addObject("books", books);
	        
	        //model.add("books", books);
			
			return "content/mainPage";
		}
		
		
		
		
	}//class end
	
