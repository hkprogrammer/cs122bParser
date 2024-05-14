

DROP PROCEDURE IF EXISTS add_movie;
delimiter //
CREATE PROCEDURE add_movie (IN id CHAR(255),IN title CHAR(255), IN year int,IN director char(255), in star_id char(255), in genre_id int, OUT status char(255))
BEGIN
    DECLARE existing INT;
    SET existing = 0;
    SELECT COUNT(*) INTO existing FROM movies m WHERE m.id = id AND m.title = title AND m.year = year AND m.director = director;
    IF existing > 0 THEN
        SELECT "Error" INTO status;
    ELSE 
        #there isn't a record in the database yet
        INSERT INTO movies(id, title, director, year) VALUES(id, title, director, year);
        INSERT INTO genres_in_movies(movieId, genreId) VALUES(id, genre_id);
        INSERT INTO stars_in_movies(movieId,starId) VALUES(id, star_id);
        SELECT "Success" INTO status;
    END IF;
END//
delimiter ;

DROP PROCEDURE IF EXISTS add_genre;
delimiter //
CREATE PROCEDURE add_genre (IN genre_name CHAR(255), IN movie_id CHAR(255), OUT genre_id INT)
BEGIN
    DECLARE existing INT;
	DECLARE existing_in_movies CHAR(255);
    SELECT id INTO existing FROM genres WHERE name = genre_name LIMIT 1;
    IF existing IS NULL THEN
        INSERT INTO genres(name) VALUES (genre_name);
        SET existing = LAST_INSERT_ID(); 
    END IF;

	SELECT movie_id INTO existing_in_movies FROM movies WHERE id = movie_id;
    IF existing_in_movies IS NULL THEN
		#that means an error
        SET genre_id = -1;
	ELSE
		INSERT INTO genres_in_movies(genreId, movieId) VALUES(existing, movie_id);
		SET genre_id = existing;
	END IF;
END //
delimiter ;




DROP PROCEDURE IF EXISTS add_cast;
delimiter //
CREATE PROCEDURE add_cast (IN movie_id CHAR(255), IN star_name CHAR(255), OUT status INT)
# status 1 is inserted, 0 is not inserted
BEGIN
    DECLARE movieFound CHAR(255);
    DECLARE star_id CHAR(255);
    #SELECT the movie:
    SELECT title INTO movieFound FROM movies m WHERE m.id = movie_id;
    IF movieFound IS NOT NULL THEN
        # do stuff
        SELECT id INTO star_id FROM stars WHERE name = star_name;# has to be an identical match;
		IF star_id IS NOT NULL THEN
			INSERT INTO stars_in_movies (starId, movieId)
				SELECT star_id, movie_id
				WHERE NOT EXISTS (
					SELECT 1 FROM stars_in_movies sm WHERE sm.starId = star_id AND sm.movieId = movie_id);
			SELECT 1 INTO status;
		ELSE
			SELECT 0 INTO STATUS;
        END IF;
	ELSE 
		SELECT 0 INTO STATUS;
    END IF;

END //
delimiter ;

