# --- !Ups

create table "USERS" ("id" INTEGER PRIMARY KEY,"userName" VARCHAR NOT NULL,"createdAt" DATETIME NOT NULL);

# --- !Downs

drop table "USERS";
