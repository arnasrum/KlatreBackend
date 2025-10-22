-- roles: 1=Owner, 2=Admin, 3=User
INSERT INTO roles (id, role_name) VALUES (1, 'OWNER'), (3, 'USER');

-- users
 INSERT INTO users (id, email, name) VALUES (101, 'owner@example.com', 'Group Owner'), (102, 'active@example.com', 'Active Climber');
 SELECT setval('users_id_seq', 102); -- Ensure sequence is up-to-date

-- grading_systems (required by places table, assuming ID 1 exists)
 INSERT INTO grading_systems (id, name, climb_type, is_global) VALUES (1, 'Yosemite Decimal System', 'Route', TRUE);


SELECT setval('grading_systems_id_seq', 1);

-- grades (needed for boulder definitions)
INSERT INTO grades (system_id, grade_string, numerical_value) VALUES (1, '5.10a', 100), (1, '5.11b', 110);


-- klatre_groups (ID 201 will be used)
INSERT INTO klatre_groups (id, owner, name, personal) VALUES
    (201, 101, 'Sample Climbing Group', FALSE);
SELECT setval('klatre_groups_id_seq', 201);

-- user_groups: Owner (101) and Active Climber (102) join the group (201)
INSERT INTO user_groups (user_id, group_id, role) VALUES
                                                      (101, 201, 1), -- Owner
                                                      (102, 201, 3); -- User

-- places (a location for the activity, ID 301 will be used)
INSERT INTO places (id, name, group_id) VALUES
    (301, 'The Main Wall', 201);
SELECT setval('places_id_seq', 301);


-- boulders (The 'routes' being climbed. Let's assume grades 1 and 2 from above)
INSERT INTO boulders (id, name, grade, userID, place) VALUES
                                                          (401, 'The Overhang Traverse', 1, 101, 301),
                                                          (402, 'Slab Perfection', 2, 101, 301);
SELECT setval('boulders_id_seq', 402);

-- active_sessions (The climbing session where the attempts happen, ID 501 will be used)
INSERT INTO active_sessions (id, user_id, group_id, place_id, name, created_at) VALUES
    (501, 102, 201, 301, 'Evening Session', NOW());
SELECT setval('active_sessions_id_seq', 501);

-- Current epoch time (in milliseconds)
-- We will use two dates: one a month ago, and one today.
SELECT EXTRACT(EPOCH FROM (NOW() - INTERVAL '1 month')) * 1000 AS one_month_ago_ms; -- ~2678400000000
SELECT EXTRACT(EPOCH FROM NOW()) * 1000 AS now_ms; -- ~2678400000000

-- Insert sample attempts (sends) for the active user (102) in the active session (501)
-- Insert 3 completed sends from a month ago
INSERT INTO route_attempts (route_id, attempts, completed, session, last_updated) VALUES
    (401, 4, TRUE, 501, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')) * 1000 + 10000), -- Completed send 1 (Boulder 401)
    (402, 1, TRUE, 501, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')) * 1000 + 20000), -- Completed send 2 (Boulder 402)
    (401, 5, TRUE, 501, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')) * 1000 + 30000); -- Completed send 3 (Boulder 401)

-- Insert 2 completed sends from today
INSERT INTO route_attempts (route_id, attempts, completed, session, last_updated) VALUES
    (402, 2, TRUE, 501, EXTRACT(EPOCH FROM NOW()) * 1000 + 10000), -- Completed send 4 (Boulder 402)
    (401, 3, TRUE, 501, EXTRACT(EPOCH FROM NOW()) * 1000 + 20000); -- Completed send 5 (Boulder 401)
-- Note: You should filter on `completed = TRUE` if you only want 'sends', but your original query counts all `ra.id`