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

-- 3. SERVICES (icon als bytea)
INSERT INTO services (icon, title, subtitle)
VALUES ('\x89504e470d0a1a0a0000000d4948445200000040000000400806000000aa6971de000000ea49444154789cedd7c111823014455174ac833aa8c422acc012acc022ac843a6844566e18183fe4c72bc33d4b46c8cb4b00691a49922449920ee7547b80fed9bce78e77b7fa6347540bb134f129ba88f4c1a3139fa28a38675e6cebe44bcf2d915ac01ea51590b182c42e7007d00168169075a18cd718f12abc447fd8b67de001d59564098eb16c18bad505a6de02d7478f9c5b22dcd8dad579dd63bb2173e25b7640b5023e968aa8b1e27f59c02f1dbe806fe60af27f001d80660174009a05d00168164007a085bf06f700ff1adc230ba003d02c800e40b3003a00cd02e800340ba00348922449920023cfd7358403a599fe0000000049454e44ae426082'::bytea,
        'Ölwechsel', 'Motoröl + Filter wechseln');

INSERT INTO services (icon, title, subtitle)
VALUES ('\x89504e470d0a1a0a0000000d4948445200000040000000400806000000aa6971de000001bb49444154789ced5b4b9683300c73e7cd61380e47e538dc66bacaa64348ac484efab0b6a5b6243b3ff2304b241289c473f18a4cb66ddb5fefb3e77986709327f188ae416986243043740d6c33a8c194c23fc132e28711c42c563c331fc58068f1ccbc436d344bf815d0210177c04ae2cd703e9001ab892f4078b90d58557c81971f6d15f856b80c58bdfa051e9edd332753fcdd8c1d95a7a0cb8059fbf988bc217300ba46479c089b0946aac014a0e221eb0076f554dd203140455611f7f7ee47e5b2b7effbbfd8c771c8de4fd4ccbb4d8818d0aad295f04fb48c60f2a20e0186f89ee79843a16a00bbfd7bc5a3cfb750d313b20f40c5b04db802cd80a8d7d8ec7cf20e18ada2ba0bf2383c9bc06ca401b309cc86dc80d1edad6a7b5c9037438c202da0555457dfecc600f6c6c62b862d3ee430d46acb5e518ad3600d92443ddd83be0f60739224549e0bd87c2493a06a4550c495ad026cb22a53432e464686843a77c83e001511b1b9cabb414fc06fb91d36eb1f76ae2110fdda0b8587671e87bd7f58bd0bbcfca00e58d50484173c04563301e54311317375182d0465129cd50d8cbc8fbf19caef0598c1aef0c82f466a58f19ba14422917834de6a86f9d27395f0580000000049454e44ae426082'::bytea,
        'Reifenwechsel', 'Sommer- / Winterreifen montieren');

INSERT INTO services (icon, title, subtitle)
VALUES ('\x89504e470d0a1a0a0000000d4948445200000040000000400806000000aa6971de000001ac49444154789ced9b416ec3300c04e9a22ff29b7cd2837cd29bfca5f6242070eac824772516e65c8b90cb252d298a2b9224499224c953594627dcf7fd4744e4388eb7bfadeb2a2222a59461bae8895ac1673e19708669082df055e10d8d010d8611f080bdc21b16031a4823be508144ee171f290fc4498b20cf04bce29d06f7048cea3a2bbfcb80d9c5373c3acc064429be61d56332205af10d8b2ee82ef01f511b10b5fb0dad3e9501a8e2afb62ed40147a3f31b91f02eaf05d65a3f9a306ad26e3bee11e4e92c3b2f7d11f48e35fbabf12d03ac5d4089b7c6b9a39b3601e8ceb12621cf018ca0ac6e31e2760d887ef0e9d1d30f9f00f6aa8d8e9f6bc06c01b34903660b984d1a305bc06ce006b0cf0de8f85d0346fe50c9a0a79ff208b0a6801137d700566074b7585375cb00e68504334e882b31af09ec5d45d559af18cd248dca35f45abc15554a59b66d7b2bb0d6ba8cbe7f503fdb2881a8f703fe423369ea3520fac148ab2fcf01960f459d028b2ef3044433c1aac7f5084431c1a3c3bd06cc36c19b1f2a5eb3457ab74194f1d05d60d43420f3e4bbc2e880671efbb6f815d1fe5f20499224491ecc2fad91d830e48442030000000049454e44ae426082'::bytea,
        'Bremsenservice', 'Bremsbeläge + Scheiben prüfen');

