CREATE DATABASE chatdata_b; 
USE chatdata_b; 
CREATE TABLE users ( 
username VARCHAR(50) PRIMARY KEY, 
password VARCHAR(50) NOT NULL 
); 
CREATE TABLE messages ( 
id INT AUTO INCREMENT PRIMARY KEY, 
sender VARCHAR(50) NOT NULL, 
recipient VARCHAR(50) NOT NULL, 
message TEXT NOT NULL 
);