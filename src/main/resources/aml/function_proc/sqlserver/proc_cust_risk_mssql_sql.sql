USE [aml]
GO
/****** Object:  StoredProcedure [dbo].[proc_cust_risk]    Script Date: 06/05/2019 14:27:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER  procedure [dbo].[proc_cust_risk]
@v_type varchar(1),
@dt_date varchar(200),
@v_str varchar(4000)
as
begin
   /*
  客户评级检测:
  评级检测描述：检测哪些客户满足评级要求，然后开始进行评级
  模型参数引用：
  1.v_type:传入参数类型（1:当v_type为1时,v_str传入客户编号,多个客户编号以逗号隔开；2:当v_type为2时,v_str为空）
  2.v_str:当v_type为2时，对数据日期为dt_date下的是客户信息表进行筛选，将满足评级条件的客户进行评级
  3.dt_date:数据日期
  */ 
	declare @v_risk_grade varchar(10); -- 风险等级
	declare @v_advance_due_risk varchar(10); -- 提前重评天数
	declare @v_risk_run_flag varchar(2); -- 提前前置条件状态
	begin
	
	-- 清空待评级客户中间表
	delete from aml_risk_cust;
	-- 获取当前使用的风险等级
	select @v_risk_grade = constDetailNum from aml_const_detail where constNum = 'sys_056' and constDetailName = '1';
	-- 获取提前重评天数
	select @v_advance_due_risk = constDetailName from aml_const_detail where constNum = 'sys_062' and constDetailNum = 'advance_due_risk';
	set @dt_date = CAST(@dt_date as date);
	-- 当传入的是客户号拼接的字符串时
	IF @v_type = '1' 
		begin
            
			  declare @sql varchar(1000)
			  declare @str_tbl table
			  (
			  str varchar(200)
			  );
			  if charindex(',',@v_str) = 0
				  begin
				  insert into @str_tbl
				   select @v_str
				  end
			  else
				  begin
				  while(charindex(',',@v_str)<>0)
					  begin
					   --第一个','之前的字符串
					   insert into @str_tbl
					   select substring(@v_str,1,charindex(',',@v_str)-1)
					   --将第一个','后面的字符串重新赋给@ids
					   set @v_str=stuff(@v_str,1,charindex(',',@v_str),'')
					   --最后一个字符串
						   if(charindex(',',@v_str)=0)
						   begin
							  insert into @str_tbl values(@v_str)
						   end
					  end
				  end
				  
			insert into aml_risk_cust
			select 
			newid(),
			case when acpg.customer_no is not null then '2' else '1' end customer_type,
			afdst.str
			from @str_tbl afdst 
			left join (select distinct customer_no from aml_customer_public) acpg on acpg.customer_no = afdst.str
			left join (select distinct customer_no from aml_customer_personal) acps on acps.customer_no = afdst.str;
		end	
	ELSE
	  begin
		-- 当传入的是表时
	    
	    delete from aml_cust_susdata where date_id = @dt_date;
	    
		-- 对公可疑及黑名单记录条数
		insert into aml_cust_susdata
		select newid(),@dt_date,t1.customer_no,'2',t1.icount,t2.icount from 
		(
		select max(acp.customer_no) customer_no,
		COUNT(ascd.customer_no) icount --可疑记录条数
		from aml_customer_public acp
		left join aml_suspcs_case_detail ascd on acp.customer_no = ascd.customer_no and ascd.audit_state = '4' and ascd.audit_type_state = '1'
		where acp.date_id = @dt_date
		) t1 left join
		(
		select max(acp.customer_no) customer_no,
		COUNT(ab.customer_name) icount --黑名单记录条数
		from aml_customer_public acp
		left join aml_blacklist ab on ab.status = '1' and (ab.customer_name = acp.customer_name and (ab.customer_name is not null or ab.customer_name <> '')) or
		(cast(acp.crtft_type as varchar) + cast(acp.crtft_number as varchar) = CAST(ab.id_type as varchar) + CAST(ab.id_num as varchar) and  (ab.id_num is not null or ab.id_num <> '') )
		where acp.date_id = @dt_date
		) t2 on t1.customer_no = t2.customer_no;
		
		-- 对私可疑及黑名单记录条数
		insert into aml_cust_susdata
		select newid(),@dt_date,t1.customer_no,'1',t1.icount,t2.icount from 
		(
		select max(acp.customer_no) customer_no,
		COUNT(ascd.customer_no) icount --可疑记录条数
		from Aml_Customer_Personal acp
		left join aml_suspcs_case_detail ascd on acp.customer_no = ascd.customer_no and ascd.audit_state = '4' and ascd.audit_type_state = '1'
		where acp.date_id = @dt_date
		) t1 left join
		(
		select max(acp.customer_no) customer_no,
		COUNT(ab.customer_name) icount --黑名单记录条数
		from Aml_Customer_Personal acp
		left join aml_blacklist ab on ab.status = '1' and (ab.customer_name = acp.customer_name and (ab.customer_name is not null or ab.customer_name <> '')) or
		(cast(acp.crtft_type as varchar) + cast(acp.crtft_number as varchar) = CAST(ab.id_type as varchar) + CAST(ab.id_num as varchar) and  (ab.id_num is not null or ab.id_num <> '') )
		where acp.date_id = @dt_date
		) t2 on t1.customer_no = t2.customer_no;
		
	
	
		-- 1、当客户新增审核通过的可疑事件被启用时.
		select @v_risk_run_flag = constDetailName from aml_const_detail where constNum = 'sys_067' and constDetailNum = '1';
		if @v_risk_run_flag = '1'
		   begin
				-- 更新评级名单表
				insert into aml_risk_cust
				select  newid(),acs1_customer_type,acs1_customer_no from
				(
				select acs1.customer_type acs1_customer_type,acs1.customer_no acs1_customer_no,
				acs1.suscount acs1_suscount,asc2.customer_no asc2_customer_no ,asc2.suscount asc2_suscount
				from (select * from aml_cust_susdata where date_id = @dt_date) acs1
				left join (select * from aml_cust_susdata where date_id = dateadd(day,-1,@dt_date)) asc2 
				on acs1.customer_no = asc2.customer_no
				) t where  acs1_suscount  > asc2_suscount
           end
		
		
			-- 2、客户信息触发黑名单被启用时.
			select @v_risk_run_flag = constDetailName from aml_const_detail where constNum = 'sys_067' and constDetailNum = '2';
			if @v_risk_run_flag = '1'
					   begin
						insert into aml_risk_cust
						select  newid(),acs1_customer_type,acs1_customer_no from
						(
						select acs1.customer_type acs1_customer_type,acs1.customer_no acs1_customer_no,
						acs1.blackcount acs1_blackcount,asc2.customer_no asc2_customer_no ,asc2.blackcount asc2_blackcount
						from (select * from aml_cust_susdata where date_id = @dt_date) acs1
						left join (select * from aml_cust_susdata where date_id = dateadd(day,-1,@dt_date)) asc2 
						on acs1.customer_no = asc2.customer_no
						) t where  acs1_blackcount  > asc2_blackcount
				 
		    end
       
       
       		-- 3、当客户是评级期限即将到期被启用时.
			select @v_risk_run_flag = constDetailName from aml_const_detail where constNum = 'sys_067' and constDetailNum = '3';
			if @v_risk_run_flag = '1'
				begin
					IF @v_risk_grade = '3'
						begin
							insert into aml_risk_cust
							select newid(),ag.customer_type,ag.customer_no from aml_grade ag
							where grade_state = '1' and dateadd(DAY,-CAST(@v_advance_due_risk as int),risk_due_date3) = getdate();
							--更新到期的评级状态
							update aml_grade set grade_state = '2' where dateadd(DAY,1,risk_due_date3) = getdate();
						end
					ELSE IF @v_risk_grade = '4'
						begin
							insert into aml_risk_cust
							select newid(),ag.customer_type,ag.customer_no from aml_grade ag
							where grade_state = '1' and dateadd(DAY,-CAST(@v_advance_due_risk as int),risk_due_date4) = getdate();
							--更新到期的评级状态
							update aml_grade set grade_state = '2' where dateadd(DAY,1,risk_due_date4) = getdate();
						end	
					else if  @v_risk_grade = '5'
						begin
							insert into aml_risk_cust
							select newid(),ag.customer_type,ag.customer_no from aml_grade ag
							where grade_state = '1' and dateadd(DAY,-CAST(@v_advance_due_risk as int),risk_due_date5) = getdate();
							--更新到期的评级状态
							update aml_grade set grade_state = '2' where dateadd(DAY,1,risk_due_date5) = getdate();
						end	
				end;
			
			
			    -- 4、当客户信息更新时.
				select @v_risk_run_flag = constDetailName from aml_const_detail where constNum = 'sys_067' and constDetailNum = '4';
						if @v_risk_run_flag = '1'
							begin
									      
								--对公
								insert into aml_risk_cust
								select  newid(),'2',acp1.customer_no from aml_customer_public acp1 
								left join (select * from aml_customer_public where date_id = dateadd(day,-1,@dt_date)) acp2 
								on acp1.customer_no = acp2.customer_no 
								where acp1.date_id = @dt_date and acp1.update_date > acp2.update_date
							    
								--对私
								insert into aml_risk_cust
								select  newid(),'1',acp1.customer_no from Aml_Customer_Personal acp1 
								left join (select * from Aml_Customer_Personal where date_id = dateadd(day,-1,@dt_date)) acp2 
								on acp1.customer_no = acp2.customer_no 
								where acp1.date_id = @dt_date and acp1.update_date > acp2.update_date
							    
							end
							            
				
				-- 5、当客户是新增客户，从来没有被评级时.
				select @v_risk_run_flag = constDetailName from aml_const_detail where constNum = 'sys_067' and constDetailNum = '5';
						if @v_risk_run_flag = '1'
							begin
				 
								--对公
								insert into aml_risk_cust
								select newid(),'2',customer_no from aml_customer_public where date_id = cast(@dt_date as date) and risk_flag = '0'
								and customer_no not in (select customer_no from aml_grade where customer_type = '2' and grade_state in ('1','3'));
								
								-- 个人
								insert into aml_risk_cust
								select newid(),'1',customer_no from aml_customer_personal where date_id = cast(@dt_date as date) and risk_flag = '0'
								and customer_no not in (select customer_no from aml_grade where customer_type = '1' and grade_state in ('1','3'));
								
							end		
			end	
        
	-- 对公客户评级
    exec proc_cust_dg_risk @dt_date;
	-- 个人客户评级
    -- exec proc_cust_ds_risk @dt_date;

    end;
    
  end;