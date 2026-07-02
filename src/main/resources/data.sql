-- ============================================================
-- Clean all tables (child → parent order) and reset sequences
-- Hinweis: "contact_info" wurde entfernt, da es dafür kein Entity/Tabelle
-- mehr im Backend gibt. "appointment_services" wird durch CASCADE
-- automatisch mitgeleert (Appointment <-> ServiceEntity, ManyToMany).
-- ============================================================
TRUNCATE TABLE
    opening_hours,
    reviews,
    appointments,
    offer_services,
    offers,
    services,
    timeslots,
    vehicles,
    users
    RESTART IDENTITY CASCADE;


-- 1. USERS (bleiben unverändert – gleiche Accounts wie bisher)
INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Max', 'Mustermann', 'admin@mwperformance.at', '$2y$10$..iVgU.UYVDvYuW8NLokEONtpdp/Rtck/qAnzFGq.bksPgOId9zwq', '+43 664 1234567', 'Werkstattstraße 1', '8010', 'Graz', 'ADMIN', CURRENT_TIMESTAMP);

INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Thomas', 'Kauer', 'thomas.kauer@gmail.com', '$2y$10$Vp.8qEcQlOBNYBHXAZi0xeS6V5Fr9giPo249L51nDVN8wqG8nERQG', '+43 699 1111111', 'Hauptstraße 5', '8010', 'Graz', 'CUSTOMER', CURRENT_TIMESTAMP);

INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Maria', 'Schreiner', 'maria.schreiner@gmail.com', '$2y$10$1X8sYSsI1tEB6tidBUqsfeSqlVdvABnBMjIfPRB7pTzUz154.O.EG', '+43 699 2222222', 'Schillerstraße 12', '8010', 'Graz', 'CUSTOMER', CURRENT_TIMESTAMP);

INSERT INTO users (first_name, last_name, email, password_hash, phone, street, zip, city, role, created_at)
VALUES ('Stefan', 'Bauer', 'stefan.bauer@gmail.com', '$2y$10$G7dfXYviNn6Ll/NvLX1XZeTH1TbS9waUZEgijrNERwHzmEDFUGAMy', '+43 699 3333333', 'Mozartgasse 3', '8430', 'Leibnitz', 'CUSTOMER', CURRENT_TIMESTAMP);

-- 2. VEHICLES (zufällig, mehr Fahrzeuge pro Kunde)
-- id 1
INSERT INTO vehicles (user_id, brand, model, build_year, license_plate) VALUES (2, 'VW', 'Golf', 2019, 'GU-12345');
-- id 2
INSERT INTO vehicles (user_id, brand, model, build_year, license_plate) VALUES (2, 'Skoda', 'Octavia', 2016, 'GU-45678');
-- id 3
INSERT INTO vehicles (user_id, brand, model, build_year, license_plate) VALUES (3, 'BMW', 'X3', 2021, 'GU-67890');
-- id 4
INSERT INTO vehicles (user_id, brand, model, build_year, license_plate) VALUES (4, 'Audi', 'A4', 2017, 'LB-11111');
-- id 5
INSERT INTO vehicles (user_id, brand, model, build_year, license_plate) VALUES (4, 'Ford', 'Focus', 2015, 'LB-22222');

-- 3. SERVICES -- alle Leistungen laut Werkstatt-/Reifenservice-Tafel (Preis/Dauer selbst festgelegt)
-- id 1  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Service laut Hersteller', 'Wartung nach Herstellervorgaben', 129.99, 90);
-- id 2  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Ölwechsel / Filterwechsel', 'Motoröl + Ölfilter tauschen', 49.99, 30);
-- id 3  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Bremsen / Fahrwerktausch', 'Bremsbeläge, Scheiben & Fahrwerk', 149.99, 120);
-- id 4  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('§57a Begutachtung (Pickerl)', 'Hauptuntersuchung nach Paragraph 57a', 120.00, 60);
-- id 5  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Ankaufstest', 'Gebrauchtwagen-Check vor dem Kauf', 79.99, 60);
-- id 6  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Steinschlagreparatur', 'Kleine Steinschläge in der Scheibe reparieren', 59.99, 30);
-- id 7  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Windschutzscheibentausch', 'Frontscheibe erneuern', 349.99, 120);
-- id 8  (Werkstatt)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Scheibenfolierung', 'Sonnenschutz- / Tönungsfolie anbringen', 199.99, 90);
-- id 9  (Reifenservice)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Reifenmontage / Auswuchten', 'Reifen montieren und auswuchten', 39.99, 45);
-- id 10 (Reifenservice)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Reifen umstecken', 'Saisonaler Räderwechsel (Sommer/Winter)', 29.99, 30);
-- id 11 (Reifenservice)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Neureifen', 'Neureifen inkl. Montage', 89.99, 45);
-- id 12 (Reifenservice)
INSERT INTO services (title, subtitle, price, duration) VALUES ('Felgen / Reifendruckkontrollsystem', 'Felgenservice & RDKS-Check', 69.99, 45);

