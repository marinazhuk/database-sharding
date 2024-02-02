CREATE TABLE books (
                       id bigint not null PRIMARY KEY,
                       category_id int not null,
                       author character varying not null,
                       title character varying not null,
                       year int not null );

CREATE INDEX books_category_id_idx ON books USING btree(category_id);
CREATE INDEX books_year_idx ON books USING btree(year);