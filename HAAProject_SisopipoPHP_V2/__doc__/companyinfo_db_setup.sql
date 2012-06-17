CREATE DATABASE SP CHARACTER SET utf8 COLLATE utf8_general_ci;

create table Company(
	companyid MEDIUMINT NOT NULL primary key AUTO_INCREMENT,
	companyname varchar(200) not null,
	webhost varchar(120) null,
	mailhost varchar(120) null,
	tel varchar(25) null,
	phone varchar(15) null,
	email varchar(100) null,
	contactor varchar(200) null,
	address varchar(500) null,
	updatetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create index IDX_COMPANY_ID on Company (companyid);
create index IDX_COMPANY_NAME on Company (companyname);