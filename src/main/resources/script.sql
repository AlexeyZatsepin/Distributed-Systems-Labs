CREATE DATABASE flydb;

CREATE DATABASE hoteldb;

CREATE TABLE flight_booking (
  ID INT PRIMARY KEY     NOT NULL,
  NAME           TEXT    NOT NULL,
  NUMBER         TEXT     NOT NULL,
  WHANCE         TEXT     NOT NULL,
  DEST           TEXT     NOT NULL,
  DATE           DATE     NOT NULL
);

CREATE TABLE hotel_booking (
  ID INT PRIMARY KEY     NOT NULL,
  NAME           TEXT    NOT NULL,
  HOTEL          TEXT     NOT NULL,
  ARRIVAL        DATE     NOT NULL,
  DEPARTURE      DATE     NOT NULL
);

INSERT INTO flight_booking VALUES (9,'1','1','1','1',CURRENT_TIMESTAMP);
INSERT INTO hotel_booking VALUES (9,'1','1',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);