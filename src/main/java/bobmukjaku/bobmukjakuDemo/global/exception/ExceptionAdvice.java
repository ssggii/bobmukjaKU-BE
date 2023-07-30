package bobmukjaku.bobmukjakuDemo.global.exception;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 @Controller에서 발생하는 예외를 처리, @ExceptionHanlder를 통해 어떤 예외를 처리할지 명시
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberException(Exception exception){
        return new ResponseEntity(HttpStatus.OK);
    }
}
