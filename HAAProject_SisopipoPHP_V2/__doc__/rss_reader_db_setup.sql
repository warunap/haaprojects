create table READER_RSS (
	rlink varchar(200) NOT NULL primary key,
	rtitle varchar(200),
	tags varchar(200),
	updatetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
create index IDX_RSSREADER_rlink on READER_RSS (rlink);
create index IDX_RSSREADER_rtitle on READER_RSS (rtitle);
create index IDX_RSSREADER_tags on READER_RSS (tags);