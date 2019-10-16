CREATE DEFINER=`aml`@`%` PROCEDURE `proc_cust_ds_risk`(in dt_date varchar(20))
BEGIN
   /*
  对私客户评级模型:
  评级模型描述：23项常规大项+1项附加大项
  模型参数引用：
  */  
	declare v_grade_code varchar(50); -- 评级编码
 	declare v_risk_section_code  varchar(32); -- 大项编号
 	declare v_risk_section_name  varchar(512); -- 大项名称
 	declare v_dt_date     varchar(20); -- 数据日期
	declare null_flag        varchar(10); -- 空标识
	declare v_customer_no        varchar(200); -- 客户编号
	declare v_cols        varchar(200); -- 字段名称
	declare v_cols_count        varchar(200); -- 字段内容大小
	declare ret_cur_risk_customer_ int DEFAULT 0;
	declare ret_cur_cust_risk int DEFAULT 0;
	declare ret_cur_cust_risk_1001001 int DEFAULT 0;
	declare v_risk_grade varchar(1); -- 风险等级
  
declare v_risk_ds_1001001 decimal(20,2); -- 客户信息的公开程度-基本项完整度
declare v_risk_ds_1001 decimal(20,2); -- 客户信息的公开程度汇总分数
declare v_risk_ds_1002001 decimal(20,2); -- 与客户建立或维持业务关系的渠道-与客户建立或维护业务关系的渠道
declare v_risk_ds_1002 decimal(20,2); -- 与客户建立或维持业务关系的渠道汇总分数
declare v_risk_ds_1003001 decimal(20,2); -- 客户所持身份证件或身份证明文件的种类-客户信息-证件类型
declare v_risk_ds_1003 decimal(20,2); -- 客户所持身份证件或身份证明文件的种类汇总分数
declare v_risk_ds_1004001 decimal(20,2); -- 反洗钱交易检测记录-客户通过终审的可疑交易笔数
declare v_risk_ds_1004 decimal(20,2); -- 反洗钱交易检测记录汇总分数
declare v_risk_ds_1005001 decimal(20,2); -- 涉及客户的风险提示信息或权威媒体报道信息-客户姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对私)
declare v_risk_ds_1005 decimal(20,2); -- 涉及客户的风险提示信息或权威媒体报道信息汇总分数
declare v_risk_ds_1006001 decimal(20,2); -- 客户其他风险-自然人客户年龄
declare v_risk_ds_1006 decimal(20,2); -- 客户其他风险汇总分数
declare v_risk_ds_1007001 decimal(20,2); -- 反洗钱、反恐怖融资监控或制载情况-客户国籍(地区)被我国反洗钱监控或制载(对私)
declare v_risk_ds_1007 decimal(20,2); -- 反洗钱、反恐怖融资监控或制载情况汇总分数
declare v_risk_ds_1008001 decimal(20,2); -- 风险提示信息-客户国籍(地区)被FATF/APG/EAG等权威组织互评风险结果提示(对私)
declare v_risk_ds_1008 decimal(20,2); -- 风险提示信息汇总分数
declare v_risk_ds_1009001 decimal(20,2); -- 上游犯罪状况-客户国籍(地区)处于FATF发布的严重存在毒品、走私、诈骗等上游犯罪情况的国家(地区)(对私)
declare v_risk_ds_1009 decimal(20,2); -- 上游犯罪状况汇总分数
declare v_risk_ds_1010001 decimal(20,2); -- 特殊的金融监管风险-客户出生地与金融机构相距过远
declare v_risk_ds_1010002 decimal(20,2); -- 特殊的金融监管风险-客户住址与金融机构相距过远
declare v_risk_ds_1010 decimal(20,2); -- 特殊的金融监管风险汇总分数
declare v_risk_ds_1011001 decimal(20,2); -- 地域其他风险-无
declare v_risk_ds_1011 decimal(20,2); -- 地域其他风险汇总分数
declare v_risk_ds_1012001 decimal(20,2); -- 与现金关联程度-客户一年内发生的现金交易总额
declare v_risk_ds_1012 decimal(20,2); -- 与现金关联程度汇总分数
declare v_risk_ds_1013001 decimal(20,2); -- 非面对面交易-客户一年内发生的非柜台业务交易总额
declare v_risk_ds_1013 decimal(20,2); -- 非面对面交易汇总分数
declare v_risk_ds_1014001 decimal(20,2); -- 跨境交易-客户一年内产生的可疑跨境交易总额(对私)
declare v_risk_ds_1014 decimal(20,2); -- 跨境交易汇总分数
declare v_risk_ds_1015001 decimal(20,2); -- 代理交易-客户一年内产生的代理交易总额
declare v_risk_ds_1015 decimal(20,2); -- 代理交易汇总分数
declare v_risk_ds_1016001 decimal(20,2); -- 特殊业务类型的交易频率-特殊类型账户的开户数量
declare v_risk_ds_1016002 decimal(20,2); -- 特殊业务类型的交易频率-特殊类型账户的销户数量
declare v_risk_ds_1016 decimal(20,2); -- 特殊业务类型的交易频率汇总分数
declare v_risk_ds_1017001 decimal(20,2); -- 金融其他风险-自然人一年内跨境汇款频率
declare v_risk_ds_1017 decimal(20,2); -- 金融其他风险汇总分数
declare v_risk_ds_1018001 decimal(20,2); -- 是否属于公认的较高风险的行业-个人职业
declare v_risk_ds_1018 decimal(20,2); -- 是否属于公认的较高风险的行业汇总分数
declare v_risk_ds_1019001 decimal(20,2); -- 是否与特定洗钱风险关联-身份为外国政要
declare v_risk_ds_1019 decimal(20,2); -- 是否与特定洗钱风险关联汇总分数
declare v_risk_ds_1020001 decimal(20,2); -- 行业现金密集程度-个人职业
declare v_risk_ds_1020 decimal(20,2); -- 行业现金密集程度汇总分数
declare v_risk_ds_1021001 decimal(20,2); -- 行业或职业其他风险-无
declare v_risk_ds_1021 decimal(20,2); -- 行业或职业其他风险汇总分数
declare v_risk_ds_1022001 decimal(20,2); -- 配合提供信息的积极程度与态度等-无
declare v_risk_ds_1022 decimal(20,2); -- 配合提供信息的积极程度与态度等汇总分数
declare v_risk_ds_1023001 decimal(20,2); -- 自定义其他风险-无
declare v_risk_ds_1023 decimal(20,2); -- 自定义其他风险汇总分数
declare v_risk_ds_1999001 decimal(20,2); -- 附加项风险-客户名称/证件被列入我国发布或承认的应实施反洗钱监控措施的名单；(对私)；
declare v_risk_ds_1999002 decimal(20,2); -- 附加项风险-客户为外国政要或其亲属、关系密切人；(对私)
declare v_risk_ds_1999003 decimal(20,2); -- 附加项风险-客户多次涉及可疑交易报告；(对私)
declare v_risk_ds_1999004 decimal(20,2); -- 附加项风险-客户拒绝金融机构依法开展的客户尽职调查工作；(对私)
declare v_risk_ds_1999 decimal(20,2); -- 附加项风险汇总分数
declare v_subitem_weight varchar(20);
declare num1 int; 
declare num2 int; 
declare v_grade_num int; 


	-- 游标cur_risk_customer_ds状态
	DECLARE ret_cur_risk_customer_ds varchar(20);
  -- 格式化日期参数
  select cast(dt_date as date) into v_dt_date;	
	-- 获取当前使用的风险等级
	select constDetailNum into v_risk_grade from aml_const_detail where constNum = 'sys_056' and constDetailName = '1';
	
	
	
	-- 更新客户信息中间表
	set @v_sql = 'truncate table aml_customer_personal_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 
	-- 更新客户信息中间表
	insert into aml_customer_personal_run(auto_id	,check_status	,customer_no	,customer_name	,branch_code	,date_id	,industry_code	,register_area	,establish_date	,nationality_code1	,nationality_code2	,phone_number1	,phone_number2	,contact_address	,contact_area	,management_nationality_code1	,management_area1	,management_address1	,management_nationality_code2	,management_area2	,management_address2	,cell_phone1	,cell_phone2	,crtft_type	,crtft_number	,crtft_due_date	,other_crtft_type	,open_date	,business_rela_type	,register_inst_distance	,management_inst_distance	,customer_gender	,cust_cooperate_flag	,cover_flag	,update_date	,risk_flag	,data_flag	,reserve_1	,reserve_2	,reserve_3	,reserve_4	,reserve_5	,reserve_6	,reserve_7	,reserve_8	,reserve_9	,reserve_10	,reserve_11	,reserve_12	,reserve_13	) 
	select auto_id	,check_status	,customer_no	,customer_name	,branch_code	,date_id	,industry_code	,register_area	,establish_date	,nationality_code1	,nationality_code2	,phone_number1	,phone_number2	,contact_address	,contact_area	,management_nationality_code1	,management_area1	,management_address1	,management_nationality_code2	,management_area2	,management_address2	,cell_phone1	,cell_phone2	,crtft_type	,crtft_number	,crtft_due_date	,other_crtft_type	,open_date	,business_rela_type	,register_inst_distance	,management_inst_distance	,customer_gender	,cust_cooperate_flag	,cover_flag	,update_date	,risk_flag	,data_flag	,reserve_1	,reserve_2	,reserve_3	,reserve_4	,reserve_5	,reserve_6	,reserve_7	,reserve_8	,reserve_9	,reserve_10	,reserve_11	,reserve_12	,reserve_13	 from aml_customer_personal where date_id = v_dt_date;
	
	
	 -- 更新账户信息中间表
	 set @v_sql = 'truncate table aml_account_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 
	insert into aml_account_run(auto_id,check_status,date_id,branch_code,customer_no,customer_name,acct_cata,acct_nature_type,acct_type,acct_code,acct_name,acct_open_date,bank_card_type,bank_card_other_type,acct_status,account_balance,agent_user_name,agent_user_crtft_type,agent_user_crtft_number,agent_user_nationality,cover_flag,special_type,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13 ) 
	select auto_id,check_status,date_id,branch_code,customer_no,customer_name,acct_cata,acct_nature_type,acct_type,acct_code,acct_name,acct_open_date,bank_card_type,bank_card_other_type,acct_status,account_balance,agent_user_name,agent_user_crtft_type,agent_user_crtft_number,agent_user_nationality,cover_flag,special_type,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13 from Aml_Account where date_id = v_dt_date;
	
	
	
	
	
	
	begin
	
	declare cur_risk_customer_ds cursor for
	select distinct customer_no from aml_risk_cust where customer_type = '1' order by customer_no;
	declare continue handler for not FOUND set ret_cur_risk_customer_ds = 1;
    
		
		-- 开启游标cur_risk_customer_ds
		OPEN cur_risk_customer_ds;
	  Cust_Loop:LOOP
		-- 循环客户编号
	  FETCH cur_risk_customer_ds into v_customer_no;
		 if ret_cur_risk_customer_ds = 1 THEN
	            LEAVE Cust_Loop;
		 else
		
      -- 检索获取评级编码
      select concat(date_format(sysdate(),'%Y%m%d%H%i%s'),v_customer_no) into v_grade_code;
	
	
				BEGIN
			
			
			
				  /******************************************* ********* *******************************************/
				  /*                                    1001: 客户信息的公开程度                                   */
				  /******************************************* ********* *******************************************/
				 
				 
				  
				  /* -------------------- 1001001：基本项完整度（对私） -------------------- */
			
				
					DECLARE cur_cust_risk_1001001 CURSOR FOR 
					SELECT cols_name FROM aml_cust_risk_cols WHERE table_name = 'aml_customer_personal' 
						AND cols_isnull = 'is not null' order by cols_name;
						
						declare continue handler for not FOUND set ret_cur_cust_risk_1001001 = 1;
						
					 -- 删除计算空值表
					 DELETE FROM aml_cust_risk_data;
		       -- 开启游标cur_cust_risk_1001001
		       OPEN cur_cust_risk_1001001;
	         cur_cust_risk_1001001_loop:LOOP
		       -- 循环必填字段
	         FETCH cur_cust_risk_1001001 into v_cols;
					 
						 if ret_cur_cust_risk_1001001 = 1 THEN
								LEAVE cur_cust_risk_1001001_loop;
						 else
						 SET @v_sql = concat('insert into aml_cust_risk_data(auto_id,cols_name,cols_isnull,table_name) select uuid() auto_id,''', v_cols, ''' cols_name,case when length(replace(',v_cols,','' '','''')) is null then 0 else length(replace(',v_cols, ','' '',''''))  end cols_isnull,''aml_customer_personal'' table_name from aml_customer_personal_run where customer_no = ''', v_customer_no,'''');
						 PREPARE stmt1 FROM @v_sql;
						 EXECUTE stmt1;
						 DEALLOCATE PREPARE stmt1;
										
						 end if;
		
						
	         end loop cur_cust_risk_1001001_loop;
           close cur_cust_risk_1001001; 
					 
			     select count(1) into num1 FROM aml_cust_risk_data WHERE cols_isnull > 0;
			     select count(1) into num2 FROM aml_cust_risk_data;
			     select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1001001 from aml_grade_weight where subitem_no = '1001001';
			     select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1001001';   
						 
		      end;
		      -- 如果未配置子项初始化分数则通过计算公式获取
			    if v_risk_ds_1001001 is null or v_risk_ds_1001001 = '' then
						      -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								      set v_risk_ds_1001001 = 0; 
					         else
									 -- 获取1001001|基本项完整度初始分数      
									  if num2 = 0 then 
											set v_risk_ds_1001001 = 0;
										 
									  else
				    					SELECT func_rule_compare_risk ( num1 / num2, '1001001' ) into v_risk_ds_1001001;
											
											-- 1001001|基本项完整度权重过滤
											SELECT cast(v_risk_ds_1001001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1001001 FROM aml_grade_weight WHERE subitem_no = '1001001';
							      end if; 
								   end if;		
					end if;
					
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight where subitem_category = '001' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1001|客户信息的公开程度 分数
							set v_risk_ds_1001 = cast(v_risk_ds_1001001 as decimal(20,2));
							-- 插入1001|客户信息的公开程度 分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1001' risk_section_code,
							'1001|客户信息的公开程度(对私)' risk_section_name,
							v_risk_ds_1001 new_grade_score, 
							v_risk_ds_1001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					/******************************************* ********* *******************************************/
				  /*                         1002|与客户建立或维持业务关系的渠道（对私）                           */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 1002001|与客户建立或维持业务关系的渠道-与客户建立或维护业务关系的渠道(对私) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1002001 from aml_grade_weight where subitem_no = '1002001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1002001';
			        if v_risk_ds_1002001 is null or v_risk_ds_1002001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1002001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1002001|与客户建立或维持业务关系的渠道（对公）初始分数
								   select func_rule_in_risk(business_rela_type,'1002001') into v_risk_ds_1002001 from aml_customer_personal_Run where customer_no = v_customer_no;
								   -- 1002001|与客户建立或维持业务关系的渠道（对公）权重过滤
								   SELECT cast(v_risk_ds_1002001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1002001 FROM aml_grade_weight t WHERE subitem_no = '1002001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '002' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1002
							set v_risk_ds_1002 = cast(v_risk_ds_1002001 as decimal(20,2));
							-- 插入1002分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1002' risk_section_code,
							'1002|与客户建立或维持业务关系的渠道（对私）' risk_section_name,
							v_risk_ds_1002 new_grade_score, 
							v_risk_ds_1002 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
						
					
					/******************************************* ********* *******************************************/
				  /*                        1003|客户所持身份证件或身份证明文件的种类(对私)                        */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 1003001|客户所持身份证件或身份证明文件的种类-客户信息-证件类型(对私) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1003001 from aml_grade_weight where subitem_no = '1003001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1003001';
			        if v_risk_ds_1003001 is null or v_risk_ds_1003001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1003001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1003001初始分数
								   select func_rule_in_risk(crtft_type,'1003001') into v_risk_ds_1003001 from aml_customer_personal_run where customer_no = v_customer_no;
								   -- 1003001权重过滤
								   SELECT cast(v_risk_ds_1003001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1003001 FROM aml_grade_weight t WHERE subitem_no = '1003001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '003' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1003
							set v_risk_ds_1003 = cast(v_risk_ds_1003001 as decimal(20,2));
							-- 插入1003分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1003' risk_section_code,
							'1003|客户所持身份证件或身份证明文件的种类(对私) ' risk_section_name,
							v_risk_ds_1003 new_grade_score, 
							v_risk_ds_1003 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
								
								
								
					
					/******************************************* ********* *******************************************/
				  /*                                  1004|反洗钱交易检测记录                                      */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 1004001|反洗钱交易检测记录-客户通过终审的可疑交易笔数(对私) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1004001 from aml_grade_weight where subitem_no = '1004001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1004001';
			        if v_risk_ds_1004001 is null or v_risk_ds_1004001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1004001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1004001初始分数
		select func_rule_compare_risk(count(1),'1004001') into v_risk_ds_1004001 from aml_suspcs_case_detail ascd 
		where ascd.audit_state = '4'  and ascd.audit_type_state = '1'
		and customer_no = v_customer_no ;
								   -- 1004001权重过滤
								   SELECT cast(v_risk_ds_1004001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1004001 FROM aml_grade_weight t WHERE subitem_no = '1004001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '004' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1004
							set v_risk_ds_1004 = cast(v_risk_ds_1004001 as decimal(20,2));
							-- 插入1004分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1004' risk_section_code,
							'1004|反洗钱交易检测记录(对私)' risk_section_name,
							v_risk_ds_1004 new_grade_score, 
							v_risk_ds_1004 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					


					/******************************************* ********* *******************************************/
				  /*                        1005|涉及客户的风险提示信息或权威媒体报道信息                          */
				  /******************************************* ********* *******************************************/
				 
				 
	 /* --- 1005001|涉及客户的风险提示信息或权威媒体报道信息-客户姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对私) --- */
			  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1005001 from aml_grade_weight where subitem_no = '1005001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1005001';
			        if v_risk_ds_1005001 is null or v_risk_ds_1005001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1005001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1005001初始分数
  	select func_rule_in_risk(count(1),'1005001') into v_risk_ds_1005001 from aml_customer_personal_run acpr 
		where customer_no = v_customer_no and 
		(
		acpr.customer_name in (select customer_name from Aml_Blacklist where (customer_name is not null or customer_name <> '') and status = '1') 
		or 
		concat(acpr.crtft_type , acpr.crtft_number) in (select concat(id_type , id_num) from Aml_Blacklist where (id_num is not null or id_num <> '') and status = '1') 
		);
		
								   -- 1005001权重过滤
								   SELECT cast(v_risk_ds_1005001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1005001 FROM aml_grade_weight t WHERE subitem_no = '1005001';
							   end if;
					    end if;
					
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '005' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1005
							set v_risk_ds_1005 = cast(v_risk_ds_1005001 as decimal(20,2));
							-- 插入1005分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1005' risk_section_code,
							'1005|涉及客户的风险提示信息或权威媒体报道信息(对私)' risk_section_name,
							v_risk_ds_1005 new_grade_score, 
							v_risk_ds_1005 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
												
					/******************************************* ********* *******************************************/
				  /*                                     1006|客户其他风险                                         */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 1006001|客户其他风险-自然人客户年龄(对私) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1006001 from aml_grade_weight where subitem_no = '1006001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1006001';
			        if v_risk_ds_1006001 is null or v_risk_ds_1006001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1006001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1006001初始分数
		select func_rule_compare_risk(TIMESTAMPDIFF(YEAR,establish_date,SYSDATE()),'1006001') into v_risk_ds_1006001 from aml_customer_personal_run where customer_no = v_customer_no ;
								   -- 1006001权重过滤
								   SELECT cast(v_risk_ds_1006001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1006001 FROM aml_grade_weight WHERE subitem_no = '1006001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '006' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1006
							set v_risk_ds_1006 = cast(v_risk_ds_1006001 as decimal(20,2));
							-- 插入1006分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1006' risk_section_code,
							'1006|客户其他风险(对私)' risk_section_name,
							v_risk_ds_1006 new_grade_score, 
							v_risk_ds_1006 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
					
					
										
					/******************************************* ********* *******************************************/
				  /*                            1007|反洗钱、反恐怖融资监控或制载情况                              */
				  /******************************************* ********* *******************************************/
				 
				 
 /* -------------- 1007001|反洗钱、反恐怖融资监控或制载情况-客户注册国籍(地区)受我国反洗钱监控或制载(对私) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1007001 from aml_grade_weight where subitem_no = '1007001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1007001';
			        if v_risk_ds_1007001 is null or v_risk_ds_1007001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1007001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1007001初始分数
	select func_rule_in_risk(COUNT(1),'1007001') into v_risk_ds_1007001 from aml_customer_personal_run where customer_no = v_customer_no and concat(nationality_code1 , register_area) in 
								   (select concat(nationality_code , address_area) from Aml_High_Risk_Area where danger_desc = '1');
								   -- 1007001权重过滤
								   SELECT cast(v_risk_ds_1007001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1007001 FROM aml_grade_weight WHERE subitem_no = '1007001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '007' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1007
							set v_risk_ds_1007 = cast(v_risk_ds_1007001 as decimal(20,2));
							-- 插入1007分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1007' risk_section_code,
							'1007|反洗钱、反恐怖融资监控或制载情况(对私)' risk_section_name,
							v_risk_ds_1007 new_grade_score, 
							v_risk_ds_1007 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
						
										
					/******************************************* ********* *******************************************/
				  /*                                         1008|风险提示信息                                     */
				  /******************************************* ********* *******************************************/
				 
				 
 /* -------------- 1008001|风险提示信息-客户注册国籍(地区)被FATF/APG/EAG等权威组织互评风险结果提示(对私) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1008001 from aml_grade_weight where subitem_no = '1008001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1008001';
			        if v_risk_ds_1008001 is null or v_risk_ds_1008001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1008001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1008001初始分数
	select func_rule_in_risk(COUNT(1),'1008001') into v_risk_ds_1008001 from aml_customer_personal_run where customer_no = v_customer_no and concat(nationality_code1 , register_area) in 
								   (select concat(nationality_code , address_area) from Aml_High_Risk_Area where danger_desc = '2');
								   -- 1008001权重过滤
								   SELECT cast(v_risk_ds_1008001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1008001 FROM aml_grade_weight WHERE subitem_no = '1008001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '008' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1008
							set v_risk_ds_1008 = cast(v_risk_ds_1008001 as decimal(20,2));
							-- 插入1008分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1008' risk_section_code,
							'1008|风险提示信息(对私)' risk_section_name,
							v_risk_ds_1008 new_grade_score, 
							v_risk_ds_1008 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
								
										
					/******************************************* ********* *******************************************/
				  /*                                         1009|上游犯罪状况                                     */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1009001|上游犯罪状况-客户注册国籍(地区)处于FATF发布的严重存在毒品、走私、诈骗等上游犯罪情况的国家(地区)(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1009001 from aml_grade_weight where subitem_no = '1009001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1009001';
			        if v_risk_ds_1009001 is null or v_risk_ds_1009001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1009001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1009001初始分数
	select func_rule_in_risk(COUNT(1),'1009001') into v_risk_ds_1009001 from aml_customer_personal_run where customer_no = v_customer_no and concat(nationality_code1 , register_area) in 
								   (select concat(nationality_code , address_area) from Aml_High_Risk_Area where danger_desc = '3');
								   -- 1009001权重过滤
								   SELECT cast(v_risk_ds_1009001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1009001 FROM aml_grade_weight WHERE subitem_no = '1009001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '009' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1009
							set v_risk_ds_1009 = cast(v_risk_ds_1009001 as decimal(20,2));
							-- 插入1009分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1009' risk_section_code,
							'1009|上游犯罪状况(对私)' risk_section_name,
							v_risk_ds_1009 new_grade_score, 
							v_risk_ds_1009 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
					
					
	
										
					/******************************************* ********* *******************************************/
				  /*                                         1010|特殊的金融监管风险                                     */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1010001|特殊的金融监管风险-客户出生地与金融机构相距过远(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1010001 from aml_grade_weight where subitem_no = '1010001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1010001';
			        if v_risk_ds_1010001 is null or v_risk_ds_1010001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1010001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1010001初始分数
	select func_rule_compare_risk(register_inst_distance,'1010001') into v_risk_ds_1010001 from aml_customer_personal_run where customer_no = v_customer_no;
								   -- 1010001权重过滤
								   SELECT cast(v_risk_ds_1010001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1010001 FROM aml_grade_weight WHERE subitem_no = '1010001';
							   end if;
					    end if;
					
			
				 
 /* ---1010002|特殊的金融监管风险-客户住址与金融机构相距过远(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1010002 from aml_grade_weight where subitem_no = '1010002';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1010002';
			        if v_risk_ds_1010002 is null or v_risk_ds_1010002 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1010002 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1010002初始分数
	select func_rule_compare_risk(register_inst_distance,'1010002') into v_risk_ds_1010002 from aml_customer_personal_run where customer_no = v_customer_no;
								   -- 1010002权重过滤
								   SELECT cast(v_risk_ds_1010002 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1010002 FROM aml_grade_weight WHERE subitem_no = '1010002';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '010' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1010
							set v_risk_ds_1010 = cast(v_risk_ds_1010001 as decimal(20,2)) + cast(v_risk_ds_1010002 as decimal(20,2));
							-- 插入1010分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1010' risk_section_code,
							'1010|特殊的金融监管风险(对私)' risk_section_name,
							v_risk_ds_1010 new_grade_score, 
							v_risk_ds_1010 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												
					
										
					/******************************************* ********* *******************************************/
				  /*                                        1011|地域其他风险                                   */
				  /******************************************* ********* *******************************************/
				 
				 
          /* ---1011001|地域其他风险-无(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1011001 from aml_grade_weight where subitem_no = '1011001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1011001';
			        if v_risk_ds_1011001 is null or v_risk_ds_1011001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1011001 = 0;
						       else
									  set v_risk_ds_1011001 = 0;
							   end if;
					    end if;
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '011' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1011
							set v_risk_ds_1011 = cast(v_risk_ds_1011001 as decimal(20,2));
							-- 插入1011分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1011' risk_section_code,
							'1011|地域其他风险(对私)' risk_section_name,
							v_risk_ds_1011 new_grade_score, 
							v_risk_ds_1011 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												
					

					
					
					
					/******************************************* ********* *******************************************/
				  /*                                        1012|与现金关联程度                                    */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1012001|与现金关联程度-客户一年内发生的现金交易总额(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1012001 from aml_grade_weight where subitem_no = '1012001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1012001';
			        if v_risk_ds_1012001 is null or v_risk_ds_1012001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1012001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1012001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'1012001') into v_risk_ds_1012001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and trans_type in
	('000000','000001','000002','000003,000004','000010','000011','000012','000013','000014','000015','000016','000017','000020','000021','000030','000031','000032','000033','000034','000035','000036','000051','010101','010102','010103','010104');
								   -- 1012001权重过滤
								   SELECT cast(v_risk_ds_1012001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1012001 FROM aml_grade_weight WHERE subitem_no = '1012001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '012' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1012
							set v_risk_ds_1012 = cast(v_risk_ds_1012001 as decimal(20,2));
							-- 插入1012分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1012' risk_section_code,
							'1012|与现金关联程度(对私)' risk_section_name,
							v_risk_ds_1012 new_grade_score, 
							v_risk_ds_1012 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
	

										
					/******************************************* ********* *******************************************/
				  /*                                        1013|非面对面交易                                      */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1013001|非面对面交易-客户一年内发生的非柜台业务交易总额(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1013001 from aml_grade_weight where subitem_no = '1013001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1013001';
			        if v_risk_ds_1013001 is null or v_risk_ds_1013001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1013001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1013001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'1013001') into v_risk_ds_1013001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and no_bar_trans_type <> '@N' and no_bar_trans_type <> '' and no_bar_trans_type is not null;
								   -- 1013001权重过滤
								   SELECT cast(v_risk_ds_1013001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1013001 FROM aml_grade_weight WHERE subitem_no = '1013001';
							   end if;
					    end if;
					
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '013' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1013
							set v_risk_ds_1013 = cast(v_risk_ds_1013001 as decimal(20,2));
							-- 插入1013分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1013' risk_section_code,
							'1013|非面对面交易(对私)' risk_section_name,
							v_risk_ds_1013 new_grade_score, 
							v_risk_ds_1013 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



										
					/******************************************* ********* *******************************************/
				  /*                                          1014|跨境交易                                        */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1014001|跨境交易-客户近一年内发生的跨境交易总额(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1014001 from aml_grade_weight where subitem_no = '1014001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1014001';
			        if v_risk_ds_1014001 is null or v_risk_ds_1014001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1014001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1014001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'1014001') into v_risk_ds_1014001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and trans_nationality <> 'CHN';
								   -- 1014001权重过滤
								   SELECT cast(v_risk_ds_1014001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1014001 FROM aml_grade_weight WHERE subitem_no = '1014001';
							   end if;
					    end if;
					
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '014' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1014
							set v_risk_ds_1014 = cast(v_risk_ds_1014001 as decimal(20,2));
							-- 插入1014分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1014' risk_section_code,
							'1014|跨境交易(对私)' risk_section_name,
							v_risk_ds_1014 new_grade_score, 
							v_risk_ds_1014 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



										
					/******************************************* ********* *******************************************/
				  /*                                          1015|代理交易                                        */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1015001|代理交易-客户发生的代理交易总额(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1015001 from aml_grade_weight where subitem_no = '1015001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1015001';
			        if v_risk_ds_1015001 is null or v_risk_ds_1015001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1015001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1015001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'1015001') into v_risk_ds_1015001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and (agent_user_name is not null or agent_user_name <> '' or agent_user_crtft_type is not null or agent_user_crtft_type <> '' or agent_user_crtft_number is not null or agent_user_crtft_number <> '');
								   -- 1015001权重过滤
								   SELECT cast(v_risk_ds_1015001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1015001 FROM aml_grade_weight WHERE subitem_no = '1015001';
							   end if;
					    end if;

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '015' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1015
							set v_risk_ds_1015 = cast(v_risk_ds_1015001 as decimal(20,2));
							-- 插入1015分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1015' risk_section_code,
							'1015|代理交易(对私)' risk_section_name,
							v_risk_ds_1015 new_grade_score, 
							v_risk_ds_1015 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  


										
					/******************************************* ********* *******************************************/
				  /*                                   1016|特殊业务类型的交易频率                                 */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1016001|特殊业务类型的交易频率-特殊类型账户的开户数量(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1016001 from aml_grade_weight where subitem_no = '1016001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1016001';
			        if v_risk_ds_1016001 is null or v_risk_ds_1016001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1016001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1016001初始分数
	select func_rule_compare_risk(COUNT(AAR.acct_code),'1016001') into v_risk_ds_1016001 from aml_customer_personal_run  acpr
								   left join Aml_Account_Run AAR on acpr.customer_no = AAR.customer_no and AAR.special_type <> '0' and acct_status = '1'; 
								   -- 1016001权重过滤
								   SELECT cast(v_risk_ds_1016001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1016001 FROM aml_grade_weight WHERE subitem_no = '1016001';
							   end if;
					    end if;
				
	 /* ---1016002|特殊业务类型的交易频率-特殊类型账户的销户数量(对私)--- */			
          select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1016002 from aml_grade_weight where subitem_no = '1016002';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1016002';
			        if v_risk_ds_1016002 is null or v_risk_ds_1016002 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1016002 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1016002初始分数
	select func_rule_compare_risk(COUNT(AAR.acct_code),'1016002') into v_risk_ds_1016002 from aml_customer_personal_run  acpr
								   left join Aml_Account_Run AAR on acpr.customer_no = AAR.customer_no and AAR.special_type <> '0' and acct_status = '2'; 
								   -- 1016002权重过滤
								   SELECT cast(v_risk_ds_1016002 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1016002 FROM aml_grade_weight WHERE subitem_no = '1016002';
							   end if;
					    end if;
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '016' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1016
							set v_risk_ds_1016 = cast(v_risk_ds_1016001 as decimal(20,2)) + cast(v_risk_ds_1016002 as decimal(20,2));
							-- 插入1016分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1016' risk_section_code,
							'1016|特殊业务类型的交易频率(对私)' risk_section_name,
							v_risk_ds_1016 new_grade_score, 
							v_risk_ds_1016 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  


					
					
					
					
					/******************************************* ********* *******************************************/
				  /*                                        1017|金融其他风险                                      */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---1017001|金融其他风险-自然人一年内跨境汇款频率(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1017001 from aml_grade_weight where subitem_no = '1017001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1017001';
			        if v_risk_ds_1017001 is null or v_risk_ds_1017001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1017001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1017001初始分数
	select func_rule_compare_risk(count(1),'1017001') into v_risk_ds_1017001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and trans_nationality <> 'CHN'
	and payment_flag = '02'; 
								   -- 1017001权重过滤
								   SELECT cast(v_risk_ds_1017001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1017001 FROM aml_grade_weight WHERE subitem_no = '1017001';
							   end if;
					    end if;
				
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '017' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1017
							set v_risk_ds_1017 = cast(v_risk_ds_1017001 as decimal(20,2));
							-- 插入1017分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1017' risk_section_code,
							'1017|金融其他风险(对私)' risk_section_name,
							v_risk_ds_1017 new_grade_score, 
							v_risk_ds_1017 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  

					
					
								
					/******************************************* ********* *******************************************/
				  /*                                1018|是否属于公认的较高风险的行业                              */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  1018001|是否属于公认的较高风险的行业-个人职业(对私)		--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1018001 from aml_grade_weight where subitem_no = '1018001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1018001';
			        if v_risk_ds_1018001 is null or v_risk_ds_1018001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1018001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1018001初始分数
	select func_rule_in_risk(industry_code,'1018001') into v_risk_ds_1018001 from aml_customer_personal_run where customer_no = v_customer_no;
								  
								   -- 1018001权重过滤
								   SELECT cast(v_risk_ds_1018001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1018001 FROM aml_grade_weight WHERE subitem_no = '1018001';
							   end if;
					    end if;
				
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '018' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1018
							set v_risk_ds_1018 = cast(v_risk_ds_1018001 as decimal(20,2));
							-- 插入1018分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1018' risk_section_code,
							'1018|是否属于公认的较高风险的行业(对私)' risk_section_name,
							v_risk_ds_1018 new_grade_score, 
							v_risk_ds_1018 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



							
					/******************************************* ********* *******************************************/
				  /*                                   1019|是否与特定洗钱风险关联                                 */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  1019001|是否与特定洗钱风险关联-客户属于外国政要(对私)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1019001 from aml_grade_weight where subitem_no = '1019001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1019001';
			        if v_risk_ds_1019001 is null or v_risk_ds_1019001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1019001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1019001初始分数
	select count(afd1.dign_user_name) into num1 from aml_customer_personal_run acpr
	left join (select * from aml_foreign_dignitaries where dign_user_name is not null and dign_user_name <> '') afd1
	on afd1.dign_user_name = acpr.customer_name
	where acpr.customer_no = v_customer_no;
	
	select count(afd2.dign_user_name) into num2 from aml_customer_personal_run acpr
	left join (select * from aml_foreign_dignitaries where dign_user_crtft_number is not null and dign_user_crtft_number <> '') afd2
	on concat(afd2.dign_user_crtft_type,dign_user_crtft_number) = concat(acpr.crtft_type ,acpr.crtft_number)
	where acpr.customer_no = v_customer_no;
	
	select func_rule_in_risk(num1+num2,'1019001') into v_risk_ds_1019001;

								  
				  -- 1019001权重过滤
							SELECT cast(v_risk_ds_1019001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1019001 FROM aml_grade_weight WHERE subitem_no = '1019001';
							   end if;
					    end if;
				
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '019' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1019
							set v_risk_ds_1019 = cast(v_risk_ds_1019001 as decimal(20,2));
							-- 插入1019分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1019' risk_section_code,
							'1019|是否与特定洗钱风险关联(对私)' risk_section_name,
							v_risk_ds_1019 new_grade_score, 
							v_risk_ds_1019 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  


					
					
					/******************************************* ********* *******************************************/
				  /*                                      1020|行业现金密集程度                                    */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  1020001|行业现金密集程度-个人职业(对私)	--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1020001 from aml_grade_weight where subitem_no = '1020001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1020001';
			        if v_risk_ds_1020001 is null or v_risk_ds_1020001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1020001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1020001初始分数
	select  func_rule_in_risk(industry_code,'1020001') into v_risk_ds_1020001 from aml_customer_personal_run where customer_no = v_customer_no;
							     
				  -- 1020001权重过滤
							SELECT cast(v_risk_ds_1020001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_ds_1020001 FROM aml_grade_weight WHERE subitem_no = '1020001';
							   end if;
					    end if;
				
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '020' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1020
							set v_risk_ds_1020 = cast(v_risk_ds_1020001 as decimal(20,2));
							-- 插入1020分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1020' risk_section_code,
							'1020|行业现金密集程度(对私)' risk_section_name,
							v_risk_ds_1020 new_grade_score, 
							v_risk_ds_1020 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



										
					/******************************************* ********* *******************************************/
				  /*                                      1021|行业或职业其他风险                                  */
				  /******************************************* ********* *******************************************/
				 
				 
          /* ---1021001|地域其他风险-无(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1021001 from aml_grade_weight where subitem_no = '1021001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1021001';
			        if v_risk_ds_1021001 is null or v_risk_ds_1021001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1021001 = 0;
						       else
									  set v_risk_ds_1021001 = 0;
							   end if;
					    end if;
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '021' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1021
							set v_risk_ds_1021 = cast(v_risk_ds_1021001 as decimal(20,2));
							-- 插入1021分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1021' risk_section_code,
							'1021|行业或职业其他风险(对私)' risk_section_name,
							v_risk_ds_1021 new_grade_score, 
							v_risk_ds_1021 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												

										
					/******************************************* ********* *******************************************/
				  /*                              1022|配合提供信息的积极程度与态度等                              */
				  /******************************************* ********* *******************************************/
				 
				 
          /* ---1022001|配合提供信息的积极程度与态度等-无(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1022001 from aml_grade_weight where subitem_no = '1022001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1022001';
			        if v_risk_ds_1022001 is null or v_risk_ds_1022001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1022001 = 0;
						       else
									  set v_risk_ds_1022001 = 0;
							   end if;
					    end if;
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '022' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1022
							set v_risk_ds_1022 = cast(v_risk_ds_1022001 as decimal(20,2));
							-- 插入1022分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1022' risk_section_code,
							'1022|配合提供信息的积极程度与态度等(对私)' risk_section_name,
							v_risk_ds_1022 new_grade_score, 
							v_risk_ds_1022 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												






										
					/******************************************* ********* *******************************************/
				  /*                                        1023|自定义其他风险                                    */
				  /******************************************* ********* *******************************************/
				 
				 
          /* ---1023001|自定义其他风险-无(对私)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1023001 from aml_grade_weight where subitem_no = '1023001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '1023001';
			        if v_risk_ds_1023001 is null or v_risk_ds_1023001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_ds_1023001 = 0;
						       else
									  set v_risk_ds_1023001 = 0;
							   end if;
					    end if;
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '023' and customer_type = '1';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 1023
							set v_risk_ds_1023 = cast(v_risk_ds_1023001 as decimal(20,2));
							-- 插入1023分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1023' risk_section_code,
							'1023|自定义其他风险(对私)' risk_section_name,
							v_risk_ds_1023 new_grade_score, 
							v_risk_ds_1023 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												








					/******************************************* ********* *******************************************/
				  /*                                       1999|附加项风险(对私)                                   */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  1999001|附加项风险-客户名称/证件被列入我国发布或承认的应实施反洗钱监控措施的名单；(对私)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1999001 from aml_grade_weight where subitem_no = '1999001';
			        if v_risk_ds_1999001 is null or v_risk_ds_1999001 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取1999001初始分数
									 
							  select func_rule_in_risk(COUNT(customer_no),'1999001') into v_risk_ds_1999001 from aml_customer_personal_run where customer_no = v_customer_no
							  and (customer_name in (select customer_name from aml_blacklist where status = '1' and (customer_name is not null or customer_name <> ''))
							  or
							   concat(crtft_type,crtft_number) in
							   (select concat(id_type,id_num) from  aml_blacklist where status = '1' and  (id_num is not null or id_num <> ''))
							   );
				
				
				       end if;
				
				
		
 /* ---  1999002|附加项风险-客户实际受益人为外国政要或其亲属、关系密切人；(对私)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1999002 from aml_grade_weight where subitem_no = '1999002';
			        if v_risk_ds_1999002 is null or v_risk_ds_1999002 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
										select count(afd1.dign_user_name) into num1 from aml_customer_personal_run acpr
	left join (select * from aml_foreign_dignitaries where dign_user_name is not null and dign_user_name <> '') afd1
	on afd1.dign_user_name = acpr.customer_name
	where acpr.customer_no = v_customer_no;
	
	select count(afd2.dign_user_name) into num2 from aml_customer_personal_run acpr
	left join (select * from aml_foreign_dignitaries where dign_user_crtft_number is not null and dign_user_crtft_number <> '') afd2
	on concat(afd2.dign_user_crtft_type,dign_user_crtft_number) = concat(acpr.crtft_type ,acpr.crtft_number)
	where acpr.customer_no = v_customer_no;
	
	select func_rule_in_risk(num1+num2,'1999002') into v_risk_ds_1999002;

	
				
				       end if;
						
						
		
 /* ---  1999003|附加项风险-客户多次涉及可疑交易报告；(对私)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1999003 from aml_grade_weight where subitem_no = '1999003';
			        if v_risk_ds_1999003 is null or v_risk_ds_1999003 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
										
										select func_rule_compare_risk(COUNT(ascd.customer_no),'1999003') into v_risk_ds_1999003 from aml_customer_personal_run acpr
							  left join aml_suspcs_case_detail ascd on ascd.customer_no = acpr.customer_no and ascd.audit_state = '4'  and ascd.audit_type_state = '1'
							  where acpr.customer_no = v_customer_no;
	
				       end if;
										
						
 /* ---  1999004|附加项风险-客户配合金融机构依法开展的客户尽职调查工作；(对私)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_ds_1999004 from aml_grade_weight where subitem_no = '1999004';
			        if v_risk_ds_1999004 is null or v_risk_ds_1999004 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
										
									select func_rule_in_risk(COUNT(1),'1999004') into v_risk_ds_1999004 from aml_customer_personal_run acpr
							  where acpr.customer_no = v_customer_no and cust_cooperate_flag = '1';
	
				       end if; 
										
		
						
							-- 汇总 1999
							set v_risk_ds_1999 = cast(v_risk_ds_1999001 as decimal(20,2))+ cast(v_risk_ds_1999002 as decimal(20,2))+ cast(v_risk_ds_1999003 as decimal(20,2))+ cast(v_risk_ds_1999004 as decimal(20,2));
							-- 插入1999分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'1999' risk_section_code,
							'1999|附加项风险(对私)' risk_section_name,
							v_risk_ds_1999 new_grade_score, 
							v_risk_ds_1999 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from aml_customer_personal_run where date_id = v_dt_date and customer_no = v_customer_no;
					


					
  -- 处理异常导致未获取到评分的子项默认为0分
	update aml_grade_detail set new_grade_score = '0',system_grade_score= '0' where grade_code = v_grade_code and (system_grade_score is null);

					
					
	-- 输出评级总分表
	update aml_grade set grade_state = '2' where customer_no = v_customer_no and grade_state = '0';
	-- 获取评级次数
		select case when max(grade_number) IS null then 1 else max(grade_number)+1 end into v_grade_num from aml_grade where customer_no = v_customer_no;
		
		
  insert into aml_grade
	select 
	uuid(),
	'0',
	v_grade_code,
	v_dt_date,
	'1',
	acpg.customer_no,
	acpg.customer_name,
	agd.grade_score,
	ad3.constDetailNum credit_rate_code3,
	ad4.constDetailNum credit_rate_code4,
	ad5.constDetailNum credit_rate_code5,
	v_grade_num,
	sysdate(),
  '0',
	date_sub(sysdate(),interval -ad3limit.constDetailName day) risk_due_date3,
	date_sub(sysdate(),interval -ad4limit.constDetailName day) risk_due_date4,
	date_sub(sysdate(),interval -ad5limit.constDetailName day) risk_due_date5,
	'' junior_help_user,
	'' repeat_help_user,
	'' cutout_help_user,
	acpg.branch_code branch_code
	from 
	aml_customer_personal_Run acpg 
	left join (select sum(new_grade_score) grade_score,max(customer_no) customer_no,grade_code from aml_grade_detail where grade_code = v_grade_code group by grade_code) agd on agd.customer_no = acpg.customer_no
	
	left join aml_const_detail ad3 on ad3.constNum = 'sys_057' and agd.grade_score between substring(ad3.constDetailName,1,LOCATE('-',ad3.constDetailName)-1) and substring(ad3.constDetailName,LOCATE('-',ad3.constDetailName)+1,LENGTH(ad3.constDetailName)-LOCATE('-',ad3.constDetailName)) and ad3.constDetailNum not like '%limit%'
	left join aml_const_detail ad3limit on ad3limit.constNum = 'sys_057' and ad3limit.constDetailNum like '%limit%' and ad3.constDetailNum = substring(ad3limit.constDetailNum,1,LOCATE('_',ad3limit.constDetailNum)-1)
		
	left join aml_const_detail ad4 on ad4.constNum = 'sys_058' and agd.grade_score between substring(ad4.constDetailName,1,LOCATE('-',ad4.constDetailName)-1) and substring(ad4.constDetailName,LOCATE('-',ad4.constDetailName)+1,LENGTH(ad4.constDetailName)-LOCATE('-',ad4.constDetailName)) and ad4.constDetailNum not like '%limit%'
	left join aml_const_detail ad4limit on ad4limit.constNum = 'sys_058' and ad4limit.constDetailNum like '%limit%' and ad4.constDetailNum = substring(ad4limit.constDetailNum,1,LOCATE('_',ad4limit.constDetailNum)-1)
	
	left join aml_const_detail ad5 on ad5.constNum = 'sys_059' and agd.grade_score between substring(ad5.constDetailName,1,LOCATE('-',ad5.constDetailName)-1) and substring(ad5.constDetailName,LOCATE('-',ad5.constDetailName)+1,LENGTH(ad5.constDetailName)-LOCATE('-',ad5.constDetailName)) and ad5.constDetailNum not like '%limit%'
	left join aml_const_detail ad5limit on ad5limit.constNum = 'sys_059' and ad5limit.constDetailNum like '%limit%' and ad5.constDetailNum = substring(ad5limit.constDetailNum,1,LOCATE('_',ad5limit.constDetailNum)-1)
	
	where acpg.customer_no = v_customer_no;
	
			end if;
				end loop Cust_Loop;
	
    CLOSE cur_risk_customer_ds; -- 关闭游标cur_risk_customer_ds
		end;
    END