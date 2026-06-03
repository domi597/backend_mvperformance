-- ============================================================
-- Clean all tables (child → parent order) and reset sequences
-- ============================================================
TRUNCATE TABLE
    opening_hours,
    contact_info,
    reviews,
    appointments,
    offer_services,
    offers,
    services,
    timeslots,
    vehicles,
    users
    RESTART IDENTITY CASCADE;


-- 1. USERS
INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Max', 'Mustermann', 'admin@mwperformance.at', '$2y$10$..iVgU.UYVDvYuW8NLokEONtpdp/Rtck/qAnzFGq.bksPgOId9zwq', '+43 664 1234567', 'Werkstattstraße 1', '8010', 'Graz', 'ADMIN', CURRENT_TIMESTAMP);

INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Thomas', 'Kauer', 'thomas.kauer@gmail.com', '$2y$10$Vp.8qEcQlOBNYBHXAZi0xeS6V5Fr9giPo249L51nDVN8wqG8nERQG', '+43 699 1111111', 'Hauptstraße 5', '8010', 'Graz', 'CUSTOMER', CURRENT_TIMESTAMP);

INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Maria', 'Schreiner', 'maria.schreiner@gmail.com', '$2y$10$1X8sYSsI1tEB6tidBUqsfeSqlVdvABnBMjIfPRB7pTzUz154.O.EG', '+43 699 2222222', 'Schillerstraße 12', '8010', 'Graz', 'CUSTOMER', CURRENT_TIMESTAMP);

INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Stefan', 'Bauer', 'stefan.bauer@gmail.com', '$2y$10$G7dfXYviNn6Ll/NvLX1XZeTH1TbS9waUZEgijrNERwHzmEDFUGAMy', '+43 699 3333333', 'Mozartgasse 3', '8430', 'Leibnitz', 'CUSTOMER', CURRENT_TIMESTAMP);

-- 2. VEHICLES
INSERT INTO vehicles (user_id, brand, model, build_year, license_plate)
VALUES (2, 'VW', 'Golf', 2019, 'GU-12345');

INSERT INTO vehicles (user_id, brand, model, build_year, license_plate)
VALUES (3, 'BMW', 'X3', 2021, 'GU-67890');

INSERT INTO vehicles (user_id, brand, model, build_year, license_plate)
VALUES (4, 'Audi', 'A4', 2017, 'LB-11111');

-- 3. SERVICES (icon wird separat per API hochgeladen)
INSERT INTO services (title, subtitle, price) VALUES ('Ölwechsel',       'Motoröl + Filter wechseln',          49.99);
INSERT INTO services (title, subtitle, price) VALUES ('Reifenwechsel',   'Sommer- / Winterreifen montieren',   39.99);
INSERT INTO services (title, subtitle, price) VALUES ('Bremsenservice',  'Bremsbeläge + Scheiben prüfen',      89.99);
INSERT INTO services (title, subtitle, price) VALUES ('HU / §57a',       'Hauptuntersuchung durchführen',     120.00);
INSERT INTO services (title, subtitle, price) VALUES ('Klimaservice',    'Kältemittel + Desinfektion + Check', 69.99);

-- 4. OFFERS
INSERT INTO offers (title, description, price, active, created_at)
VALUES ('Frühjahrs-Check', 'Ölwechsel + Bremsen + Reifencheck', 49.0, true, CURRENT_TIMESTAMP);

INSERT INTO offers (title, description, price, active, created_at)
VALUES ('Klimaservice', 'Kältemittel + Desinfektion + Check', 69.0, true, CURRENT_TIMESTAMP);

-- 5. OFFER_SERVICES
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 1);
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 2);
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 3);
INSERT INTO offer_services (offer_id, service_id) VALUES (2, 5);

