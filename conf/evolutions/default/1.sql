# --- !Ups

create table "users" (
  "id" INTEGER PRIMARY KEY,
  "name" VARCHAR NOT NULL,
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

create table "news" (
  "id" INTEGER PRIMARY KEY,
  "cardinality" INTEGER NOT NULL,
  "type" VARCHAR NOT NULL,
  "createdAt" DATETIME NOT NULL,
  "updatedAt" DATETIME NOT NULL,
  "userId" VARCHAR,
  "referenceId" VARCHAR,
  FOREIGN KEY(userId) REFERENCES users(id)
  );

create table "images" (
  "id" INTEGER PRIMARY KEY,
  "path" VARCHAR,
  "createdAt" DATETIME NOT NULL,
  "updatedAt" DATETIME NOT NULL
  );

# --- !Downs

drop table "users";
drop table "achievements";
drop table "drinks";
drop table "news";
drop table "images";



