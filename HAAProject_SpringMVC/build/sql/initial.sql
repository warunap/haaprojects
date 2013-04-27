create database mvc DEFAULT CHARACTER SET 'utf8';

drop table Foo;
drop table Petclinic;
create table Foo(ID varchar2(32) primary key,name varchar2(100) not null);
create table Petclinic(ID varchar2(32) primary key,name varchar2(100) not null);
commit;