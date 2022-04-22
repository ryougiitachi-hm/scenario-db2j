CREATE DATABASE maxwell ;
CREATE USER 'maxwell'@'%' IDENTIFIED BY '123456';

GRANT ALL ON maxwell.* TO 'maxwell'@'%';
GRANT SELECT ,REPLICATION SLAVE, REPLICATION CLIENT  ON *.* TO maxwell@'%';

