select current_database();

select current_user;

create table account_master (
  id bigint,
  name varchar(255),
  rank int,
  url varchar(255),
  score numeric
);

insert into account_master (id, name, rank, url, score) values (1, 'John', 7, 'www.facebook.com', 0.21);
insert into account_master (id, name, rank, url, score) values (2, 'Mike', 19, 'www.google.com', 0.37);
insert into account_master (id, name, rank, url, score) values (3, 'Mark', 4, 'https://news.google.com/?hl=en-US&gl=US&ceid=US:en', 0.57);
insert into account_master (id, name, rank, url, score) values (4, 'Matt', 25, 'www.infinera.com', 0.99);
insert into account_master (id, name, rank, url, score) values (7, 'Mark', 37, 'www.cnn.com', 0.77);

\dt;

select * from account_master;



