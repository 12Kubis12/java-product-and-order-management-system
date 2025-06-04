package sk.streetofcode.productordermanagement;

import lombok.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void addProduct() {
        addProductInternal();
    }

    @Test
    void addProduct201Response() {
        addProductInternal("name", "description", 1L, 1.0, HttpStatus.CREATED);
    }

    @Test
    void getAllProducts() {
        addProductInternal();
        addProductInternal();
        addProductInternal();

        final ResponseEntity<List<TestProductResponse>> getProductsResponse = restTemplate.exchange(
                "/product",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, getProductsResponse.getStatusCode());
        Assertions.assertNotNull(getProductsResponse.getBody());
        Assertions.assertEquals(3, getProductsResponse.getBody().size());
    }

    @Test
    void getProduct() {
        final TestProductResponse product = addProductInternal();
        final ResponseEntity<TestProductResponse> responseEntity = restTemplate.exchange(
                "/product/" + product.getId(),
                HttpMethod.GET,
                null,
                TestProductResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(product, responseEntity.getBody());
    }

    @Test
    void getProduct404() {
        final ResponseEntity<TestProductResponse> responseEntity = restTemplate.exchange(
                "/product/1",
                HttpMethod.GET,
                null,
                TestProductResponse.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void updateProduct() {
        final TestProductResponse product = addProductInternal();
        final TestProductRequest request = new TestProductRequest();
        request.setName("new name");
        request.setDescription("new description");

        final HttpEntity<TestProductRequest> httpEntity = new HttpEntity<>(request);
        final ResponseEntity<TestProductResponse> responseEntity = restTemplate.exchange(
                "/product/" + product.getId(),
                HttpMethod.PUT,
                httpEntity,
                TestProductResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(request.getName(), responseEntity.getBody().getName());
        Assertions.assertEquals(request.getDescription(), responseEntity.getBody().getDescription());
    }

    @Test
    void updateProduct404() {
        final TestProductRequest request = new TestProductRequest();
        request.setName("new name");
        request.setDescription("new description");

        final HttpEntity<TestProductRequest> httpEntity = new HttpEntity<>(request);
        final ResponseEntity<TestProductResponse> responseEntity = restTemplate.exchange(
                "/product/1",
                HttpMethod.PUT,
                httpEntity,
                TestProductResponse.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void deleteProduct() {
        final TestProductResponse product = addProductInternal();
        final ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "/product/" + product.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final ResponseEntity<TestProductResponse> responseEntity2 = restTemplate.exchange(
                "/product/" + product.getId(),
                HttpMethod.GET,
                null,
                TestProductResponse.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity2.getStatusCode());
    }

    @Test
    void deleteProduct404() {
        final ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "/product/1",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void getProductAmount() {
        final TestProductResponse product = addProductInternal();
        final ResponseEntity<Amount> responseEntity = restTemplate.exchange(
                "/product/" + product.getId() + "/amount",
                HttpMethod.GET,
                null,
                Amount.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(product.getAmount(), responseEntity.getBody().getAmount());
    }

    @Test
    void getProductAmount404() {
        final ResponseEntity<Amount> responseEntity = restTemplate.exchange(
                "/product/1/amount",
                HttpMethod.GET,
                null,
                Amount.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void addProductAmount() {
        final TestProductResponse product = addProductInternal();
        final Amount amount = new Amount();
        amount.setAmount(10L);
        final HttpEntity<Amount> httpEntity = new HttpEntity<>(amount);
        final ResponseEntity<Amount> responseEntity = restTemplate.exchange(
                "/product/" + product.getId() + "/amount",
                HttpMethod.POST,
                httpEntity,
                Amount.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(product.getAmount() + amount.getAmount(), responseEntity.getBody().getAmount());
    }

    @Test
    void addProductAmount404() {
        final Amount amount = new Amount();
        amount.setAmount(10L);
        final HttpEntity<Amount> httpEntity = new HttpEntity<>(amount);
        final ResponseEntity<Amount> responseEntity = restTemplate.exchange(
                "/product/1/amount",
                HttpMethod.POST,
                httpEntity,
                Amount.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void createOrder() {
        createOrderInternal();
    }

    @Test
    void getOrderById() {
        final TestOrderResponse order = createOrderInternal();
        final ResponseEntity<TestOrderResponse> responseEntity = restTemplate.exchange(
                "/order/" + order.getId(),
                HttpMethod.GET,
                null,
                TestOrderResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(order, responseEntity.getBody());
    }

    @Test
    void getOrderById404() {
        final ResponseEntity<TestOrderResponse> responseEntity = restTemplate.exchange(
                "/order/1",
                HttpMethod.GET,
                null,
                TestOrderResponse.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void deleteOrder() {
        final TestOrderResponse order = createOrderInternal();
        final ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "/order/" + order.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final ResponseEntity<TestOrderResponse> responseEntity2 = restTemplate.exchange(
                "/order/" + order.getId(),
                HttpMethod.GET,
                null,
                TestOrderResponse.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity2.getStatusCode());
    }

    @Test
    void deleteOrder404() {
        final ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "/order/1",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void addProductToOrder() {
        final TestOrderResponse order = createOrderInternal();
        final TestProductResponse product = addProductInternal(10);
        Assertions.assertEquals(order.getShoppingList().size(), 0);
        final TestOrderResponse updatedOrder = addProductToOrderInternal(order.getId(), product.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder.getShoppingList().size(), 1);
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getProductId(), product.getId());
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getAmount(), 5);
    }

    @Test
    void addProductToOrderRemovesFromStorage() {
        final TestOrderResponse order = createOrderInternal();
        final TestProductResponse product = addProductInternal(10);
        Assertions.assertEquals(order.getShoppingList().size(), 0);
        final TestOrderResponse updatedOrder = addProductToOrderInternal(order.getId(), product.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder.getShoppingList().size(), 1);
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getProductId(), product.getId());
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getAmount(), 5);
        final ResponseEntity<Amount> responseEntity = restTemplate.exchange(
                "/product/" + product.getId() + "/amount",
                HttpMethod.GET,
                null,
                Amount.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(5, responseEntity.getBody().getAmount());
    }

    @Test
    void addProductToOrderNotEnoughProduct() {
        final TestOrderResponse order = createOrderInternal();
        final TestProductResponse product = addProductInternal(10);
        Assertions.assertEquals(order.getShoppingList().size(), 0);
        addProductToOrderInternal(order.getId(), product.getId(), 15, HttpStatus.BAD_REQUEST);

        // get order again and check if it is still empty
        final ResponseEntity<TestOrderResponse> responseEntity = restTemplate.exchange(
                "/order/" + order.getId(),
                HttpMethod.GET,
                null,
                TestOrderResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(0, responseEntity.getBody().getShoppingList().size());

        // check if product amount is still 10
        final ResponseEntity<Amount> responseEntity2 = restTemplate.exchange(
                "/product/" + product.getId() + "/amount",
                HttpMethod.GET,
                null,
                Amount.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        Assertions.assertNotNull(responseEntity2.getBody());
        Assertions.assertEquals(10, responseEntity2.getBody().getAmount());
    }

    @Test
    void addProductToOrderProductNotFound() {
        final TestOrderResponse order = createOrderInternal();
        addProductToOrderInternal(order.getId(), 1, 15, HttpStatus.NOT_FOUND);
    }

    @Test
    void addProductToOrderOrderNotFound() {
        addProductToOrderInternal(1, 1, 15, HttpStatus.NOT_FOUND);
    }

    @Test
    void addProductToOrderTwice() {
        final TestOrderResponse order = createOrderInternal();
        final TestProductResponse product = addProductInternal(10);
        Assertions.assertEquals(order.getShoppingList().size(), 0);
        final TestOrderResponse updatedOrder = addProductToOrderInternal(order.getId(), product.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder.getShoppingList().size(), 1);
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getProductId(), product.getId());
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getAmount(), 5);
        final TestOrderResponse updatedOrder2 = addProductToOrderInternal(order.getId(), product.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder2.getShoppingList().size(), 1);
        Assertions.assertEquals(updatedOrder2.getShoppingList().get(0).getProductId(), product.getId());
        Assertions.assertEquals(updatedOrder2.getShoppingList().get(0).getAmount(), 10);
    }

    @Test
    void payForOrder() {
        final TestOrderResponse order = createOrderInternal();
        final TestProductResponse product1 = addProductInternal(10, 40.0);

        // add first product
        Assertions.assertEquals(order.getShoppingList().size(), 0);
        final TestOrderResponse updatedOrder = addProductToOrderInternal(order.getId(), product1.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder.getShoppingList().size(), 1);
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getProductId(), product1.getId());
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getAmount(), 5);

        // add second product
        final TestProductResponse product2 = addProductInternal(10, 50.0);
        final TestOrderResponse updatedOrder2 = addProductToOrderInternal(order.getId(), product2.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder2.getShoppingList().size(), 2);
        // find first product
        final TestOrderEntry entry1 = updatedOrder2.getShoppingList().stream().filter(e -> e.getProductId() == product1.getId()).findFirst().orElseThrow();
        Assertions.assertEquals(entry1.getAmount(), 5);
        Assertions.assertEquals(entry1.getProductId(), product1.getId());
        // find second product
        final TestOrderEntry entry2 = updatedOrder2.getShoppingList().stream().filter(e -> e.getProductId() == product2.getId()).findFirst().orElseThrow();
        Assertions.assertEquals(entry2.getAmount(), 5);
        Assertions.assertEquals(entry2.getProductId(), product2.getId());

        // pay for order
        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/order/" + order.getId() + "/pay",
                HttpMethod.POST,
                null,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        final String expectedPrice = String.format("%.1f", 5 * 40.0 + 5 * 50.0);
        Assertions.assertEquals(expectedPrice, responseEntity.getBody());

        // check that order is paid
        final ResponseEntity<TestOrderResponse> responseEntity2 = restTemplate.exchange(
                "/order/" + order.getId(),
                HttpMethod.GET,
                null,
                TestOrderResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        Assertions.assertNotNull(responseEntity2.getBody());
        Assertions.assertTrue(responseEntity2.getBody().isPaid());
    }

    @Test
    void payForOrder404() {
        final ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/order/1/pay",
                HttpMethod.POST,
                null,
                String.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void payForOrderTwice() {
        final TestOrderResponse order = createOrderInternal();
        final TestProductResponse product = addProductInternal(10, 40.0);

        Assertions.assertEquals(order.getShoppingList().size(), 0);
        final TestOrderResponse updatedOrder = addProductToOrderInternal(order.getId(), product.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder.getShoppingList().size(), 1);
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getProductId(), product.getId());
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getAmount(), 5);

        // pay for order
        final ResponseEntity<String> payOrderResponse = restTemplate.exchange(
                "/order/" + order.getId() + "/pay",
                HttpMethod.POST,
                null,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK, payOrderResponse.getStatusCode());
        Assertions.assertNotNull(payOrderResponse.getBody());
        final String expectedPrice = String.format("%.1f", 5 * 40.0);
        Assertions.assertEquals(expectedPrice, payOrderResponse.getBody());

        // check that order is paid
        final ResponseEntity<TestOrderResponse> getOrderResponse = restTemplate.exchange(
                "/order/" + order.getId(),
                HttpMethod.GET,
                null,
                TestOrderResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, getOrderResponse.getStatusCode());
        Assertions.assertNotNull(getOrderResponse.getBody());
        Assertions.assertTrue(getOrderResponse.getBody().isPaid());

        // pay for order again
        final ResponseEntity<String> payForOrderAgainResponse = restTemplate.exchange(
                "/order/" + order.getId() + "/pay",
                HttpMethod.POST,
                null,
                String.class
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, payForOrderAgainResponse.getStatusCode());
    }

    @Test
    void addProductToPaidOrder() {
        final TestOrderResponse order = createOrderInternal();
        final TestProductResponse product = addProductInternal(10, 40.0);

        Assertions.assertEquals(order.getShoppingList().size(), 0);
        final TestOrderResponse updatedOrder = addProductToOrderInternal(order.getId(), product.getId(), 5, HttpStatus.OK);
        Assertions.assertEquals(updatedOrder.getShoppingList().size(), 1);
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getProductId(), product.getId());
        Assertions.assertEquals(updatedOrder.getShoppingList().get(0).getAmount(), 5);

        // pay for order
        final ResponseEntity<String> payOrderResponse = restTemplate.exchange(
                "/order/" + order.getId() + "/pay",
                HttpMethod.POST,
                null,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK, payOrderResponse.getStatusCode());
        Assertions.assertNotNull(payOrderResponse.getBody());
        final String expectedPrice = String.format("%.1f", 5 * 40.0);
        Assertions.assertEquals(expectedPrice, payOrderResponse.getBody());

        // check that order is paid
        final ResponseEntity<TestOrderResponse> getOrderResponse = restTemplate.exchange(
                "/order/" + order.getId(),
                HttpMethod.GET,
                null,
                TestOrderResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, getOrderResponse.getStatusCode());
        Assertions.assertNotNull(getOrderResponse.getBody());
        Assertions.assertTrue(getOrderResponse.getBody().isPaid());

        // add product to paid order
        addProductToOrderInternal(order.getId(), product.getId(), 5, HttpStatus.BAD_REQUEST);
    }

    private TestOrderResponse createOrderInternal() {
        final ResponseEntity<TestOrderResponse> responseEntity = restTemplate.exchange(
                "/order",
                HttpMethod.POST,
                null,
                TestOrderResponse.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertNotNull(responseEntity.getBody().getShoppingList());
        Assertions.assertEquals(0, responseEntity.getBody().getShoppingList().size());
        Assertions.assertFalse(responseEntity.getBody().isPaid());
        return responseEntity.getBody();
    }

    private TestOrderResponse addProductToOrderInternal(long orderId, long productId, long amount, HttpStatus expectedStatus) {
        final TestOrderEntry entry = new TestOrderEntry();
        entry.setProductId(productId);
        entry.setAmount(amount);
        final HttpEntity<TestOrderEntry> httpEntity = new HttpEntity<>(entry);
        final ResponseEntity<TestOrderResponse> addOrderResponse = restTemplate.exchange(
                "/order/" + orderId + "/add",
                HttpMethod.POST,
                httpEntity,
                TestOrderResponse.class
        );
        Assertions.assertEquals(expectedStatus, addOrderResponse.getStatusCode());

        if (expectedStatus != HttpStatus.OK) {
            return null;
        }

        Assertions.assertNotNull(addOrderResponse.getBody());
        return addOrderResponse.getBody();
    }

    private TestProductResponse addProductInternal() {
        return addProductInternal("name", "description", 1L, 1.0, HttpStatus.CREATED);
    }

    private TestProductResponse addProductInternal(long amount) {
        return addProductInternal("name", "description", amount, 1.0, HttpStatus.CREATED);
    }

    private TestProductResponse addProductInternal(long amount, double price) {
        return addProductInternal("name", "description", amount, price, HttpStatus.CREATED);
    }


    private TestProductResponse addProductInternal(String name, String description, Long amount, double price, HttpStatus expectedStatus) {
        final TestProductRequest product = new TestProductRequest();
        product.setName(name);
        product.setDescription(description);
        product.setAmount(amount);
        product.setPrice(price);

        final HttpEntity<TestProductRequest> request = new HttpEntity<>(product);

        ResponseEntity<TestProductResponse> addProductResponse = restTemplate.exchange(
                "/product",
                HttpMethod.POST,
                request,
                TestProductResponse.class
        );

        Assertions.assertEquals(expectedStatus, addProductResponse.getStatusCode());

        final TestProductResponse returnedProduct = addProductResponse.getBody();
        Assertions.assertNotNull(returnedProduct);
        Assertions.assertEquals(product.getName(), returnedProduct.getName());
        Assertions.assertEquals(product.getDescription(), returnedProduct.getDescription());
        Assertions.assertEquals(product.getAmount(), returnedProduct.getAmount());
        Assertions.assertEquals(product.getPrice(), returnedProduct.getPrice());
        return returnedProduct;
    }


    @Getter
    @Setter
    private static class Amount {
        private long amount;
    }

    @Getter
    @Setter
    private static class TestProductRequest extends Amount {
        private String name;
        private String description;
        private double price;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    private static class TestProductResponse extends TestProductRequest {
        private long id;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    private static class TestOrderResponse {
        private long id;
        private List<TestOrderEntry> shoppingList;
        private boolean paid;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TestOrderEntry {
        private Long productId;
        private Long amount;
    }
}
