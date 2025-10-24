-- 1. Insert a Test User
INSERT INTO users (id, name, email) VALUES
    (101, 'test_user', 'user@example.com'),
    (999, 'owner_user', 'user2@example.com')
ON CONFLICT (id) DO NOTHING; -- Assuming users table has columns for this

-- 2. Insert a Test Group (klatre_groups)
INSERT INTO klatre_groups (id, name) VALUES
    (201, 'Test Group A'),
    (999, 'Test Group B')
ON CONFLICT (id) DO NOTHING; -- Assuming klatre_groups table has a name column

INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES
    (1, 'french', 'sport', true);

INSERT INTO grades(system_id, grade_string, numerical_value) VALUES
    (1, '4a', 1),
    (1, '4b', 2),
    (1, '4c', 3),
    (1, '5a', 4),
    (1, '5b', 5),
    (1, '5c', 6),
    (1, '6a', 7),
    (1, '6a+', 8),
    (1, '6b', 9),
    (1, '6b+', 10),
    (1, '6c', 11),
    (1, '6c+', 12),
    (1, '7a', 13),
    (1, '7a+', 14),
    (1, '7b', 15),
    (1, '7b+', 16),
    (1, '7c', 17),
    (1, '7c+', 18),
    (1, '8a', 19),
    (1, '8a+', 20),
    (1, '8b', 21),
    (1, '8b+', 22),
    (1, '8c', 23),
    (1, '8c+', 24),
    (1, '9a', 25);

-- 3. Insert a Test Place
INSERT INTO places (id, name, group_id) VALUES
(301, 'Test Gym', 201)
ON CONFLICT (id) DO NOTHING; -- Assuming places table has name and address columns

INSERT INTO boulders (id, name, grade, userID, place)
VALUES
    (501, 'a', 6, 999, 301),
    (502, 'b', 7, 999, 301),
    (503, 'b', 7, 999, 301),
    (504, 'b', 7, 999, 301),
    (505, 'b', 7, 999, 301);

-- Past Session 1: Should be returned by getPastSessions(201)
INSERT INTO climbing_sessions (id, name, active, user_id, group_id, place_id, created_at) VALUES
(1, 'Morning Climb', FALSE, 101, 201, 301, NOW() - INTERVAL '2 days');

-- Past Session 2: Should be returned by getPastSessions(201)
INSERT INTO climbing_sessions (id, name, active, user_id, group_id, place_id, created_at) VALUES
(2, 'Evening Bouldering', FALSE, 101, 201, 301, NOW() - INTERVAL '1 day');

-- Active Session: Should NOT be returned by getPastSessions(201) because active = TRUE
INSERT INTO climbing_sessions (id, name, active, user_id, group_id, place_id, created_at) VALUES
(3, 'Current Session', TRUE, 101, 201, 301, NOW());

-- Session for a different group: Should NOT be returned by getPastSessions(201)
INSERT INTO climbing_sessions (id, name, active, user_id, group_id, place_id, created_at) VALUES
(4, 'Other Group Session', FALSE, 999, 999, 301, NOW() - INTERVAL '3 days');

-- Attempts for Past Session 1
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
(10, 501, 3, TRUE, 1, EXTRACT(EPOCH FROM NOW()) * 1000), -- 501 is a hypothetical route ID
(11, 502, 2, TRUE, 1, EXTRACT(EPOCH FROM NOW()) * 1000);

-- Attempts for Past Session 2
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (12, 503, 1, FALSE, 2, EXTRACT(EPOCH FROM NOW()) * 1000),
    (13, 504, 5, TRUE, 2, EXTRACT(EPOCH FROM NOW()) * 1000);

-- Attempts for Active Session 3 (to verify it's not loaded)
INSERT INTO route_attempts (id, route_id, attempts, completed, session, last_updated) VALUES
    (14, 505, 4, FALSE, 3, EXTRACT(EPOCH FROM NOW()) * 1000);