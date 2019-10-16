CREATE Function [dbo].[func_rule_in_risk](@values_str varchar(2000),@cumpute_code varchar(200)) 
 Returns varchar(4000)
 As 
 /*传入待计算值values_str，计算公式代码cumpute_code，根据字符串in返回评级分数*/
 -- 根据计算公式代码得到计算公式
	Begin 
	declare @v_str varchar(1000);
	declare @v_id varchar(100);
	declare @v_value varchar(2000);
	declare @num int;
	declare @v_score varchar(100);
	declare @ret_value varchar(20);
	
	declare @aml_func_rule_in_risk_tmp01 table
		 (
		 str1 varchar(4000)
		 );

			-- 检查游标是否未被正常关闭
			declare @cur_rule_in_risk_001_1_isopen int;
			select @cur_rule_in_risk_001_1_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_rule_risk_001_1');
			-- 如果没有关闭则进行关闭
			if @cur_rule_in_risk_001_1_isopen > 0 
			begin
			  close cur_rule_risk_001_1;
			  deallocate cur_rule_risk_001_1;
			end  

			DECLARE cur_rule_risk_001_1 CURSOR FOR 
			select auto_id,value,score from Aml_Grade_Score where subitem_nos = @cumpute_code;
			-- 开启游标
			open cur_rule_risk_001_1;
		    fetch next from cur_rule_risk_001_1 into @v_id,@v_value,@v_score;
				--提取成功，进行下一条数据的提取操作
				while @@FETCH_STATUS = 0  
				begin
				     
					 select @v_value = replace(@v_value,' ','');
					 delete from @aml_func_rule_in_risk_tmp01;
					 set @v_value = @v_value+',' 
					 Declare @insertStr varchar(1000) --截取后的第一个字符串 
					 Declare @newstr varchar(1000) --截取第一个字符串后剩余的字符串 
					 set @insertStr = left(@v_value,charindex(',',@v_value)-1) 
					 set @newstr = stuff(@v_value,1,charindex(',',@v_value),'') 
					 Insert @aml_func_rule_in_risk_tmp01 Values(@insertStr) 
					 while(len(@newstr)>0) 
					 begin 
						 set @insertStr = left(@newstr,charindex(',',@newstr)-1) 
						 Insert @aml_func_rule_in_risk_tmp01 Values(@insertStr) 
						 set @newstr = stuff(@newstr,1,charindex(',',@newstr),'') 
					 end 
					 
					 select @num = COUNT(1) from @aml_func_rule_in_risk_tmp01 where @values_str in (select str1 from @aml_func_rule_in_risk_tmp01)
					 
					 if @num > 0 
					 begin
					 
					  set @ret_value = @v_score;
					    
					 end
					 	
				    fetch next from cur_rule_risk_001_1 into @v_id,@v_value,@v_score;
				end
				
		    -- 关闭游标	   
			close cur_rule_risk_001_1; 
			deallocate cur_rule_risk_001_1;
			
			
			 if @ret_value is null or @ret_value = ''
			 begin
				set @ret_value = '0';
			 end
			 return @ret_value;
		 

 End