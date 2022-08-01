INSERT INTO rating(rating_value)
SELECT 'G'
WHERE NOT EXISTS (SELECT 1 FROM rating r WHERE r.rating_value = 'G');

INSERT INTO rating(rating_value)
SELECT 'PG'
WHERE NOT EXISTS (SELECT 1 FROM rating r WHERE r.rating_value = 'PG');

INSERT INTO rating(rating_value)
SELECT 'PG-13'
WHERE NOT EXISTS (SELECT 1 FROM rating r WHERE r.rating_value = 'PG-13');

INSERT INTO rating(rating_value)
SELECT 'R'
WHERE NOT EXISTS (SELECT 1 FROM rating r WHERE r.rating_value = 'R');

INSERT INTO rating(rating_value)
SELECT 'NC-17'
WHERE NOT EXISTS (SELECT 1 FROM rating r WHERE r.rating_value = 'NC-17');



INSERT INTO genre(name)
SELECT 'Комедия'
WHERE NOT EXISTS (SELECT 1 FROM genre g WHERE g.name = 'Комедия');

INSERT INTO genre(name)
SELECT 'Драма'
WHERE NOT EXISTS (SELECT 1 FROM genre g WHERE g.name = 'Драма');

INSERT INTO genre(name)
SELECT 'Мультфильм'
WHERE NOT EXISTS (SELECT 1 FROM genre g WHERE g.name = 'Мультфильм');

INSERT INTO genre(name)
SELECT 'Триллер'
WHERE NOT EXISTS (SELECT 1 FROM genre g WHERE g.name = 'Триллер');

INSERT INTO genre(name)
SELECT 'Документальный'
WHERE NOT EXISTS (SELECT 1 FROM genre g WHERE g.name = 'Документальный');

INSERT INTO genre(name)
SELECT 'Боевик'
WHERE NOT EXISTS (SELECT 1 FROM genre g WHERE g.name = 'Боевик');
