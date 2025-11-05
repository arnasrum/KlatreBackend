-- Roles
INSERT INTO roles (id, role_name) VALUES
(1, 'OWNER'),
(2, 'ADMIN'),
(3, 'USER');

-- Users
INSERT INTO users (id, email, name) VALUES
    (1, 'alice@example.com', 'Alice'),
    (2, 'bob@example.com', 'Bob'),
    (3, 'charlie@example.com', 'Charlie'),
    (4, 'diana@example.com', 'Diana');

-- Groups
INSERT INTO klatre_groups (id, owner, name, personal, uuid, description) VALUES
    (1, 1, 'Climbing Buddies', false, 'uuid-123', 'Main climbing group'),
    (2, 2, 'Bob Solo', true, 'uuid-456', 'Personal group');

-- User Groups (medlemskap)
INSERT INTO user_groups (user_id, group_id, role) VALUES
    (1, 1, 1), -- Alice owns group 1
    (2, 1, 3), -- Bob is member
    (3, 1, 3), -- Charlie is member
    (2, 2, 1); -- Bob owns his personal group

-- Grading Systems
INSERT INTO grading_systems (id, name, climb_type, is_global, created_in_group) VALUES
    (1, 'Font', 'boulder', true, NULL);

-- Grades (Font skala)
INSERT INTO grades (id, system_id, grade_string, numerical_value) VALUES
    (1, 1, '5A', 50),
    (2, 1, '5B', 51),
    (3, 1, '5C', 52),
    (4, 1, '6A', 60),
    (5, 1, '6A+', 61),
    (6, 1, '6B', 62),
    (7, 1, '6C', 63),
    (8, 1, '7A', 70),
    (9, 1, '7B', 71);

-- Places
INSERT INTO places (id, name, description, grading_system_id, group_id) VALUES
    (1, 'Local Gym', 'Indoor bouldering gym', 1, 1),
    (2, 'Outdoor Crag', 'Nice outdoor spot', 1, 1);

-- Routes/Boulders
INSERT INTO routes (id, name, grade_id, description, active, user_id, place_id) VALUES
    (1, 'Warm Up', 1, 'Easy starter', true, 1, 1),
    (2, 'The Crimper', 4, 'Technical crimps', true, 1, 1),
    (3, 'Roof Problem', 7, 'Steep roof', true, 2, 1),
    (4, 'Slab Master', 3, 'Balance moves', true, 1, 1),
    (5, 'Project X', 9, 'Very hard', true, 1, 1),
    (6, 'Outdoor Classic', 5, 'Nice moves', true, 2, 2);

-- Climbing Sessions
INSERT INTO climbing_sessions (id, active, user_id, group_id, place_id, created_at) VALUES
    (1, false, 1, 1, 1, 1704067200), -- Alice, 1 jan 2024
    (2, false, 2, 1, 1, 1704153600), -- Bob, 2 jan 2024
    (3, false, 1, 1, 1, 1704240000), -- Alice, 3 jan 2024
    (4, false, 3, 1, 1, 1704326400), -- Charlie, 4 jan 2024
    (5, false, 1, 1, 1, 1706918400), -- Alice, 3 feb 2024
    (6, true, 2, 1, 1, 1730678400);  -- Bob, active session (3 nov 2024)

-- Route Attempts
-- Session 1: Alice sender lett, prøver vanskelig
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (1, 1, 1, true, 1, 1704067200000),  -- Warm Up: flash
    (2, 2, 3, true, 1, 1704069000000),  -- The Crimper: 3 forsøk
    (3, 3, 5, false, 1, 1704070800000); -- Roof Problem: ikke sendt

-- Session 2: Bob sender middle grades
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (4, 2, 2, true, 2, 1704153600000),  -- The Crimper: 2 forsøk
    (5, 4, 1, true, 2, 1704155400000),  -- Slab Master: flash
    (6, 3, 8, false, 2, 1704157200000); -- Roof Problem: ikke sendt (8 forsøk)

-- Session 3: Alice sender Roof Problem endelig
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (7, 1, 1, true, 3, 1704240000000),  -- Warm Up igjen
    (8, 3, 12, true, 3, 1704243600000); -- Roof Problem: sendt etter mange forsøk!

-- Session 4: Charlie er ny, sender bare lette
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (9, 1, 2, true, 4, 1704326400000),  -- Warm Up
    (10, 4, 4, true, 4, 1704328200000), -- Slab Master
    (11, 2, 6, false, 4, 1704330000000); -- The Crimper: ikke sendt enda

-- Session 5: Alice (februar) - viser at hun progger
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (12, 3, 2, true, 5, 1706918400000),  -- Roof Problem: nå lett!
    (13, 5, 15, false, 5, 1706922000000); -- Project X: ikke sendt

-- Session 6: Bob sin active session (pågående)
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (14, 1, 1, true, 6, 1730678400000),  -- Warm up
    (15, 3, 3, false, 6, 1730680200000); -- Working på Roof

-- Reset sequences
SELECT setval('users_id_seq', 4);
SELECT setval('klatre_groups_id_seq', 2);
SELECT setval('grading_systems_id_seq', 1);
SELECT setval('grades_id_seq', 9);
SELECT setval('places_id_seq', 2);
SELECT setval('routes_id_seq', 6);
SELECT setval('climbing_sessions_id_seq', 6);
SELECT setval('route_attempts_id_seq', 15);

-- Verification queries
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Groups', COUNT(*) FROM klatre_groups
UNION ALL
SELECT 'Routes', COUNT(*) FROM routes
UNION ALL
SELECT 'Sessions', COUNT(*) FROM climbing_sessions
UNION ALL
SELECT 'Attempts', COUNT(*) FROM route_attempts;