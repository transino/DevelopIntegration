CREATE Function [dbo].[func_rule_compare_risk](@values_str varchar(2000),@cumpute_code varchar(200)) 
 Returns varchar(4000)
 As 
 /*传入待计算值values_str，计算公式代码cumpute_code，根据金额比较区间返回评级分数*/
 -- 根据计算公式代码得到计算公式
	Begin 
	declare @v_str varchar(1000);
	declare @v_id varchar(100);
	declare @v_value varchar(100);
	declare @v_score varchar(100);
	declare @left_str varchar(10);
	declare @right_str varchar(20);
	declare @ret_value varchar(20);
	declare @left_value DECIMAL(8,2);
	declare @right_value DECIMAL(8,2);

			-- 检查游标是否未被正常关闭
			declare @cur_rule_risk_2001001_1_isopen int;
			select @cur_rule_risk_2001001_1_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_rule_risk_2001001_1');
			-- 如果没有关闭则进行关闭
			if @cur_rule_risk_2001001_1_isopen > 0 
			begin
			  close cur_rule_risk_2001001_1;
			  deallocate cur_rule_risk_2001001_1;
			end  

			DECLARE cur_rule_risk_2001001_1 CURSOR FOR 
			select auto_id,value,score from Aml_Grade_Score where subitem_nos = @cumpute_code;
			-- 开启游标
			open cur_rule_risk_2001001_1;
		    fetch next from cur_rule_risk_2001001_1 into @v_id,@v_value,@v_score;
				--提取成功，进行下一条数据的提取操作
				while @@FETCH_STATUS = 0  
				begin
					select @v_value = replace(@v_value,' ','');
					select @left_str = left(@v_value,charindex('-',@v_value)-1);
					select @right_str = right(@v_value,charindex('-',@v_value)-1);
				
					if cast(@values_str as decimal(20,2)) >= cast(@left_str as decimal(20,2)) and cast(@values_str as decimal(20,2)) <= cast(@right_str as decimal(20,2))
						begin
						  select @ret_value = score from Aml_Grade_Score where auto_id = @v_id;
						end
				    fetch next from cur_rule_risk_2001001_1 into @v_id,@v_value,@v_score;
				end
		    -- 关闭游标	   
			close cur_rule_risk_2001001_1; 
			deallocate cur_rule_risk_2001001_1;
			
		 if @ret_value is null 
		 begin
		    set @ret_value = '0';
		 end
		 
		 return @ret_value;
		 

 End