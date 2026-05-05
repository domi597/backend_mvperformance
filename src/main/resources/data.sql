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

-- 2. VEHICLES  (user_id: Thomas=2, Maria=3, Stefan=4)
INSERT INTO vehicles (user_id, brand, model, build_year, license_plate)
VALUES (2, 'VW', 'Golf', 2019, 'GU-12345');

INSERT INTO vehicles (user_id, brand, model, build_year, license_plate)
VALUES (3, 'BMW', 'X3', 2021, 'GU-67890');

INSERT INTO vehicles (user_id, brand, model, build_year, license_plate)
VALUES (4, 'Audi', 'A4', 2017, 'LB-11111');

-- 3. SERVICES
INSERT INTO services (icon, title, subtitle, sort)
VALUES ('oil-can',   'Ölwechsel',       'Motoröl + Filter wechseln',          1);

INSERT INTO services (icon, title, subtitle, sort)
VALUES ('tire',      'Reifenwechsel',    'Sommer- / Winterreifen montieren',   2);

INSERT INTO services (icon, title, subtitle, sort)
VALUES ('brake',     'Bremsenservice',   'Bremsbeläge + Scheiben prüfen',      3);

INSERT INTO services (icon, title, subtitle, sort)
VALUES ('car',       'HU / §57a',        'Hauptuntersuchung durchführen',      4);

INSERT INTO services (icon, title, subtitle, sort)
VALUES ('snowflake', 'Klimaservice',     'Kältemittel + Desinfektion + Check', 5);

-- 4. OFFERS
INSERT INTO offers (title, description, price, active, created_at)
VALUES ('Frühjahrs-Check', 'Ölwechsel + Bremsen + Reifencheck', 49.0, true, CURRENT_TIMESTAMP);

INSERT INTO offers (title, description, price, active, created_at)
VALUES ('Klimaservice', 'Kältemittel + Desinfektion + Check', 69.0, true, CURRENT_TIMESTAMP);

-- 5. OFFER_SERVICES
-- Frühjahrs-Check (offer_id=1) = Ölwechsel(1) + Reifenwechsel(2) + Bremsenservice(3)
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 1);
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 2);
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 3);

-- Klimaservice (offer_id=2) = Klimaservice(5)
INSERT INTO offer_services (offer_id, service_id) VALUES (2, 5);

-- 6. APPOINTMENTS
INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 1, 'Thomas Kauer',   'Ölwechsel',     'VW Golf 2019',  '2026-03-19 10:00:00', 'NEU',           149.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 2, 'Maria Schreiner','Reifenwechsel',  'BMW X3 2021',   '2026-03-20 09:00:00', 'BESTÄTIGT',     79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 3, 'Stefan Bauer',   'Bremsenservice', 'Audi A4 2017',  '2026-03-21 14:00:00', 'ABGESCHLOSSEN', 199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 2, 'Thomas Kauer',   'Reifenwechsel',  'VW Golf 2019',  '2026-04-02 08:30:00', 'NEU',           79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 3, 'Maria Schreiner','Bremsenservice', 'BMW X3 2021',   '2026-04-03 11:00:00', 'BESTÄTIGT',     199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 1, 'Stefan Bauer',   'Ölwechsel',      'Audi A4 2017',  '2026-04-04 13:15:00', 'NEU',           149.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 5, 'Thomas Kauer',   'Klimaservice',   'VW Golf 2019',  '2026-04-05 09:45:00', 'ABGESCHLOSSEN', 69.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 4, 'Maria Schreiner','HU / §57a',      'BMW X3 2021',   '2026-04-06 10:30:00', 'NEU',           120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 2, 'Stefan Bauer',   'Reifenwechsel',  'Audi A4 2017',  '2026-04-07 15:00:00', 'BESTÄTIGT',     79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 3, 'Thomas Kauer',   'Bremsenservice', 'VW Golf 2019',  '2026-04-08 08:00:00', 'NEU',           199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 1, 'Maria Schreiner','Ölwechsel',      'BMW X3 2021',   '2026-04-09 09:00:00', 'NEU',           149.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 5, 'Stefan Bauer',   'Klimaservice',   'Audi A4 2017',  '2026-04-10 14:30:00', 'BESTÄTIGT',     69.99,  CURRENT_TIMESTAMP);

-- 7. REVIEWS
INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (2, 'Thomas Kauer',    5, 'Super Service, mein VW Golf läuft wieder perfekt!', CURRENT_TIMESTAMP);

INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (3, 'Maria Schreiner', 4, 'Schneller Reifenwechsel, sehr freundlich.',          CURRENT_TIMESTAMP);

INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (4, 'Stefan Bauer',    5, 'Transparent, pünktlich, top Preisleistung.',         CURRENT_TIMESTAMP);

-- 8. CONTACT INFO
INSERT INTO contact_info (icon, value)
VALUES ('phone',    '+43 664 1234567');

INSERT INTO contact_info (icon, value)
VALUES ('envelope', 'office@mwperformance.at');

INSERT INTO contact_info (icon, value)
VALUES ('map-pin',  'Werkstattstraße 1, 8010 Graz');

-- 9. OPENING HOURS
INSERT INTO opening_hours (day_label, open_time, close_time, closed)
VALUES ('Mo – Fr', '08:00', '17:00', false);

INSERT INTO opening_hours (day_label, open_time, close_time, closed)
VALUES ('Samstag', '09:00', '13:00', false);

INSERT INTO opening_hours (day_label, open_time, close_time, closed)
VALUES ('Sonntag', NULL, NULL, true);