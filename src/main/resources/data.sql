-- 1. CONTACT INFO
INSERT INTO contact_info (id, icon, value, sort) VALUES (nextval('contact_info_seq'), 'phone', '+43 664 1234567', 1);
INSERT INTO contact_info (id, icon, value, sort) VALUES (nextval('contact_info_seq'), 'envelope', 'office@gdg-technik.at', 2);
INSERT INTO contact_info (id, icon, value, sort) VALUES (nextval('contact_info_seq'), 'map-pin', 'Werkstattstraße 1, 8010 Graz', 3);

-- 2. SERVICES (Angebote)
INSERT INTO services (id, title, subtitle, icon, sort) VALUES (nextval('services_seq'), 'Frühjahrs-Check', 'Ölwechsel + Bremsen + Reifencheck - € 49,-', 'wrench', 1);
INSERT INTO services (id, title, subtitle, icon, sort) VALUES (nextval('services_seq'), 'Klimaservice', 'Kältemittel + Desinfektion + Check - € 69,-', 'snowflake', 2);

-- 3. REVIEWS (Bewertungen)
-- Hinweis: Falls du hier IDENTITY nutzt, braucht PostgreSQL keine ID-Angabe
INSERT INTO reviews (name, stars, text, created_at) VALUES ('Thomas Kauer', 5, 'Super Service, mein VW Golf läuft wieder perfekt!', CURRENT_TIMESTAMP);
INSERT INTO reviews (name, stars, text, created_at) VALUES ('Maria Schreiner', 4, 'Schneller Reifenwechsel, sehr freundlich.', CURRENT_TIMESTAMP);

-- 4. APPOINTMENTS (Die "Offenen Terminanfragen" aus deinem Dashboard)
INSERT INTO appointments (id, customer_name, service_type, vehicle, preferred_date, status)
VALUES (nextval('appointment_seq'), 'Thomas Kauer', 'Ölwechsel', 'VW Golf 2019', '2026-03-19 10:00:00', 'Neu');

INSERT INTO appointments (id, customer_name, service_type, vehicle, preferred_date, status)
VALUES (nextval('appointment_seq'), 'Maria Schreiner', 'Reifenwechsel', 'BMW X3 2021', '2026-03-20 09:00:00', 'Ausstehend');

INSERT INTO appointments (id, customer_name, service_type, vehicle, preferred_date, status)
VALUES (nextval('appointment_seq'), 'Stefan Bauer', 'Bremsenservice', 'Audi A4 2017', '2026-03-21 14:00:00', 'Bestätigt');