CREATE DEFINER=`aml`@`%` FUNCTION `func_rule_compare_risk`(values_str varchar(2000),cumpute_code varchar(200)) RETURNS varchar(2000) CHARSET utf8
BEGIN
	/*传入待计算值values_str，计算公式代码cumpute_code，根据金额比较区间返回评级分数*/
 -- 根据计算公式代码得到计算公式
	declare v_str varchar(1000);
	declare v_id varchar(100);
	declare v_value varchar(100);
	declare v_score varchar(100);
	declare left_str varchar(10);
	declare right_str varchar(20);
	declare ret_value varchar(2000);
	declare ret_cur_rule_risk_2001001_1 varchar(20);
	
	begin
	
			DECLARE cur_rule_risk_2001001_1 CURSOR FOR 
					select auto_id,value,score from Aml_Grade_Score where subitem_nos = cumpute_code;
			declare continue handler for not FOUND set ret_cur_rule_risk_2001001_1 = 1;
			
									 
			 OPEN cur_rule_risk_2001001_1;
							 cur_rule_risk_2001001_1_loop:LOOP
								 FETCH cur_rule_risk_2001001_1 into v_id,v_value,v_score;
								  if ret_cur_rule_risk_2001001_1 = 1 THEN
	                     LEAVE cur_rule_risk_2001001_1_loop;
		              else
									
									 
									 select replace(v_value,' ','') into v_value;
									 select left(v_value,LOCATE('-',v_value)-1) into left_str;
									 select right(v_value,length(v_value)-LOCATE('-',v_value)) into right_str;
									 insert into aml_func_dist_str_tmp01 select concat(left_str,' ',right_str);
									 if(values_str <> '') then
										 if cast(values_str as DECIMAL(20,2)) >= cast(left_str as DECIMAL(20,2)) and cast(values_str as DECIMAL(20,2))<= cast(right_str as DECIMAL(20,2)) then
												select score into ret_value from Aml_Grade_Score where auto_id = v_id;
										 end if;
									 else
									 set ret_value = '0';
									 end if;
									 end if;
								end loop cur_rule_risk_2001001_1_loop;
			 close cur_rule_risk_2001001_1; 
			
			
			 if ret_value is null then
						set ret_value = '0';
			 end if;
			 
		 return ret_value;
		
   END; 
	 
end