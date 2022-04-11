package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    // 해당 컨트롤러가 호출될 때마다 항상 검증기를 호출할 수 있게 설정
    // 글로벌 검증기 설정은 따로 해줘야하고 이렇게 하면 해당 컨트롤러에서만 실행된다.
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    @PostMapping("/add")
    // @Validated 는 검증기를 실행하라는 어노테이션이다.
    // 이게 붙으면 WebDataBinder 에 등록한 검증기를 찾아서 실행한다. 이 때 여러 개의 검증기가 등로돼있으면
    // 어떤 검증기가 실행되어야 하는지 구분이 필요하고, 이때 supports() 가 사용되는 것이다.
    // 여기서는 Item 객체가 넘어오니까 ItemValidator 의 validate() 가 호출된다.
    public String addItemV6(@Validated @ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {


        // 검증에 실패하면 다시 입력 폼으로 이동해야한다.
        if (bindingResult.hasErrors()) {
            // bindingResult 는 자동으로 View 로 넘어가서 따로 model 에 안 담아도 된다.
            // model.addAttribute("errors", bindingResult);
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        // 에러가 없다면 ? -> 성공 로직
        Item saveItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", saveItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
//    public String addItemV5(@ModelAttribute Item item,
//                            BindingResult bindingResult,  // 추가 errors 역할을 해줌, 항상 ModelAttribute 뒤에 와야한다. (순서 중요)
//                            RedirectAttributes redirectAttributes) {
//
//        // 검증기를 만들어서 직접적으로 호출하는 방식
//        itemValidator.validate(item, bindingResult);
//
//        // 검증에 실패하면 다시 입력 폼으로 이동해야한다.
//        if (bindingResult.hasErrors()) {
//            // bindingResult 는 자동으로 View 로 넘어가서 따로 model 에 안 담아도 된다.
//            // model.addAttribute("errors", bindingResult);
//            log.info("errors = {}", bindingResult);
//            return "validation/v2/addForm";
//        }
//
//        // 에러가 없다면 ? -> 성공 로직
//        Item saveItem = itemRepository.save(item);
//        redirectAttributes.addAttribute("itemId", saveItem.getId());
//        redirectAttributes.addAttribute("status", true);
//        return "redirect:/validation/v2/items/{itemId}";
//    }

//    @PostMapping("/add")
//    public String addItemV4(@ModelAttribute Item item,
//                            BindingResult bindingResult,  // 추가 errors 역할을 해줌, 항상 ModelAttribute 뒤에 와야한다. (순서 중요)
//                            RedirectAttributes redirectAttributes) {
//
//        if (!StringUtils.hasText(item.getItemName())) {
//            // errorCode.객체이름.필드명 으로 되어있는 properties 를 잘 찾아온다.
//            bindingResult.rejectValue("itemName", "required");
//        }
//
//        if (item.getPrice() == null || item.getPrice() < 1_000 || item.getPrice() > 1_000_000) {
//            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
//        }
//
//        if (item.getQuantity() == null || item.getQuantity() > 9_999) {
//            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
//        }
//
//        // 특정 필드가 아닌 복합 룰 검증
//        if (item.getPrice() != null && item.getQuantity() != null) {
//            int resultPrice = item.getPrice() * item.getQuantity();
//            if (resultPrice < 10_000) {
//                // 특정 필드가 없기 때문에 new FieldError 말고 ObjectError 사용 (글로벌 오류)
//                bindingResult.reject("totalPriceMin", new Object[]{10000}, null);
//            }
//        }
//
//        // 검증에 실패하면 다시 입력 폼으로 이동해야한다.
//        if (bindingResult.hasErrors()) {
//            // bindingResult 는 자동으로 View 로 넘어가서 따로 model 에 안 담아도 된다.
//            // model.addAttribute("errors", bindingResult);
//            log.info("errors = {}", bindingResult);
//            return "validation/v2/addForm";
//        }
//
//        // 에러가 없다면 ? -> 성공 로직
//        Item saveItem = itemRepository.save(item);
//        redirectAttributes.addAttribute("itemId", saveItem.getId());
//        redirectAttributes.addAttribute("status", true);
//        return "redirect:/validation/v2/items/{itemId}";
//    }

//    @PostMapping("/add")
//    public String addItemV3(@ModelAttribute Item item,
//                            BindingResult bindingResult,  // 추가 errors 역할을 해줌, 항상 ModelAttribute 뒤에 와야한다. (순서 중요)
//                            RedirectAttributes redirectAttributes) {
//
//        // 필드 검증 로직, errors 에 담긴 내용을 뷰에서 보여줘야 한다. (빨간색으로)
//        // addError() 인자로 rejectedValue 를 넣어주면 해당 값이 검증에 실패했을 때, 사라지지 않고 뷰에 다시 보여진다.
//        if (!StringUtils.hasText(item.getItemName())) {
//            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
//                    false, new String[]{"required.item.itemName"}, null, null));
//        }
//
//        if (item.getPrice() == null || item.getPrice() < 1_000 || item.getPrice() > 1_000_000) {
//            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
//                    false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
//        }
//
//        if (item.getQuantity() == null || item.getQuantity() > 9_999) {
//            bindingResult.addError(new FieldError("item", "quantity",
//                    item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
//        }
//
//        // 특정 필드가 아닌 복합 룰 검증
//        if (item.getPrice() != null && item.getQuantity() != null) {
//            int resultPrice = item.getPrice() * item.getQuantity();
//            if (resultPrice < 10_000) {
//                // 특정 필드가 없기 때문에 new FieldError 말고 ObjectError 사용 (글로벌 오류)
//                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
//            }
//        }
//
//        // 검증에 실패하면 다시 입력 폼으로 이동해야한다.
//        if (bindingResult.hasErrors()) {
//            // bindingResult 는 자동으로 View 로 넘어가서 따로 model 에 안 담아도 된다.
//            // model.addAttribute("errors", bindingResult);
//            log.info("errors = {}", bindingResult);
//            return "validation/v2/addForm";
//        }
//
//        // 에러가 없다면 ? -> 성공 로직
//        Item saveItem = itemRepository.save(item);
//        redirectAttributes.addAttribute("itemId", saveItem.getId());
//        redirectAttributes.addAttribute("status", true);
//        return "redirect:/validation/v2/items/{itemId}";
//    }

    // 컨트롤러가 호출되기 전 검증을 거친 뒤 에러 값을 BindingResult 에 담아준 뒤 컨트롤러가 호출된다.
    // 에러가 없으면 BindingResult 는 비어있는 것.
//    @PostMapping("/add")
//    public String addItemV2(@ModelAttribute Item item,
//                            BindingResult bindingResult,  // 추가 errors 역할을 해줌, 항상 ModelAttribute 뒤에 와야한다. (순서 중요)
//                            RedirectAttributes redirectAttributes) {
//
//        // 필드 검증 로직, errors 에 담긴 내용을 뷰에서 보여줘야 한다. (빨간색으로)
//        // addError() 인자로 rejectedValue 를 넣어주면 해당 값이 검증에 실패했을 때, 사라지지 않고 뷰에 다시 보여진다.
//        if (!StringUtils.hasText(item.getItemName())) {
//            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
//                    false, null, null, "상품 이름은 필수입니다."));
//        }
//
//        if (item.getPrice() == null || item.getPrice() < 1_000 || item.getPrice() > 1_000_000) {
//            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
//                    false, null, null, "가격은 1,000원에서 1,000,000 까지 허용합니다."));
//        }
//
//        if (item.getQuantity() == null || item.getQuantity() > 9_999) {
//            bindingResult.addError(new FieldError("item", "quantity",
//                    item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용됩니다."));
//        }
//
//        // 특정 필드가 아닌 복합 룰 검증
//        if (item.getPrice() != null && item.getQuantity() != null) {
//            int resultPrice = item.getPrice() * item.getQuantity();
//            if (resultPrice < 10_000) {
//                // 특정 필드가 없기 때문에 new FieldError 말고 ObjectError 사용 (글로벌 오류)
//                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 = " + resultPrice));
//            }
//        }
//
//        // 검증에 실패하면 다시 입력 폼으로 이동해야한다.
//        if (bindingResult.hasErrors()) {
//            // bindingResult 는 자동으로 View 로 넘어가서 따로 model 에 안 담아도 된다.
//            // model.addAttribute("errors", bindingResult);
//            log.info("errors = {}", bindingResult);
//            return "validation/v2/addForm";
//        }
//
//        // 에러가 없다면 ? -> 성공 로직
//        Item saveItem = itemRepository.save(item);
//        redirectAttributes.addAttribute("itemId", saveItem.getId());
//        redirectAttributes.addAttribute("status", true);
//        return "redirect:/validation/v2/items/{itemId}";
//    }

//    @PostMapping("/add")
//    public String addItemV1(@ModelAttribute Item item,
//                          BindingResult bindingResult,  // 추가 errors 역할을 해줌, 항상 ModelAttribute 뒤에 와야한다. (순서 중요)
//                          RedirectAttributes redirectAttributes
//                          ) {
//
//
//        // 필드 검증 로직, errors 에 담긴 내용을 뷰에서 보여줘야 한다. (빨간색으로)
//        if (!StringUtils.hasText(item.getItemName())) {
//            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
//        }
//
//        if (item.getPrice() == null || item.getPrice() < 1_000 || item.getPrice() > 1_000_000) {
//            bindingResult.addError(new FieldError("item", "price", "가격은 1,000원에서 1,000,000 까지 허용합니다."));
//        }
//
//        if (item.getQuantity() == null || item.getQuantity() > 9_999) {
//            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용됩니다."));
//        }
//
//        // 특정 필드가 아닌 복합 룰 검증
//        if (item.getPrice() != null && item.getQuantity() != null) {
//            int resultPrice = item.getPrice() * item.getQuantity();
//            if (resultPrice < 10_000) {
//                // 특정 필드가 없기 때문에 new FieldError 말고 ObjectError 사용 (글로벌 오류)
//                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 = " + resultPrice));
//            }
//        }
//
//        // 검증에 실패하면 다시 입력 폼으로 이동해야한다.
//        if (bindingResult.hasErrors()) {
//            // bindingResult 는 자동으로 View 로 넘어가서 따로 model 에 안 담아도 된다.
//            // model.addAttribute("errors", bindingResult);
//            log.info("errors = {}", bindingResult);
//            return "validation/v2/addForm";
//        }
//
//        // 에러가 없다면 ? -> 성공 로직
//        Item saveItem = itemRepository.save(item);
//        redirectAttributes.addAttribute("itemId", saveItem.getId());
//        redirectAttributes.addAttribute("status", true);
//        return "redirect:/validation/v2/items/{itemId}";
//    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }
}