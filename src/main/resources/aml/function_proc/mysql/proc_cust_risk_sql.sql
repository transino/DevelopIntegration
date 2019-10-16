CREATE DEFINER=`aml`@`%` PROCEDURE `proc_cust_risk`(in v_type varchar(1),in dt_date varchar(20),v_str varchar(4000))
BEGIN
   /*
  客户评级检测:
  评级检测描述：检测哪些客户满足评级要求，然后开始进行评级
  模型参数引用：
  1.v_type:传入参数类型（1:当v_type为1时,v_str传入客户编号,多个客户编号以逗号隔开；2:当v_type为2时,v_str为空）
  2.dt_date:数据日期
  3.v_str:当v_type为2时，对数据日期为dt_date下的是客户信息表进行筛选，将满足评级条件的客户进行评级
  */ 
	declare v_risk_grade varchar(1); -- 风险等级
	declare v_advance_due_risk varchar(10); -- 提前重评天数
	declare v_risk_run_flag varchar(2); -- 提前前置条件状态
	
	begin
	set dt_date = cast(dt_date as date);
	-- 清空待评级客户中间表
	delete from aml_risk_cust;
	-- 获取当前使用的风险等级
	select constDetailNum into v_risk_grade from aml_const_detail where constNum = 'sys_056' and constDetailName = '1';
	-- 获取提前重评天数
	select constDetailName into v_advance_due_risk from aml_const_detail where constNum = 'sys_062' and constDetailNum = 'advance_due_risk';
	
	
	-- 当传入的是客户号拼接的字符串时
	IF v_type = '1' THEN
	-- 将拼接的字符串加载到临时表
	DROP table if exists aml_func_dist_str_tmp01;
	create table aml_func_dist_str_tmp01(str1 varchar(2000));
	select func_dist_str(v_str,',') into @isn;
	
	insert into aml_risk_cust
	select 
	uuid(),
	case when acpg.customer_no is not null then '2' else '1' end customer_type,
	afdst.str1
  from aml_func_dist_str_tmp01 afdst 
	left join (select distinct customer_no from aml_customer_public) acpg on acpg.customer_no = afdst.str1
	left join (select distinct customer_no from aml_customer_personal) acps on acps.customer_no = afdst.str1;
	
 ELSE
	-- 当传入的是表时
	 delete from aml_cust_susdata where date_id = cast(dt_date as date);
	    
		-- 对公可疑及黑名单记录条数
		insert into aml_cust_susdata
		select uuid(),dt_date,t1.customer_no,'2',t1.icount,t2.icount from 
		(
		select max(acp.customer_no) customer_no,
		COUNT(ascd.customer_no) icount -- 可疑记录条数
		from aml_customer_public acp
		left join aml_suspcs_case_detail ascd on acp.customer_no = ascd.customer_no and ascd.audit_state = '4' and ascd.audit_type_state = '1'
		where acp.date_id = dt_date
		) t1 left join
		(
		select max(acp.customer_no) customer_no,
		COUNT(ab.customer_name) icount -- 黑名单记录条数
		from aml_customer_public acp
		left join aml_blacklist ab on ab.status = '1' and (ab.customer_name = acp.customer_name and (ab.customer_name is not null or ab.customer_name <> '')) or
		(concat(acp.crtft_type,acp.crtft_number) = concat(ab.id_type ,ab.id_num) and  (ab.id_num is not null or ab.id_num<> '') )
		where acp.date_id = dt_date
		) t2 on t1.customer_no = t2.customer_no;
	
	
	
		-- 对私可疑及黑名单记录条数
		insert into aml_cust_susdata
		select uuid(),dt_date,t1.customer_no,'1',t1.icount,t2.icount from 
		(
		select max(acp.customer_no) customer_no,
		COUNT(ascd.customer_no) icount -- 可疑记录条数
		from Aml_Customer_Personal acp
		left join aml_suspcs_case_detail ascd on acp.customer_no = ascd.customer_no and ascd.audit_state = '4' and ascd.audit_type_state = '1'
		where acp.date_id = dt_date
		) t1 left join
		(
		select max(acp.customer_no) customer_no,
		COUNT(ab.customer_name) icount -- 黑名单记录条数
		from Aml_Customer_Personal acp
		left join aml_blacklist ab on ab.status = '1' and (ab.customer_name = acp.customer_name and (ab.customer_name is not null or ab.customer_name <> '')) or
		(concat(acp.crtft_type,acp.crtft_number) = concat(ab.id_type,ab.id_num) and  (ab.id_num is not null or ab.id_num <> '') )
		where acp.date_id = dt_date
		) t2 on t1.customer_no = t2.customer_no;
	
	
		-- 1、当客户新增审核通过的可疑事件被启用时.
		select constDetailName into v_risk_run_flag from aml_const_detail where constNum = 'sys_067' and constDetailNum = '1';
		if v_risk_run_flag = '1' then
				-- 更新评级名单表
				insert into aml_risk_cust
				select  uuid(),acs1_customer_type,acs1_customer_no from
				(
				select acs1.customer_type acs1_customer_type,acs1.customer_no acs1_customer_no,
				acs1.suscount acs1_suscount,asc2.customer_no asc2_customer_no ,asc2.suscount asc2_suscount
				from (select * from aml_cust_susdata where date_id = dt_date) acs1
				left join (select * from aml_cust_susdata where date_id = date_sub(dt_date,interval 1 day)) asc2 
				on acs1.customer_no = asc2.customer_no
				) t where  acs1_suscount  > asc2_suscount;
    end if;
		
		
			-- 2、客户信息触发黑名单被启用时.
			select constDetailName into v_risk_run_flag from aml_const_detail where constNum = 'sys_067' and constDetailNum = '2';
			if v_risk_run_flag = '1' then
						insert into aml_risk_cust
						select  uuid(),acs1_customer_type,acs1_customer_no from
						(
						select acs1.customer_type acs1_customer_type,acs1.customer_no acs1_customer_no,
						acs1.blackcount acs1_blackcount,asc2.customer_no asc2_customer_no ,asc2.blackcount asc2_blackcount
						from (select * from aml_cust_susdata where date_id = dt_date) acs1
						left join (select * from aml_cust_susdata where date_id = date_sub(dt_date,interval 1 day)) asc2 
						on acs1.customer_no = asc2.customer_no
						) t where  acs1_blackcount  > asc2_blackcount;
				 
		   end if;
       
	
      -- 3、当客户是评级期限即将到期被启用时.
			select constDetailName into v_risk_run_flag from aml_const_detail where constNum = 'sys_067' and constDetailNum = '3';
			if v_risk_run_flag = '1' then
					IF v_risk_grade = '3' then
						insert into aml_risk_cust
						select uuid(),ag.customer_type,ag.customer_no from aml_grade ag
						where grade_state = '1' and date_sub(risk_due_date3,interval v_advance_due_risk day) = sysdate();
						-- 更新到期的评级状态
						update aml_grade set grade_state = '2' where date_sub(risk_due_date3,interval v_advance_due_risk day) = sysdate();
					end if;
					IF v_risk_grade = '4' then
						insert into aml_risk_cust
						select uuid(),ag.customer_type,ag.customer_no from aml_grade ag
						where grade_state = '1' and date_sub(risk_due_date4,interval v_advance_due_risk day) = sysdate();
						-- 更新到期的评级状态
						update aml_grade set grade_state = '2' where date_sub(risk_due_date4,interval v_advance_due_risk day) = sysdate();
					end	if;
					IF v_risk_grade = '5' then
						insert into aml_risk_cust
						select uuid(),ag.customer_type,ag.customer_no from aml_grade ag
						where grade_state = '1' and date_sub(risk_due_date5,interval v_advance_due_risk day) = sysdate();
						-- 更新到期的评级状态
						update aml_grade set grade_state = '2' where date_sub(risk_due_date5,interval v_advance_due_risk day) = sysdate();
					end	if;	
			end	if;
			
			  -- 4、当客户信息更新时.
				select constDetailName into v_risk_run_flag from aml_const_detail where constNum = 'sys_067' and constDetailNum = '4';
						if v_risk_run_flag = '1' then 
								-- 对公
								insert into aml_risk_cust
								select  uuid(),'2',acp1.customer_no from aml_customer_public acp1 
								left join (select * from aml_customer_public where date_id = date_sub(dt_date,interval 1 day)) acp2 
								on acp1.customer_no = acp2.customer_no 
								where acp1.date_id = dt_date and acp1.update_date > acp2.update_date;
							    
								-- 对私
								insert into aml_risk_cust
								select  uuid(),'1',acp1.customer_no from Aml_Customer_Personal acp1 
								left join (select * from Aml_Customer_Personal where date_id = date_sub(dt_date,interval 1 day)) acp2 
								on acp1.customer_no = acp2.customer_no 
								where acp1.date_id = dt_date and acp1.update_date > acp2.update_date;
							    
							end if;
							            
				
				-- 5、当客户是新增客户，从来没有被评级时.
				select constDetailName into v_risk_run_flag from aml_const_detail where constNum = 'sys_067' and constDetailNum = '5';
						if v_risk_run_flag = '1' then
								-- 对公
								insert into aml_risk_cust
								select uuid(),'2',customer_no from aml_customer_public where date_id = dt_date and risk_flag = '0' and customer_no not in (select customer_no from aml_grade where customer_type = '2' and grade_state in ('0','1','3'));
								
								-- 个人
								insert into aml_risk_cust
								select uuid(),'1',customer_no from aml_customer_personal where date_id = dt_date and risk_flag = '0' and customer_no not in (select customer_no from aml_grade where customer_type = '1' and grade_state in ('0','1','3'));
								
						end	if;	
			

end if;
			
			
	-- 对公客户评级
   call proc_cust_dg_risk(dt_date);
	-- 个人客户评级
   call proc_cust_ds_risk(dt_date);
	
end;

end