-- 4. OFFERS
-- Frühjahrs-Check: Ölwechsel(2) + Bremsen(3) + Reifenmontage(9) -> 30+120+45 = 195 min
INSERT INTO offers (title, description, price, duration, active, created_at)
VALUES ('Frühjahrs-Check', 'Ölwechsel + Bremsen + Reifenmontage im Paket', 179.99, 195, true, CURRENT_TIMESTAMP);

-- Reifenwechsel-Paket: Reifen umstecken(10) + Felgen/RDKS(12) -> 30+45 = 75 min
INSERT INTO offers (title, description, price, duration, active, created_at)
VALUES ('Reifenwechsel-Paket', 'Reifen umstecken inkl. Felgen- und RDKS-Check', 89.99, 75, true, CURRENT_TIMESTAMP);

-- 5. OFFER_SERVICES
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 2);
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 3);
INSERT INTO offer_services (offer_id, service_id) VALUES (1, 9);
INSERT INTO offer_services (offer_id, service_id) VALUES (2, 10);
INSERT INTO offer_services (offer_id, service_id) VALUES (2, 12);

-- 6. APPOINTMENTS
-- Jede Leistung (id 1-12) kommt mindestens einmal vor, Rest ist zufällig gemischt.
-- offer_id ist bei Paket-Buchungen gesetzt, service_id dann NULL.

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 1, 1, NULL, 'Thomas Kauer', 'Service laut Hersteller', 'VW Golf 2019', CURRENT_TIMESTAMP + INTERVAL '1 day', 'NEU', 129.99, NULL, 90, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 2, NULL, 'Maria Schreiner', 'Ölwechsel / Filterwechsel', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '2 days', 'AUSSTEHEND', 49.99, NULL, 30, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 4, 3, NULL, 'Stefan Bauer', 'Bremsen / Fahrwerktausch', 'Audi A4 2017', CURRENT_TIMESTAMP + INTERVAL '3 days', 'BESTÄTIGT', 149.99, 'Bitte auf Quietschen an der Vorderachse achten', 120, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 2, 4, NULL, 'Thomas Kauer', '§57a Begutachtung (Pickerl)', 'Skoda Octavia 2016', CURRENT_TIMESTAMP + INTERVAL '4 days', 'ABGELEHNT', 120.00, NULL, 60, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 5, NULL, 'Maria Schreiner', 'Ankaufstest', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '5 days', 'NEU', 79.99, NULL, 60, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 5, 6, NULL, 'Stefan Bauer', 'Steinschlagreparatur', 'Ford Focus 2015', CURRENT_TIMESTAMP + INTERVAL '6 days', 'ABGESCHLOSSEN', 59.99, NULL, 30, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 1, 7, NULL, 'Thomas Kauer', 'Windschutzscheibentausch', 'VW Golf 2019', CURRENT_TIMESTAMP + INTERVAL '7 days', 'BESTÄTIGT', 349.99, 'Ersatzscheibe wurde bereits bestellt', 120, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 8, NULL, 'Maria Schreiner', 'Scheibenfolierung', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '8 days', 'NEU', 199.99, NULL, 90, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 4, 9, NULL, 'Stefan Bauer', 'Reifenmontage / Auswuchten', 'Audi A4 2017', CURRENT_TIMESTAMP + INTERVAL '9 days', 'AUSSTEHEND', 39.99, NULL, 45, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 2, 10, NULL, 'Thomas Kauer', 'Reifen umstecken', 'Skoda Octavia 2016', CURRENT_TIMESTAMP + INTERVAL '10 days', 'ABGELEHNT', 29.99, NULL, 30, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 11, NULL, 'Maria Schreiner', 'Neureifen', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '11 days', 'BESTÄTIGT', 89.99, NULL, 45, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 5, 12, NULL, 'Stefan Bauer', 'Felgen / Reifendruckkontrollsystem', 'Ford Focus 2015', CURRENT_TIMESTAMP + INTERVAL '12 days', 'ABGESCHLOSSEN', 69.99, NULL, 45, CURRENT_TIMESTAMP - INTERVAL '8 days');

