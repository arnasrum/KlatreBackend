INSERT INTO roles(id, role_name) VALUES
    (0, 'owner' ),
    (1, 'admin'),
    (2, 'user');


INSERT INTO users (email, name)
VALUES
    ('a.rumbavicius@gmail.com', 'Arnas Rumbavicius'),
    ('a.rumbavi@gmail.com', 'Rune')
;

INSERT INTO klatre_groups(owner, name, personal)
VALUES
    (1, 'Personal', true),
    (2, 'Group 2', true)
    ;

INSERT INTO user_groups(user_id, group_id, role)
VALUES
    (1, 1, 0),
    (2, 1, 1),
    (2, 2, 0),
    (1, 2, 1)
;

INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES
    (1, 'french', 'sport', true),
    (2, 'yosemite', 'sport', true),
    (3, 'font', 'bouldering', true),
    (4, 'v-scale', 'bouldering', true);


INSERT INTO places(name, group_id)
VALUES
    ('LA', 1),
    ('Stavern', 1),
    ('Test', 2)
;



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

-- Insert all Yosemite grades with their numerical values
INSERT INTO grades(system_id, grade_string, numerical_value) VALUES
    (2, '5.4', 1),
    (2, '5.5', 2),
    (2, '5.6', 3),
    (2, '5.7', 4),
    (2, '5.8', 5),
    (2, '5.9', 6),
    (2, '5.10a', 7),
    (2, '5.10b', 8),
    (2, '5.10c', 9),
    (2, '5.10d', 10),
    (2, '5.11a', 11),
    (2, '5.11b', 12),
    (2, '5.11c', 13),
    (2, '5.11d', 14),
    (2, '5.12a', 15),
    (2, '5.12b', 16),
    (2, '5.12c', 17),
    (2, '5.12d', 18),
    (2, '5.13a', 19),
    (2, '5.13b', 20),
    (2, '5.13c', 21),
    (2, '5.13d', 22),
    (2, '5.14a', 23),
    (2, '5.14b', 24);

INSERT INTO grades(system_id, grade_string, numerical_value) VALUES
    (3, '3', 1),
    (3, '4', 2),
    (3, '5', 3),
    (3, '6A', 4),
    (3, '6A+', 5),
    (3, '6B', 6),
    (3, '6B+', 7),
    (3, '6C', 8),
    (3, '6C+', 9),
    (3, '7A', 10),
    (3, '7A+', 11),
    (3, '7B', 12),
    (3, '7B+', 13),
    (3, '7C', 14),
    (3, '7C+', 15),
    (3, '8A', 16),
    (3, '8A+', 17),
    (3, '8B', 18),
    (3, '8B+', 19),
    (3, '8C', 20),
    (3, '8C+', 21),
    (3, '9A', 22);

-- Insert all V-scale grades with their numerical values
INSERT INTO grades(system_id, grade_string, numerical_value) VALUES
    (4, 'V0', 1),
    (4, 'V1', 2),
    (4, 'V2', 3),
    (4, 'V3', 4),
    (4, 'V4', 5),
    (4, 'V5', 6),
    (4, 'V6', 7),
    (4, 'V7', 8),
    (4, 'V8', 9),
    (4, 'V9', 10),
    (4, 'V10', 11),
    (4, 'V11', 12),
    (4, 'V12', 13),
    (4, 'V13', 14),
    (4, 'V14', 15),
    (4, 'V15', 16),
    (4, 'V16', 17),
    (4, 'V17', 18);


INSERT INTO boulders (name, grade, userID, place)
VALUES
    ('svaberg', 6, 1, 1),
    ('sva', 7, 1, 1);

SELECT MAX(id) FROM grading_systems;
SELECT setval('grading_systems_id_seq', (SELECT MAX(id) FROM grading_systems));

