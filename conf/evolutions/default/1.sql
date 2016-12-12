# --- !Ups

create table "users" (
  "id" INTEGER PRIMARY KEY,
  "userName" VARCHAR NOT NULL,
  "createdAt" DATETIME NOT NULL,
  "updatedAt" DATETIME NOT NULL
);

create table "achievements" (
  "id" INTEGER PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "imagePath" VARCHAR NOT NULL,
  "description" VARCHAR NOT NULL,
  "createdAt" DATETIME NOT NULL,
  "updatedAt" DATETIME NOT NULL
  );
create table "drinks" (
  "id" INTEGER PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "type" VARCHAR NOT NULL,
  "createdAt" DATETIME NOT NULL,
  "updatedAt" DATETIME NOT NULL
  );

# --- !Downs

drop table "users";
drop table "achievements";
drop table "drinks";



