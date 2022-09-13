DROP TABLE IF EXISTS CUSTOMER;
CREATE TABLE customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL
);

CREATE TABLE customer_transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    amount NUMERIC(20, 2) NOT NULL,
    points INT,
    create_date TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

INSERT INTO customer(name, surname) VALUES ('Joe', 'Biden');
INSERT INTO customer(name, surname) VALUES ('Andrzej', 'Duda');
INSERT INTO customer(name, surname) VALUES ('Donald', 'Tusk');

INSERT INTO customer_transaction(customer_id, amount, points, create_date)
VALUES (1, 60, 20, parsedatetime('08-09-2022 18:47:52.69', 'dd-MM-yyyy HH:mm:ss.SS'));
INSERT INTO customer_transaction(customer_id, amount, points, create_date)
VALUES (1, 60, 20, parsedatetime('01-08-2022 18:47:52.69', 'dd-MM-yyyy HH:mm:ss.SS'));
INSERT INTO customer_transaction(customer_id, amount, points, create_date)
VALUES (1, 60, 20, parsedatetime('01-07-2022 18:47:52.69', 'dd-MM-yyyy HH:mm:ss.SS'));
INSERT INTO customer_transaction(customer_id, amount, points, create_date)
VALUES (1, 60, 20, parsedatetime('01-06-2022 18:47:52.69', 'dd-MM-yyyy HH:mm:ss.SS'));
INSERT INTO customer_transaction(customer_id, amount, points, create_date)
VALUES (1, 60, 20, parsedatetime('01-05-2022 18:47:52.69', 'dd-MM-yyyy HH:mm:ss.SS'));