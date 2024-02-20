package kr.co.readLog;

import java.net.URI;
import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
		
		
		
		@RequestMapping(value = "/search", method=RequestMethod.GET)
		public String search(HttpServletRequest request) {
			return "content/search";
		}
		
		
		
		
		//@RequestMapping(value = "/mainPage", method=RequestMethod.GET)
		@GetMapping("/searchResult")
		public ModelAndView goMainpage(@RequestParam("searchInput") String searchInput,
									@RequestParam(defaultValue = "1") int page) {
			
			ModelAndView mav = new ModelAndView();
			
			
			// 네이버 검색 API 요청
			String clientId = "a1_WahDd7nyERBCyaRSv"; 		
			String clientSecret = "Uw7lvHeD8e";
			String text = searchInput;
			
			
			
			int startIndex = 1;
			int display = 9;
			
			if(page>1) {
				
				startIndex = display + 1;
				display = page * display;
				
			}
			
			
			
			/*
			  
			  int start 
			  
			 page 1
			 start = 1 / display = 9
			 
			 page 2
			 start = 10 / display = 18
			 
			 page 3
			 start = 19 / display = 27
			 
			 if(page > 1){
			 
			 start = display + 1
			 display = (page* display)
			 
			 } 
			  
			  
			  
			  
			 
			 */
			
			
			 //String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;    // JSON 결과
			URI uri = UriComponentsBuilder
			        .fromUriString("https://openapi.naver.com")
			        .path("/v1/search/book.json")
			        .queryParam("query", text)
			        .queryParam("display", display) //최대
			        .queryParam("start", startIndex)
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
	        int resultCnt = resultVO.getTotal();
	        
	        Pagination pagination = new Pagination(resultCnt, page);
	        
	        
	        //각 행의 row num?
			/*
			  
			  resultCnt = 38이면
			  
			 
			 */
	        
	        
	        //System.out.println(resultCnt);
	        //System.out.println(resultVO.getStart());
	        //System.out.println(resultVO.toString());
	        
	        pagination.setStartIndex(startIndex);
	        
	        System.out.println(pagination.toString());
	        
	        mav.addObject("books", books);
	        mav.addObject("cnt", resultCnt);
	        mav.addObject("searchInput",searchInput);
	        mav.setViewName("content/searchResult");
	        mav.addObject("pagination", pagination);
	        
	        

	        return mav;
		}	
		
		
		
		
	}//class end
	
