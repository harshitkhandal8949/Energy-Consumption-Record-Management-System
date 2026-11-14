
CREATE DATABASE IF NOT EXISTS energy_management;

USE energy_management;

CREATE TABLE IF NOT EXISTS energy_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    consumer_name VARCHAR(100) NOT NULL,
    meter_number VARCHAR(50) NOT NULL,
    record_month VARCHAR(30) NOT NULL,
    units_consumed DECIMAL(10,2) NOT NULL,
    rate_per_unit DECIMAL(10,2) NOT NULL,
    total_cost DECIMAL(12,2) NOT NULL
);

SHOW TABLES;
SELECT * From energy_records;
