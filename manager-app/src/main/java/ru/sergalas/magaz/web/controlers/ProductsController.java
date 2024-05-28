package ru.sergalas.magaz.web.controlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sergalas.magaz.web.controlers.payloads.CreateProductPayload;
import ru.sergalas.magaz.web.entity.Product;
import ru.sergalas.magaz.web.exeption.BadRequestException;
import ru.sergalas.magaz.web.services.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {
    private final ProductService productService;

    @GetMapping(value = "list")
    public String getProductList(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("products", this.productService.findAllProducts(filter));
        model.addAttribute("filter", filter);
        return "catalogue/products/list";
    }

    @GetMapping("create")
    public String getNewProductPage() {
       return "catalogue/products/new";
    }

    @PostMapping("create")
    public String createProduct(
            CreateProductPayload payload,
            BindingResult bindingResult,
            Model model
    ) {
        try{
            Product product = this.productService.createProduct(payload.title(), payload.details());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload",payload);
            model.addAttribute("errors", exception.getErrors() );
            return "catalogue/products/new";
        }
    }
}
