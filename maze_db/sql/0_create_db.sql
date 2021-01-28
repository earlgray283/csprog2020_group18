drop database if exists score_db;
create database if not exists score_db;

use score_db;

create table if not exists scores (
  id bigint auto_increment primary key,
  name varchar(32) not null,
  score int not null,
  created_at datetime(6) null
);