DROP TABLE IF EXISTS order;
CREATE TABLE order (
  id bigint NOT NULL AUTO_INCREMENT,
  paid varchar(10) NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(45) NOT NULL,
  description varchar(160),
  amount bigint NOT NULL,
  price double NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS order_product;
CREATE TABLE order_product (
  order_id bigint NOT NULL,
  product_id bigint NOT NULL,
  amount bigint NOT NULL,
  FOREIGN KEY (order_id) REFERENCES order(id),
  FOREIGN KEY (product_id) REFERENCES product(id)
);