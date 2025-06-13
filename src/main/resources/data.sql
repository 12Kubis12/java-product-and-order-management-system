create sequence order_id_seq start with 1 increment by 1;
create sequence product_id_seq start with 1 increment by 1;

-- id, paid;
--INSERT INTO order VALUES
--(next value for order_id_seq, TRUE),
--(next value for order_id_seq, TRUE),
--(next value for order_id_seq, FALSE),
--(next value for order_id_seq, FALSE);

-- id, name, description, amount, price;
--INSERT INTO product VALUES
--(next value for product_id_seq, 'First product', 'Description of first product', 5L, 1.5),
--(next value for product_id_seq, 'Second product', 'Description of second product', 1L, 2.0),
--(next value for product_id_seq, 'Third product', 'Description of third product', 3L, 0.8),
--(next value for product_id_seq, 'Fourth product', 'Description of fourth product', 6L, 2.2);

-- order_id, product_id, amount;
--INSERT INTO order_product VALUES
--(1, 1, 1L),
--(1, 2, 1L),
--(1, 3, 1L),
--(1, 4, 1L),
--(2, 1, 2L),
--(2, 2, 1L),
--(2, 3, 1L),
--(3, 1, 1L),
--(3, 4, 1L),
--(4, 4, 2L);