INSERT INTO services (icon, title, subtitle)
VALUES ('\x89504e470d0a1a0a0000000d4948445200000040000000400806000000aa6971de0000011f49444154789ced9abb1582401045d16309c61461a21558821558841181114558812558812416416c0f9a2bbf9d5d7870e6de10d87d6fde193e0b641900000000000000000000f861153bc1eefcfca4309282d7ed105ccf7a0c234bc27d009bd4135e2f7bf3d8a2ac4ce38bb2326bbaef0002501b5043006a036a08406d400d01a80da8e97d129cd362a78f36af5d8b24f71dd0bb7c5c5207b441077440006a036a08406d400d01a80da8711f409297a2efc7e96fdbf6784f31f5e83a51013419fadd9722883175cca7409729cb712a1d5300a162567353e8040730e7622ce3dcdf05dc0710fc3939cf73f3fb81baae07eb4da5e3be03820308493766dc543aa60e986b31161df3293054cc5afc543ad1ff086559f3052bb670a50e00801bbe9f396c489c2b41600000000049454e44ae426082'::bytea,
        'HU / §57a', 'Hauptuntersuchung durchführen');

INSERT INTO services (icon, title, subtitle)
VALUES ('\x89504e470d0a1a0a0000000d4948445200000040000000400806000000aa6971de0000015649444154789ced9a5b0e82500c4447d7e7325c96cb707ff865620c6242e7a161ceaf096d4f107a5b80524a29a51c955322e8f5be2c9f7ebb5d4ed69ccece60bf4805a413485301e904d25400e3225baf35258cb86301cf24dc1258714702de83bb2430e38e04ac756d6a096bd79f748fe3bf805302bb7880f410744850140f105f834a09aae201721fa090a02c1e1034424c09eae2015127c890e0281e10b6c21309aee201c34488f120544e89e487a169f2ea1199e534b8b708c77cf06b80d4498fc996c8ce03d209a4a9807402692a209d401adb1e6ecfebd4d10758ee00e669908d5c80621ec0442a60cfa9ce3d689509981c699d12240218e7799704ba00e630c321812a4031c9514ba009508eb1941264db617613a39240db0ebfa2eae01412a8db6140dfbeb22550b7c3ae6ffc987169db61f7078eacb8b2edb0839fda0eff2b15904e204d05a41348737801a59452ca8179007991d05fb92efc820000000049454e44ae426082'::bytea,
        'Klimaservice', 'Kältemittel + Desinfektion + Check');

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
VALUES (2, 1, 4, 'Thomas Kauer',    'HU / §57a',      'VW Golf 2019',  '2026-04-11 08:00:00', 'NEU',           120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 5, 'Maria Schreiner', 'Klimaservice',   'BMW X3 2021',   '2026-04-11 10:00:00', 'AUSSTEHEND',    69.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 4, 'Stefan Bauer',    'HU / §57a',      'Audi A4 2017',  '2026-04-12 09:00:00', 'BESTÄTIGT',     120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 2, 'Thomas Kauer',    'Reifenwechsel',  'VW Golf 2019',  '2026-04-14 11:30:00', 'NEU',           79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 1, 'Maria Schreiner', 'Ölwechsel',      'BMW X3 2021',   '2026-04-15 08:30:00', 'ABGELEHNT',     149.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 3, 'Stefan Bauer',    'Bremsenservice', 'Audi A4 2017',  '2026-04-15 13:00:00', 'NEU',           199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 5, 'Thomas Kauer',    'Klimaservice',   'VW Golf 2019',  '2026-04-16 09:00:00', 'AUSSTEHEND',    69.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 3, 'Maria Schreiner', 'Bremsenservice', 'BMW X3 2021',   '2026-04-17 14:00:00', 'BESTÄTIGT',     199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 1, 'Stefan Bauer',    'Ölwechsel',      'Audi A4 2017',  '2026-04-18 08:00:00', 'ABGESCHLOSSEN', 149.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 3, 'Thomas Kauer',    'Bremsenservice', 'VW Golf 2019',  '2026-04-21 10:30:00', 'NEU',           199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 2, 'Maria Schreiner', 'Reifenwechsel',  'BMW X3 2021',   '2026-04-22 09:00:00', 'NEU',           79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 5, 'Stefan Bauer',    'Klimaservice',   'Audi A4 2017',  '2026-04-22 15:00:00', 'AUSSTEHEND',    69.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 4, 'Thomas Kauer',    'HU / §57a',      'VW Golf 2019',  '2026-04-23 08:00:00', 'ABGELEHNT',     120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 4, 'Maria Schreiner', 'HU / §57a',      'BMW X3 2021',   '2026-04-24 11:00:00', 'BESTÄTIGT',     120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 2, 'Stefan Bauer',    'Reifenwechsel',  'Audi A4 2017',  '2026-04-25 09:30:00', 'NEU',           79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 1, 'Thomas Kauer',    'Ölwechsel',      'VW Golf 2019',  '2026-04-28 08:00:00', 'ABGESCHLOSSEN', 149.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 5, 'Maria Schreiner', 'Klimaservice',   'BMW X3 2021',   '2026-04-28 14:00:00', 'ABGESCHLOSSEN', 69.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 3, 'Stefan Bauer',    'Bremsenservice', 'Audi A4 2017',  '2026-04-29 10:00:00', 'NEU',           199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 2, 'Thomas Kauer',    'Reifenwechsel',  'VW Golf 2019',  '2026-04-30 09:00:00', 'BESTÄTIGT',     79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 3, 'Maria Schreiner', 'Bremsenservice', 'BMW X3 2021',   '2026-05-02 11:00:00', 'NEU',           199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 4, 'Stefan Bauer',    'HU / §57a',      'Audi A4 2017',  '2026-05-05 08:30:00', 'AUSSTEHEND',    120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 5, 'Thomas Kauer',    'Klimaservice',   'VW Golf 2019',  '2026-05-06 13:00:00', 'NEU',           69.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 1, 'Maria Schreiner', 'Ölwechsel',      'BMW X3 2021',   '2026-05-07 09:00:00', 'BESTÄTIGT',     149.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 2, 'Stefan Bauer',    'Reifenwechsel',  'Audi A4 2017',  '2026-05-07 15:30:00', 'NEU',           79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 3, 'Thomas Kauer',    'Bremsenservice', 'VW Golf 2019',  '2026-05-08 08:00:00', 'AUSSTEHEND',    199.99, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 4, 'Maria Schreiner', 'HU / §57a',      'BMW X3 2021',   '2026-05-09 10:00:00', 'NEU',           120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 5, 'Stefan Bauer',    'Klimaservice',   'Audi A4 2017',  '2026-05-12 14:00:00', 'BESTÄTIGT',     69.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (2, 1, 4, 'Thomas Kauer',    'HU / §57a',      'VW Golf 2019',  '2026-05-13 09:30:00', 'NEU',           120.00, CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (3, 2, 2, 'Maria Schreiner', 'Reifenwechsel',  'BMW X3 2021',   '2026-05-14 11:00:00', 'AUSSTEHEND',    79.99,  CURRENT_TIMESTAMP);

INSERT INTO appointments (user_id, vehicle_id, service_id, customer_name, service_type, vehicle, preferred_date, status, price, created_at)
VALUES (4, 3, 1, 'Stefan Bauer',    'Ölwechsel',      'Audi A4 2017',  '2026-05-15 08:00:00', 'NEU',           149.99, CURRENT_TIMESTAMP);

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

-- 10. TIMESLOTS
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
