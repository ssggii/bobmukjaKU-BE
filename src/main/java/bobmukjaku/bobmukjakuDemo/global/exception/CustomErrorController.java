package bobmukjaku.bobmukjakuDemo.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<String> handleError() {
        System.out.println("LoggedOutTokenException이 필터에서 발생");
        return new ResponseEntity<>("로그아웃한 토큰임", HttpStatus.UNAUTHORIZED);
    }

    /*public String getErrorPath() {
        return "/error";
    }*/
}
