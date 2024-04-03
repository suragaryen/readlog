package kr.co.readLog;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readLog.dto.JoinDTO;
import kr.co.readLog.dto.JwtTokenDto;
import kr.co.readLog.entity.MemberEntity;
import kr.co.readLog.jwt.JWTFilter;
import kr.co.readLog.jwt.JWTUtil;
import kr.co.readLog.jwt.LoginFilter;
import kr.co.readLog.naverAPI.BookVO;
import kr.co.readLog.naverAPI.NaverResultVO;
import kr.co.readLog.service.JoinService;

@Controller
public class HomeController {

	private final JoinService joinService;
	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	@Autowired
	public HomeController(JoinService joinService,AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.joinService = joinService;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

/*
	@PostMapping("/login")
	public ResponseEntity<JwtTokenDto> login(@RequestBody MemberEntity loginRequest, HttpServletResponse response) {
	    // 사용자 인증을 시도하고 인증 객체를 받아옴
	    Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

	    // 인증 객체를 SecurityContextHolder에 저장
	    SecurityContextHolder.getContext().setAuthentication(authentication);

	    // JWT 토큰 생성
	    String token = jwtUtil.createJwt(loginRequest.getUsername(), 60 * 60 * 10L);

	    // 헤더에 JWT 토큰을 담아 클라이언트에게 반환
	    response.addHeader("Authorization", "Bearer " + token);

	    // JWT 토큰을 응답에 담아 반환
	    JwtTokenDto jwtTokenDto = new JwtTokenDto();
	    jwtTokenDto.setAccessToken(token);
	    return ResponseEntity.ok(jwtTokenDto);
	}
	
	*/
	

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String goHome(HttpServletRequest request) {
		return "/content/home";
	}
	
	
	@ResponseBody
	@GetMapping("/")
	public String mainP() {
	    String name = SecurityContextHolder.getContext().getAuthentication().getName();
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	    Iterator<? extends GrantedAuthority> iter = authorities.iterator();
	    GrantedAuthority auth = iter.next();
	    String role = auth.getAuthority();
	    
	    
	    System.out.println(name);
	    return "Main Controller : " + name +role;
	}
	

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(HttpServletRequest request, HttpServletResponse response, JwtTokenDto dto) {
		
		
		
		
		//String token = dto.getAccessToken();
		//response.addHeader("Authorization", "Bearer " + token);
		//String name = SecurityContextHolder.getContext().getAuthentication().getName();
		//System.out.println("search :" + name);

		return "/content/search";
	}

	@ResponseBody
	@PostMapping("/join")
	public String joinProcess(JoinDTO joinDTO) {

		joinService.joinnProcess(joinDTO);

		return "ok";
	}

	// @RequestMapping(value = "/mainPage", method=RequestMethod.GET)
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

		if (page > 1) {

			startIndex = display + 1;
			display = page * display;

		}

		// String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text; //
		// JSON 결과
		URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/book.json")
				.queryParam("query", text).queryParam("display", display) // 최대
				.queryParam("start", startIndex).queryParam("sort", "sim").encode().build().toUri();

		// Spring 요청 제공 클래스
		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Naver-Client-Id", clientId)
				.header("X-Naver-Client-Secret", clientSecret).build();

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

		List<BookVO> books = resultVO.getItems(); // books를 list.html에 출력 -> model 선언
		int resultCnt = resultVO.getTotal();

		Pagination pagination = new Pagination(resultCnt, page);

		// 각 행의 row num?
		/*
		 * 
		 * resultCnt = 38이면
		 * 
		 * 
		 */

		// System.out.println(resultCnt);
		// System.out.println(resultVO.getStart());
		// System.out.println(resultVO.toString());

		pagination.setStartIndex(startIndex);

		System.out.println(pagination.toString());

		mav.addObject("books", books);
		mav.addObject("cnt", resultCnt);
		mav.addObject("searchInput", searchInput);
		mav.setViewName("content/searchResult");
		mav.addObject("pagination", pagination);

		return mav;
	}

}// class end
