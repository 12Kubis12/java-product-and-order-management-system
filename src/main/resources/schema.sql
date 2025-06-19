DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id bigint NOT NULL AUTO_INCREMENT,
  paid varchar(10) NOT NULL,
  created_at datetime NOT NULL,
  updated_at datetime NOT NULL,
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
  id bigint NOT NULL AUTO_INCREMENT,
  order_id bigint NOT NULL,
  product_id bigint NOT NULL,
  amount bigint NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES product(id),
  UNIQUE (order_id, product_id)
);