/*
-- More generated data for klatre_group with id = 1 (owner: User 1, admin: User 2)

-- New Boulders in 'LA' (place id 1), added by User 1
INSERT INTO boulders (name, grade, userID, place)
VALUES
    -- Grade IDs for French (system_id 1) grades, from data.sql:
    -- '7a' (13), '7b' (15), '7c' (17)
    ('The Big One', 13, 1, 1), -- 7a
    ('Corner Crack', 15, 1, 1), -- 7b
    ('Slab Problem', 17, 1, 1); -- 7c

-- New Boulders in 'Stavern' (place id 2), added by User 2
INSERT INTO boulders (name, grade, userID, place)
VALUES
    -- Grade IDs for Font (system_id 3) grades, from data.sql:
    -- '7A' (10), '7A+' (11), '7B' (12)
    -- Assuming a placeholder grade for now, as places are set to use French by default (grade_system_id 1 in places table)
    -- Let's use more French grades for consistency with 'LA' since the 'places' table doesn't specify a non-default system in data.sql
    -- Using: '6a+' (8), '6b+' (10), '6c+' (12)
    ('Stavern Traverse', 8, 2, 2), -- 6a+
    ('The Dyno', 10, 2, 2), -- 6b+
    ('Overhang Stavern', 12, 2, 2); -- 6c+


-- Add route_sends (successful and unsuccessful) for both users on the new and existing boulders.
-- Existing boulders: 'svaberg' (grade 6, id 1), 'sva' (grade 7, id 2)
-- New boulders: 'The Big One' (id 3), 'Corner Crack' (id 4), 'Slab Problem' (id 5), 'Stavern Traverse' (id 6), 'The Dyno' (id 7), 'Overhang Stavern' (id 8)

-- User 1 (Arnas Rumbavicius) Sends
INSERT INTO route_sends (userID, boulderID, date, attempts, completed, perceivedGrade)
VALUES
    -- Send on existing boulders
    (1, 1, '2025-09-01 10:00:00+02', 1, TRUE, NULL), -- svaberg (6) - Flash
    (1, 2, '2025-09-01 11:00:00+02', 4, TRUE, '6a'), -- sva (7) - Sends
    (1, 2, '2025-09-08 11:00:00+02', 1, TRUE, '5c+'), -- Second send on sva, maybe they felt it was easier

    -- Sends on new 'LA' boulders (ids 3, 4, 5)
    (1, 3, '2025-09-15 15:30:00+02', 7, TRUE, '7a'), -- The Big One (13) - Sends after a few tries
    (1, 4, '2025-09-15 17:00:00+02', 2, FALSE, '7b'), -- Corner Crack (15) - Tries, fails
    (1, 5, '2025-09-22 10:00:00+02', 1, TRUE, '7c'), -- Slab Problem (17) - Flash (impressive!)

    -- Tries on new 'Stavern' boulders (ids 6, 7, 8)
    (1, 6, '2025-09-25 12:00:00+02', 3, TRUE, NULL), -- Stavern Traverse (8) - Sends
    (1, 7, '2025-09-25 14:00:00+02', 10, FALSE, '6b+'); -- The Dyno (10) - Project, not sent

-- User 2 (Rune) Sends
INSERT INTO route_sends (userID, boulderID, date, attempts, completed, perceivedGrade)
VALUES
    -- Tries on existing boulders
    (2, 1, '2025-09-05 09:00:00+02', 2, FALSE, NULL), -- svaberg (6) - Tries, fails
    (2, 2, '2025-09-05 10:30:00+02', 5, TRUE, NULL), -- sva (7) - Sends

    -- Sends/Tries on new 'LA' boulders (ids 3, 4, 5)
    (2, 3, '2025-09-18 16:00:00+02', 12, TRUE, '7a+'), -- The Big One (13) - Sends, felt a bit harder
    (2, 4, '2025-09-18 18:00:00+02', 4, TRUE, '7b'), -- Corner Crack (15) - Sends
    (2, 5, '2025-09-20 09:00:00+02', 8, FALSE, '7c+'), -- Slab Problem (17) - Project, not sent

    -- Sends on new 'Stavern' boulders (ids 6, 7, 8)
    (2, 6, '2025-09-27 11:00:00+02', 1, TRUE, '6a'), -- Stavern Traverse (8) - Flash, felt easier
    (2, 7, '2025-09-27 13:00:00+02', 6, TRUE, NULL), -- The Dyno (10) - Sends
    (2, 8, '2025-09-27 15:00:00+02', 9, TRUE, '6c+'); -- Overhang Stavern (12) - Sends
*/
