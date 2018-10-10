
------------创建命令表------------
create table cmds(
	cmd varchar(100) not null, --命令,同种类型的命令不能有多条
	type varchar(20) not null --类型,可执行程序,目录或文件
);

------------创建路径表------------
create table paths(
	path varchar(250) not null, --程序或目录的路径
	tag varchar(100) default null --对该路径的说明
);

------------z------------
create table c_p(
	c_id int not null, --cmds uid
	p_id int not null --paths uid
);