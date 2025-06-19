package parentPackage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import parentPackage.api.ProductService;
import parentPackage.dto.request.AddProductRequest;
import parentPackage.dto.request.ProductAmountRequest;
import parentPackage.dto.request.UpdateProductRequest;
import parentPackage.dto.response.ProductAmountResponse;
import parentPackage.dto.response.ProductResponse;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok().body(this.productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.productService.get(id));
    }


    @PostMapping
    public ResponseEntity<ProductResponse> add(@RequestBody AddProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.add(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable("id") long id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok().body(this.productService.edit(id, request));
    }

    @GetMapping("/{id}/amount")
    public ResponseEntity<ProductAmountResponse> getAmount(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.productService.getAmount(id));
    }

    @PostMapping("/{id}/amount")
    public ResponseEntity<ProductAmountResponse> addAmount(@PathVariable("id") long id, @RequestBody ProductAmountRequest request) {
        return ResponseEntity.ok().body(this.productService.updateAmount(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        this.productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
