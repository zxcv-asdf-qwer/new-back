package co.kr.compig.api.presentation;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class SwaggerController {
	@RequestMapping("/")
	public void doSocialLogin(HttpServletResponse reponse) throws IOException {
		reponse.sendRedirect("/swagger-ui/index.html");
	}

	@GetMapping("favicon.ico")
	@ResponseBody
	void returnNoFavicon() {
	}
}
