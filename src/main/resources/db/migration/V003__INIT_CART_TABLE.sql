CREATE TABLE cart(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY  KEY,
    created_date TIMESTAMP NOT NULL,
    quantity INTEGER NOT NULL,
    product_id BIGINT REFERENCES product(id),
    user_id BIGINT REFERENCES _user(id)
);