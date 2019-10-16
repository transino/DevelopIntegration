delete from Aml_Const where ConstNum = 'sys_998';
delete from Aml_Const_Detail where ConstNum = 'sys_998';
insert into Aml_Const select 'sys_998','初始密码强制修改标识';
insert into Aml_Const_Detail select 'FE73AABA-CA00-4E8C-B773-24E1E173C06E','sys_update_password','2','sys_998','系统初始化密码强制修改标识(1：强制；2：不强制)';
delete from Aml_Const where ConstNum = 'sys_997';
delete from Aml_Const_Detail where ConstNum = 'sys_997';
insert into Aml_Const select 'sys_997','密码正则表达式中文映射';
insert into Aml_Const_Detail select 'FE73AABA-CA00-4E8C-B773-24E1E173C06F','0001','密码必须由8-16位字母、数字、特殊符号组成','sys_997','^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";''<>?,.\/]).{8,16}';
