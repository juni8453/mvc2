package hello.itemservice;

import hello.itemservice.web.validation.ItemValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

}

// 글로벌 검증기 설정
// 이렇게 하면 기존 컨트롤러의 @InitBinder 를 제거해도 글로벌 설정으로 검증기가 정상 동작한다.
// 이렇게 글로벌 설정을 하면 BeanValidator 가 자동 등록되지 않고, 글로벌 설정을 직접 사용하는 경우는 드물다.

// public class ItemServiceApplication implements WebMvcConfigurer {
// 	public static void main(String[] args) {
// 		SpringApplication.run(ItemServiceApplication.class, args);
// 	}
//
// 	@Override
// 	public Validator getValidator() {
// 		return new ItemValidator();
// 	}
//}