-- weitere zufällige Termine (Mischung aus Einzelleistungen und Paketen)
INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 1, 2, NULL, 'Thomas Kauer', 'Ölwechsel / Filterwechsel', 'VW Golf 2019', CURRENT_TIMESTAMP + INTERVAL '13 days', 'NEU', 49.99, NULL, 30, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 4, 4, NULL, 'Stefan Bauer', '§57a Begutachtung (Pickerl)', 'Audi A4 2017', CURRENT_TIMESTAMP + INTERVAL '14 days', 'BESTÄTIGT', 120.00, NULL, 60, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 9, NULL, 'Maria Schreiner', 'Reifenmontage / Auswuchten', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '15 days', 'AUSSTEHEND', 39.99, NULL, 45, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 2, 1, NULL, 'Thomas Kauer', 'Service laut Hersteller', 'Skoda Octavia 2016', CURRENT_TIMESTAMP + INTERVAL '16 days', 'ABGESCHLOSSEN', 129.99, NULL, 90, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 5, 3, NULL, 'Stefan Bauer', 'Bremsen / Fahrwerktausch', 'Ford Focus 2015', CURRENT_TIMESTAMP + INTERVAL '17 days', 'NEU', 149.99, NULL, 120, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 12, NULL, 'Maria Schreiner', 'Felgen / Reifendruckkontrollsystem', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '18 days', 'ABGELEHNT', 69.99, NULL, 45, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 1, 10, NULL, 'Thomas Kauer', 'Reifen umstecken', 'VW Golf 2019', CURRENT_TIMESTAMP + INTERVAL '19 days', 'BESTÄTIGT', 29.99, NULL, 30, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 4, 11, NULL, 'Stefan Bauer', 'Neureifen', 'Audi A4 2017', CURRENT_TIMESTAMP + INTERVAL '20 days', 'NEU', 89.99, NULL, 45, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 6, NULL, 'Maria Schreiner', 'Steinschlagreparatur', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '21 days', 'AUSSTEHEND', 59.99, NULL, 30, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 2, 7, NULL, 'Thomas Kauer', 'Windschutzscheibentausch', 'Skoda Octavia 2016', CURRENT_TIMESTAMP + INTERVAL '22 days', 'ABGESCHLOSSEN', 349.99, NULL, 120, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 5, 8, NULL, 'Stefan Bauer', 'Scheibenfolierung', 'Ford Focus 2015', CURRENT_TIMESTAMP + INTERVAL '23 days', 'BESTÄTIGT', 199.99, NULL, 90, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 5, NULL, 'Maria Schreiner', 'Ankaufstest', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '24 days', 'NEU', 79.99, NULL, 60, CURRENT_TIMESTAMP - INTERVAL '8 days');

-- Paket-Buchungen (service_id NULL, offer_id gesetzt)
INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 1, NULL, 1, 'Thomas Kauer', 'Frühjahrs-Check', 'VW Golf 2019', CURRENT_TIMESTAMP + INTERVAL '25 days', 'BESTÄTIGT', 179.99, 'Paketbuchung Frühjahrs-Check', 195, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 4, NULL, 2, 'Stefan Bauer', 'Reifenwechsel-Paket', 'Audi A4 2017', CURRENT_TIMESTAMP + INTERVAL '26 days', 'NEU', 89.99, 'Paketbuchung Reifenwechsel', 75, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, 4, NULL, 'Maria Schreiner', '§57a Begutachtung (Pickerl)', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '27 days', 'ABGELEHNT', 120.00, NULL, 60, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (2, 2, 2, NULL, 'Thomas Kauer', 'Ölwechsel / Filterwechsel', 'Skoda Octavia 2016', CURRENT_TIMESTAMP + INTERVAL '28 days', 'ABGESCHLOSSEN', 49.99, NULL, 30, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (4, 5, 9, NULL, 'Stefan Bauer', 'Reifenmontage / Auswuchten', 'Ford Focus 2015', CURRENT_TIMESTAMP + INTERVAL '29 days', 'BESTÄTIGT', 39.99, NULL, 45, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO appointments (user_id, vehicle_id, service_id, offer_id, customer_name, service_type, vehicle, preferred_date, status, price, note, duration_minutes, created_at)
VALUES (3, 3, NULL, 1, 'Maria Schreiner', 'Frühjahrs-Check', 'BMW X3 2021', CURRENT_TIMESTAMP + INTERVAL '30 days', 'AUSSTEHEND', 179.99, 'Paketbuchung Frühjahrs-Check', 195, CURRENT_TIMESTAMP - INTERVAL '8 days');

-- 7. REVIEWS
INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (2, 'Thomas Kauer', 5, 'Super Service, mein VW Golf läuft wieder perfekt!', CURRENT_TIMESTAMP);

INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (3, 'Maria Schreiner', 4, 'Schneller Reifenwechsel, sehr freundlich.', CURRENT_TIMESTAMP);

INSERT INTO reviews (user_id, name, stars, text, created_at)
VALUES (4, 'Stefan Bauer', 5, 'Transparent, pünktlich, top Preisleistung.', CURRENT_TIMESTAMP);

-- 8. OPENING HOURS
INSERT INTO opening_hours (day_label, open_time, close_time, closed) VALUES ('Mo – Fr', '08:00', '17:00', false);
INSERT INTO opening_hours (day_label, open_time, close_time, closed) VALUES ('Samstag', '09:00', '13:00', false);
INSERT INTO opening_hours (day_label, open_time, close_time, closed) VALUES ('Sonntag', NULL, NULL, true);

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