CREATE DATABASE IF NOT EXISTS hotel_ibatti;
CREATE USER IF NOT EXISTS 'hotel_app'@'localhost' IDENTIFIED BY '271315';
GRANT ALL PRIVILEGES ON hotel_ibatti.* TO 'hotel_app'@'localhost';
FLUSH PRIVILEGES;