CREATE DEFINER=`aml`@`%` FUNCTION `func_rule_in_risk`(values_str varchar(2000),cumpute_code varchar(200)) RETURNS varchar(2000) CHARSET utf8
BEGIN
	/*传入待计算值values_str，计算公式代码cumpute_code，根据字符串in返回评级分数*/
 -- 根据计算公式代码得到计算公式
	declare v_str varchar(1000);
	declare v_id varchar(100);
	declare v_value varchar(2000);
	declare num int;
	declare v_score varchar(100);
	declare ret_value varchar(20);
	declare i int;
	declare j int;
	declare ret_cur_rule_risk_001_1 varchar(20);
	
			insert into aml_func_dist_str_tmp01 
			select concat(auto_id,value,score) from Aml_Grade_Score where subitem_nos = cumpute_code;
	begin
	
			
			DECLARE cur_rule_risk_001_1 CURSOR FOR 
			select auto_id,value,score from Aml_Grade_Score where subitem_nos = cumpute_code;
			declare continue handler for not FOUND set ret_cur_rule_risk_001_1 = 1;
			
										 
			 OPEN cur_rule_risk_001_1;
							 cur_rule_risk_001_1_loop:LOOP
								 FETCH cur_rule_risk_001_1 into v_id,v_value,v_score;
								 
								  if ret_cur_rule_risk_001_1 = 1 THEN
	                     LEAVE cur_rule_risk_001_1_loop;
		              else
			               set i = 1;
		                 set j = length(v_value)-length(REPLACE(v_value,',',''))+1;
										 
										 delete from aml_func_dist_str_tmp01;
										 while i <= j do
												 insert into aml_func_dist_str_tmp01
												 select substring_index(substring_index(v_value,',',i),',',-1) str;
												 set i = i+1;
										 end while;	
			               select COUNT(1) into num 
			               from aml_func_dist_str_tmp01 where values_str in (select str1 from aml_func_dist_str_tmp01);
					 
												 if num > 0  then
												 
													set ret_value = v_score;
														
												 end if; 
												 
									  end if;
										
								end loop cur_rule_risk_001_1_loop;
			 close cur_rule_risk_001_1; 
			
			
			 if ret_value is null then
						set ret_value = '0';
			 end if;
			 
		 return ret_value;
		
   END; 
	 
END