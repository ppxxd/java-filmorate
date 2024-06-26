DELETE FROM mpa;
INSERT INTO mpa (name)
VALUES ('G'), --  — у фильма нет возрастных ограничений
       ('PG'), -- — детям рекомендуется смотреть фильм с родителями
       ('PG-13'), --  — детям до 13 лет просмотр не желателен
       ('R'), -- — лицам до 17 лет просматривать фильм можно только в присутствии взрослого
       ('NC-17'); --  — лицам до 18 лет просмотр запрещён

DELETE FROM genres;
INSERT INTO genres (GENRE_ID, NAME)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');