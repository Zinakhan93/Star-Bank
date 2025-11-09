package com.Star_Bank.сontroller;

import com.Star_Bank.model.CreateRuleRequest;
import com.Star_Bank.model.RuleListResponse;
import com.Star_Bank.model.RuleResponse;
import com.Star_Bank.servise.DynamicRuleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для управления динамическими правилами рекомендаций
 * Предоставляет API для создания, получения и удаления правил
 */
@RestController
@RequestMapping("/rule")
public class DynamicRuleController {
    private final DynamicRuleService dynamicRuleService;

    @Autowired
    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    /**
     * Создает новое динамическое правило рекомендаций
     * @param request DTO с данными правила
     * @return ResponseEntity с созданным правилом
     */
    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody CreateRuleRequest request) {
        try {
            RuleResponse response = dynamicRuleService.createRule(request);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid rule format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating rule: " + e.getMessage());
        }
    }

    /**
     * Получает все динамические правила
     * @return ResponseEntity со списком всех правил
     */
    @GetMapping
    public ResponseEntity<RuleListResponse> getAllRules() {
        RuleListResponse response = dynamicRuleService.getAllRules();
        return ResponseEntity.ok(response);
    }

    /**
     * Удаляет правило по ID продукта
     * @param productId ID продукта правила для удаления
     * @return ResponseEntity со статусом 204 No Content при успехе
     */
    @DeleteMapping("/{product_id}")
    public ResponseEntity<Void> deleteRule(@PathVariable("product_id") String productId) {
        boolean deleted = dynamicRuleService.deleteRule(productId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