-- 6. APPOINTMENTS
INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 4, 'Thomas Kauer',    'HU / §57a',      'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '1 day',   'NEU',           120.00, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 5, 'Maria Schreiner', 'Klimaservice',   'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '2 days',  'AUSSTEHEND',    69.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 4, 'Stefan Bauer',    'HU / §57a',      'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '3 days',  'BESTÄTIGT',     120.00, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 2, 'Thomas Kauer',    'Reifenwechsel',  'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '4 days',  'NEU',           79.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 1, 'Maria Schreiner', 'Ölwechsel',      'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '5 days',  'ABGELEHNT',     149.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 3, 'Stefan Bauer',    'Bremsenservice', 'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '6 days',  'NEU',           199.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 5, 'Thomas Kauer',    'Klimaservice',   'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '7 days',  'AUSSTEHEND',    69.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 3, 'Maria Schreiner', 'Bremsenservice', 'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '8 days',  'BESTÄTIGT',     199.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 1, 'Stefan Bauer',    'Ölwechsel',      'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '9 days',  'ABGESCHLOSSEN', 149.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 3, 'Thomas Kauer',    'Bremsenservice', 'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '10 days', 'NEU',           199.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 2, 'Maria Schreiner', 'Reifenwechsel',  'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '11 days', 'NEU',           79.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 5, 'Stefan Bauer',    'Klimaservice',   'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '12 days', 'AUSSTEHEND',    69.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 4, 'Thomas Kauer',    'HU / §57a',      'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '13 days', 'ABGELEHNT',     120.00, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 4, 'Maria Schreiner', 'HU / §57a',      'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '14 days', 'BESTÄTIGT',     120.00, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 2, 'Stefan Bauer',    'Reifenwechsel',  'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '15 days', 'NEU',           79.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 1, 'Thomas Kauer',    'Ölwechsel',      'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '16 days', 'ABGESCHLOSSEN', 149.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 5, 'Maria Schreiner', 'Klimaservice',   'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '17 days', 'ABGESCHLOSSEN', 69.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 3, 'Stefan Bauer',    'Bremsenservice', 'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '18 days', 'NEU',           199.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 2, 'Thomas Kauer',    'Reifenwechsel',  'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '19 days', 'BESTÄTIGT',     79.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 3, 'Maria Schreiner', 'Bremsenservice', 'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '20 days', 'NEU',           199.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 4, 'Stefan Bauer',    'HU / §57a',      'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '21 days', 'AUSSTEHEND',    120.00, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 5, 'Thomas Kauer',    'Klimaservice',   'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '22 days', 'NEU',           69.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 1, 'Maria Schreiner', 'Ölwechsel',      'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '23 days', 'BESTÄTIGT',     149.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 2, 'Stefan Bauer',    'Reifenwechsel',  'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '24 days', 'NEU',           79.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 3, 'Thomas Kauer',    'Bremsenservice', 'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '25 days', 'AUSSTEHEND',    199.99, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 4, 'Maria Schreiner', 'HU / §57a',      'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '26 days', 'NEU',           120.00, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 5, 'Stefan Bauer',    'Klimaservice',   'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '27 days', 'BESTÄTIGT',     69.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 4, 'Thomas Kauer',    'HU / §57a',      'VW Golf 2019',  CURRENT_TIMESTAMP + INTERVAL '28 days', 'NEU',           120.00, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 2, 'Maria Schreiner', 'Reifenwechsel',  'BMW X3 2021',   CURRENT_TIMESTAMP + INTERVAL '29 days', 'AUSSTEHEND',    79.99,  CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 1, 'Stefan Bauer',    'Ölwechsel',      'Audi A4 2017',  CURRENT_TIMESTAMP + INTERVAL '30 days', 'NEU',           149.99, CURRENT_TIMESTAMP - INTERVAL '8 days');
-- 7. REVIEWS
INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (2, 'Thomas Kauer',    5, 'Super Service, mein VW Golf läuft wieder perfekt!', CURRENT_TIMESTAMP);

INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (3, 'Maria Schreiner', 4, 'Schneller Reifenwechsel, sehr freundlich.',          CURRENT_TIMESTAMP);

INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (4, 'Stefan Bauer',    5, 'Transparent, pünktlich, top Preisleistung.',         CURRENT_TIMESTAMP);


-- 8. OPENING HOURS
INSERT INTO opening_hours (day_label, open_time, close_time, closed)
VALUES ('Mo – Fr', '08:00', '17:00', false);

INSERT INTO opening_hours (day_label, open_time, close_time, closed)
VALUES ('Samstag', '09:00', '13:00', false);

INSERT INTO opening_hours (day_label, open_time, close_time, closed)
VALUES ('Sonntag', NULL, NULL, true);

-- 9. TIMESLOTS
INSERT INTO timeslots (time) VALUES ('08:00:00');
INSERT INTO timeslots (time) VALUES ('08:30:00');
INSERT INTO timeslots (time) VALUES ('09:00:00');
INSERT INTO timeslots (time) VALUES ('09:30:00');
INSERT INTO timeslots (time) VALUES ('10:00:00');
INSERT INTO timeslots (time) VALUES ('10:30:00');
INSERT INTO timeslots (time) VALUES ('11:00:00');
INSERT INTO timeslots (time) VALUES ('11:30:00');
INSERT INTO timeslots (time) VALUES ('13:00:00');
INSERT INTO timeslots (time) VALUES ('13:30:00');
INSERT INTO timeslots (time) VALUES ('14:00:00');
INSERT INTO timeslots (time) VALUES ('14:30:00');
INSERT INTO timeslots (time) VALUES ('15:00:00');
INSERT INTO timeslots (time) VALUES ('15:30:00');
INSERT INTO timeslots (time) VALUES ('16:00:00');
INSERT INTO timeslots (time) VALUES ('16:30:00');
