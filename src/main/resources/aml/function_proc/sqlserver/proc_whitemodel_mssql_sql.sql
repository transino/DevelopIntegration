CREATE procedure [dbo].[proc_whitemodel]
@dt_date date
as
begin
   /*
    白名单模型代码：
    白名单模型描述：前置白名单，命中可疑交易后进行提升，不删除；后置白名单，命中可疑交易后进行删除，不提示
    白名单模型参数引用：
	1、white_front_back_position:白名单前后置配置
	2、white_list_active：白名单作用位置
  */ 
	
	declare @v_suspcs_code varchar(8); -- 可疑事件识别码
 	declare @v_trans_code  varchar(32); -- 可疑交易编号
 	declare @v_dt_date     varchar(20); -- 数据日期
 	declare @white_front_back_position        varchar(20); -- 白名单前后置配置
 	declare @white_list_active        varchar(20); -- 白名单作用位置
 	declare @white_remarks        varchar(2000); -- 白名单提示
	declare @str varchar(2000);
	declare @white_tbl table
	(
	    trans_code varchar(200),  --  交易特征
	    v_aosp varchar(2000)      -- 疑点分析
	    
	);
	
	declare @aml_func_dist_str_tmp01 table
		 (
		 str1 varchar(4000)
		 );
             
	begin
	
	    -- 基础参数获取
	    select @white_front_back_position = paramValue from aml_modelparam where paramCode = 'white_front_back_position';
		select @white_list_active = paramValue  from aml_modelparam where paramCode = 'white_list_active';
		
		select @v_dt_date = @dt_date;
		
		delete from @white_tbl;
		delete from @aml_func_dist_str_tmp01;
		 -- 格式化已参与白名单的可疑模型代码
		 select @str = susModelCodes from aml_modelparam where paramCode = 'white_front_back_position' 
		 set @str = @str+',' 
		 Declare @insertStr varchar(50) --截取后的第一个字符串 
		Declare @newstr varchar(1000) --截取第一个字符串后剩余的字符串 
		set @insertStr = left(@str,charindex(',',@str)-1) 
		 set @newstr = stuff(@str,1,charindex(',',@str),'') 
		 Insert @aml_func_dist_str_tmp01 Values(@insertStr) 
		 while(len(@newstr)>0) 
		 begin 
		 set @insertStr = left(@newstr,charindex(',',@newstr)-1) 
		 Insert @aml_func_dist_str_tmp01 Values(@insertStr) 
		 set @newstr = stuff(@newstr,1,charindex(',',@newstr),'') 
         end
        
		--当白名单作用于客户姓名时
		if @white_list_active = '1' or @white_list_active = '3'
		begin
		 
		 insert into @white_tbl
		 select trans_code,'当前可疑主体名称命中白名单' from aml_suspcs_case_detail t where date_id = @v_dt_date
			    and t.SENM in (select customer_name from Aml_Whitelist where status = '1')
		        and suspcs_model_code in (select str1 from @aml_func_dist_str_tmp01);
		        
		end
		
		--当白名单作用于客户证件时
		if @white_list_active = '2' or @white_list_active = '3'
		begin
		 
		 insert into @white_tbl
		 select trans_code,'当前可疑主体证件命中白名单' from aml_suspcs_case_detail t where date_id = @v_dt_date
			    and convert(varchar,t.SETP)+convert(varchar,SEID) in (select convert(varchar,id_type)+convert(varchar,id_num) from Aml_Whitelist where status = '1')
			    and suspcs_model_code in (select str1 from @aml_func_dist_str_tmp01);
		end
		
		
		--当白名单后置时
		if @white_front_back_position = '2'
		
			begin
			
			   -- 删除命中白名单的可疑记录
			   delete from aml_suspcs_case_detail where date_id = @v_dt_date and trans_code in (select trans_code from @white_tbl)
			   -- 删除明细为空的事件表
			   delete from aml_suspcs_case where date_id = @v_dt_date and auto_id not in
			   (
			   select distinct asc1.auto_id from aml_suspcs_case asc1
			   inner join aml_suspcs_case_detail ascd on 
			   asc1.suspcs_cdoe = ascd.suspcs_cdoe 
			   and ascd.date_id = @v_dt_date
			   where asc1.date_id = @v_dt_date
			   );
			end
		-- 其他	
		else
		
		   begin 
		   
		    --更新可疑明细表疑点分析
		    update aml_suspcs_case_detail  set AOSP = 
		    AOSP+';'+dbo.func_dist_str(stuff((select ','+ v_aosp from (select distinct v_aosp v_aosp from @white_tbl where trans_code = aml_suspcs_case_detail.trans_code) t  for xml path('')),1,1,''),',') 
		    where date_id = @v_dt_date and trans_code in (select trans_code from @white_tbl);
			
			--更新可疑事件表疑点分析
			update aml_suspcs_case  set AOSP = 
			dbo.func_dist_str(stuff((select ','+ aosp from (select distinct aosp from aml_suspcs_case_detail where date_id = @v_dt_date and suspcs_cdoe = aml_suspcs_case.suspcs_cdoe) t for xml path('')),1,1,''),',')
			where date_id = @v_dt_date;
			 
			   
		   end	
		
		
   end

end