package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // 검증할 클래스.isAssignableFrom(clazz);
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    // 컨트롤러에서 target 은 item, errors 는 bindingResult 가 넘어올 것이다.
    public void validate(Object target, Errors errors) {
        // target 을 검증할 클래스 타입으로 캐스팅해준다.
        Item item = (Item) target;

        if (!StringUtils.hasText(item.getItemName())) {
            // errors 는 bindingResult 의 부모라서 rejectValue() 가 있다.
            errors.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
