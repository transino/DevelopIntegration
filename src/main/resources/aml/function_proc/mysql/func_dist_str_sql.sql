CREATE DEFINER=`aml`@`%` FUNCTION `func_dist_str`(source_str varchar(2000),cut_str varchar(200)) RETURNS varchar(2000) CHARSET utf8
begin
		 declare v_sql varchar(4000);
		 declare i int;
		 declare j int;
		 set i = 1;
		 set j = length(source_str)-length(REPLACE(source_str,cut_str,''))+1;
		 -- 清除临时表的数据
		 delete from aml_func_dist_str_tmp01;
     while i <= j do
		     insert into aml_func_dist_str_tmp01
         select substring_index(substring_index(source_str,cut_str,i),cut_str,-1) str;
				 set i = i+1;
     end while;	 
		 select GROUP_CONCAT(distinct str1) into @v_str from aml_func_dist_str_tmp01;
     return  @v_str;
     end