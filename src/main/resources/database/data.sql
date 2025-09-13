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
