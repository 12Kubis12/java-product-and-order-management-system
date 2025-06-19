create sequence order_id_seq start with 1 increment by 1;
create sequence product_id_seq start with 1 increment by 1;
create sequence order_product_id_seq start with 1 increment by 1;

-- id, paid, created_at, updated_at;
--INSERT INTO orders VALUES
--(next value for order_id_seq, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(next value for order_id_seq, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(next value for order_id_seq, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(next value for order_id_seq, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- id, name, description, amount, price;
--INSERT INTO product VALUES
--(next value for product_id_seq, 'First product', 'Description of first product', 5L, 1.5),
--(next value for product_id_seq, 'Second product', 'Description of second product', 1L, 2.0),
--(next value for product_id_seq, 'Third product', 'Description of third product', 3L, 0.8),
--(next value for product_id_seq, 'Fourth product', 'Description of fourth product', 6L, 2.2);

-- id, order_id, product_id, amount;
--INSERT INTO order_product VALUES
--(next value for order_product_id_seq, 1, 1, 1L),
--(next value for order_product_id_seq, 1, 2, 1L),
--(next value for order_product_id_seq, 1, 3, 1L),
--(next value for order_product_id_seq, 1, 4, 1L),
--(next value for order_product_id_seq, 2, 1, 2L),
--(next value for order_product_id_seq, 2, 2, 1L),
--(next value for order_product_id_seq, 2, 3, 1L),
--(next value for order_product_id_seq, 3, 1, 1L),
--(next value for order_product_id_seq, 3, 4, 1L),
--(next value for order_product_id_seq, 4, 4, 2L);
