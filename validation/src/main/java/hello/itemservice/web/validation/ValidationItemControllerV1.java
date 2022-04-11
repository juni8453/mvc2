package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v1/items")
@RequiredArgsConstructor
public class ValidationItemControllerV1 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v1/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v1/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        // new Item() 해서 빈 값을 넘긴 이유는 검증 실패시 입력한 값들을 재사용할 수 있도록 하기 위함이다.
        // 검증 실패시 다시 이 컨트롤러를 타게 되는데, 이때 @ModelAttribute Item item 해서 들어간 값이 이미 있다.
        // 따라서 다시 이 컨트롤러를 탔을 때, 적어논 모든 값들이 사라지지 않고 뷰에 남게된다.
        model.addAttribute("item", new Item());
        return "validation/v1/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        // 검증에 실패하면 검증 오류 결과를 보관해야 한다.
        Map<String, String> errors = new HashMap<>();

        // 필드 검증 로직, errors 에 담긴 내용을 뷰에서 보여줘야 한다. (빨간색으로)
        if (!StringUtils.hasText(item.getItemName())) {
            errors.put("itemName", "상품 이름은 필수입니다.");
        }

        if (item.getPrice() == null || item.getPrice() < 1_000 || item.getPrice() > 1_000_000) {
            errors.put("price", "가격은 1,000원에서 1,000,000 까지 허용합니다.");
        }

        if (item.getQuantity() == null || item.getQuantity() > 9_999) {
            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10_000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 = " + resultPrice);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로 이동해야한다.
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            log.info("errors = {}", errors);
            return "validation/v1/addForm";
        }

        // 에러가 없다면 ? -> 성공 로직
        Item saveItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", saveItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v1/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v1/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v1/items/{itemId}";
    }

}

