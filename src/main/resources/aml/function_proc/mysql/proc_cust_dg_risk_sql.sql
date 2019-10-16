CREATE DEFINER=`aml`@`%` PROCEDURE `proc_cust_dg_risk`(in dt_date varchar(20))
BEGIN
   /*
  对公客户评级模型:
  评级模型描述：23项常规大项+1项附加大项
  */  
	
	
	-- 评级编码
  DECLARE v_grade_code VARCHAR(50); 
  -- 大项编号
  DECLARE v_risk_section_code VARCHAR(32); 
  -- 大项名称
  DECLARE v_risk_section_name VARCHAR(512); 
  -- 数据日期
  DECLARE v_dt_date VARCHAR(20); 
  -- 执行sql
  DECLARE v_sql VARCHAR(2000); 
  -- 空标识
  DECLARE null_flag decimal(20,2); 
  -- 客户编号
  DECLARE v_customer_no VARCHAR(200); 
  -- 字段名称
  DECLARE v_cols VARCHAR(200); 
  -- 字段内容大小
  DECLARE v_cols_count VARCHAR(200); 
  -- 风险等级
  DECLARE v_risk_grade VARCHAR(1); 
  -- 数据记录条数
  DECLARE counti VARCHAR(20); 
  -- 评级次数
  DECLARE v_grade_num int;
  -- 明细子项权重
  DECLARE v_subitem_weight VARCHAR(20);
  -- 客户信息的公开程度-基本项完整度(对公)
  DECLARE v_risk_dg_2001001 decimal(20,2); 
  -- 客户信息的公开程度-是否上市(对公)
  DECLARE v_risk_dg_2001002 decimal(20,2); 
  -- 客户信息的公开程度汇总分数(对公)
  DECLARE v_risk_dg_2001 decimal(20,2); 
  -- 与客户建立或维持业务关系的渠道-与客户建立或维护业务关系的渠道(对公)
  DECLARE v_risk_dg_2002001 decimal(20,2); 
  -- 与客户建立或维持业务关系的渠道汇总分数(对公)
  DECLARE v_risk_dg_2002 decimal(20,2); 
  -- 客户所持身份证件或身份证明文件的种类-客户信息-证件类型(对公)
  DECLARE v_risk_dg_2003001 decimal(20,2); 
  -- 客户所持身份证件或身份证明文件的种类汇总分数(对公)
  DECLARE v_risk_dg_2003 decimal(20,2); 
  -- 反洗钱交易检测记录-客户通过终审的可疑交易笔数(对公)
  DECLARE v_risk_dg_2004001 decimal(20,2); 
  -- 反洗钱交易检测记录汇总分数(对公)
  DECLARE v_risk_dg_2004 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息-客户姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对公)
  DECLARE v_risk_dg_2005001 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息-股东姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对公)
  DECLARE v_risk_dg_2005002 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息-法人姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对公)
  DECLARE v_risk_dg_2005003 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息汇总分数(对公)
  DECLARE v_risk_dg_2005 decimal(20,2); 
  -- 客户其他风险-非自然人客户的存续时间(对公)
  DECLARE v_risk_dg_2006001 decimal(20,2); 
  -- 客户其他风险汇总分数(对公)
  DECLARE v_risk_dg_2006 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-客户注册国籍被反恐怖融资监控或制载(对公)
  DECLARE v_risk_dg_2007001 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-客户注册地代码、经营地址代码、联系地址代码存在高风险(对公)
  DECLARE v_risk_dg_2007002 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-股东国籍被反恐怖融资监控或制载(对公)
  DECLARE v_risk_dg_2007003 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-股东出生地代码、客户住址代码、联系地址代码存在高风险(对公)
  DECLARE v_risk_dg_2007004 decimal(20,2);
  -- 反洗钱、反恐怖融资监控或制载情况-法人国籍被反恐怖融资监控或制载(对公)
  DECLARE v_risk_dg_2007005 decimal(20,2);  
  -- 反洗钱、反恐怖融资监控或制载情况-法人出生地代码、客户住址代码、联系地址代码存在高风险(对公)
  DECLARE v_risk_dg_2007006 decimal(20,2);  
  -- 反洗钱、反恐怖融资监控或制载情况汇总分数(对公)
  DECLARE v_risk_dg_2007 decimal(20,2); 
  -- 风险提示信息-FATF发布的反洗钱恐怖融资国家地区(对公)
  DECLARE v_risk_dg_2008001 decimal(20,2); 
  -- 风险提示信息汇总分数(对公)
  DECLARE v_risk_dg_2008 decimal(20,2); 
  -- 上游犯罪状况-FATF发布的参与恐怖及支持恐怖活动的严重国家地区(对公)
  DECLARE v_risk_dg_2009001 decimal(20,2); 
  -- 上游犯罪状况汇总分数(对公)
  DECLARE v_risk_dg_2009 decimal(20,2); 
  -- 特殊的金融监管风险-客户注册地与金融机构相距过远(对公)
  DECLARE v_risk_dg_2010001 decimal(20,2); 
  -- 特殊的金融监管风险-客户经营地与金融机构相距过远(对公)
  DECLARE v_risk_dg_2010002 decimal(20,2); 
  -- 特殊的金融监管风险汇总分数(对公)
  DECLARE v_risk_dg_2010 decimal(20,2); 
  -- 地域其他风险-无(对公)
  DECLARE v_risk_dg_2011001 decimal(20,2); 
  -- 地域其他风险汇总分数(对公)
  DECLARE v_risk_dg_2011 decimal(20,2);
  -- 与现金关联程度-客户一年内发生的现金交易总额(对公)
  DECLARE v_risk_dg_2012001 decimal(20,2);
  -- 与现金关联程度汇总分数(对公)
  DECLARE v_risk_dg_2012 decimal(20,2); 
  -- 非面对面交易-客户一年内发生的非柜台业务交易总额(对公)
  DECLARE v_risk_dg_2013001 decimal(20,2); 
  -- 非面对面交易-非面对面交易(对公)
  DECLARE v_risk_dg_2013002 decimal(20,2); 
  -- 非面对面交易汇总分数(对公)
  DECLARE v_risk_dg_2013 decimal(20,2); 
  -- 跨境交易-客户一年内产生的可疑跨境交易次数(对公)
  DECLARE v_risk_dg_2014001 decimal(20,2); 
  -- 跨境交易汇总分数(对公)
  DECLARE v_risk_dg_2014 decimal(20,2);
  -- 代理交易-开户代办人代理账户个数(对公)
  DECLARE v_risk_dg_2015001 decimal(20,2); 
  -- 代理交易-开户代办人涉及可疑报告(对公)
  DECLARE v_risk_dg_2015002 decimal(20,2); 
  -- 代理交易-开户代办人涉及名单(对公)
  DECLARE v_risk_dg_2015003 decimal(20,2); 
  -- 代理交易汇总分数(对公)
  DECLARE v_risk_dg_2015 decimal(20,2);
  -- 特殊业务类型的交易频率-开户数量(对公)
  DECLARE v_risk_dg_2016001 decimal(20,2); 
  -- 特殊业务类型的交易频率-销户数量(对公)
  DECLARE v_risk_dg_2016002 decimal(20,2);
  -- 特殊业务类型的交易频率汇总分数(对公) 
  DECLARE v_risk_dg_2016 decimal(20,2); 
  -- 金融其他风险-非自然人一年内跨境汇款频率(对公)
  DECLARE v_risk_dg_2017001 decimal(20,2); 
  -- 金融其他风险汇总分数(对公)
  DECLARE v_risk_dg_2017 decimal(20,2); 
  -- 是否属于公认的较高风险的行业-行业分类(对公)
  DECLARE v_risk_dg_2018001 decimal(20,2); 
  -- 是否属于公认的较高风险的行业汇总分数(对公)
  DECLARE v_risk_dg_2018 decimal(20,2);
  -- 是否与特定洗钱风险关联-其实际受益人、法人、股东属于外国政要(对公)
  DECLARE v_risk_dg_2019001 decimal(20,2); 
  -- 是否与特定洗钱风险关联汇总分数(对公)
  DECLARE v_risk_dg_2019 decimal(20,2); 
  -- 行业现金密集程度-企业类型(对公)
  DECLARE v_risk_dg_2020001 decimal(20,2); 
  -- 行业现金密集程度汇总分数(对公)
  DECLARE v_risk_dg_2020 decimal(20,2); 
  -- 行业或职业其他风险-无(对公)
  DECLARE v_risk_dg_2021001 decimal(20,2); 
  -- 行业或职业其他风险汇总分数(对公)
  DECLARE v_risk_dg_2021 decimal(20,2); 
  -- 配合提供信息的积极程度与态度等-非自然人客户的股权或控制权结构(对公)
  DECLARE v_risk_dg_2022001 decimal(20,2); 
  -- 配合提供信息的积极程度与态度等汇总分数(对公)
  DECLARE v_risk_dg_2022 decimal(20,2); 
  -- 自定义其他风险-无(对公)
  DECLARE v_risk_dg_2023001 decimal(20,2); 
  -- 自定义其他风险汇总分数(对公)
  DECLARE v_risk_dg_2023 decimal(20,2); 
  -- 附加项风险-客户、客户实际控制人或实际受益人被列入我国发布或承认的应实施反洗钱监控措施的名单；(对公)
  DECLARE v_risk_dg_2999001 decimal(20,2);
  -- 附加项风险-客户、客户实际控制人或实际受益人为外国政要或其亲属、关系密切人；(对公)
  DECLARE v_risk_dg_2999002 decimal(20,2); 
  -- 附加项风险-客户多次涉及可疑交易报告；(对公)
  DECLARE v_risk_dg_2999003 decimal(20,2); 
  -- 附加项风险-客户拒绝金融机构依法开展的客户尽职调查工作；(对公)
  DECLARE v_risk_dg_2999004 decimal(20,2); 
  -- 附加项风险汇总分数(对公)
  DECLARE v_risk_dg_2999 decimal(20,2); 
	-- 游标cur_risk_customer_dg状态
	DECLARE ret_cur_risk_customer_dg varchar(20);
	-- 游标cur_cust_risk_2001001状态
	DECLARE ret_cur_cust_risk_2001001 varchar(20);
	
	declare num1 int;
	declare num2 int;
	
  -- 格式化日期参数
  select cast(dt_date as date) into v_dt_date;	
	-- 获取当前使用的风险等级
	-- select constDetailNum into v_risk_grade from aml_const_detail where constNum = 'sys_056' and constDetailName = '1';
	select '5' into v_risk_grade;
	
	-- 更新客户信息中间表
	set @v_sql = 'truncate table aml_customer_public_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	
	insert into aml_customer_public_run(auto_id ,check_status ,customer_no ,customer_name ,branch_code ,date_id ,industry_code ,register_area ,establish_date ,legal_name ,legal_crtft_type ,legal_other_crtft_type,legal_crtft_number,controller_name ,controller_crtft_type ,controller_oher_crtft_type,controller_crtft_number,nationality_code1,nationality_code2,phone_number1,phone_number2,contact_address ,contact_area ,management_nationality_code1,management_area1 ,management_address1 ,management_nationality_code2,management_area2 ,management_address2 ,cell_phone1 ,cell_phone2 ,crtft_type ,crtft_number,crtft_due_date ,other_crtft_type,open_date ,business_license_code,business_license_expire_date ,register_balance ,register_currency,regist_certft_code,organization_code,business_scope ,unid_social_code,listing_flag ,business_rela_type ,register_inst_distance,management_inst_distance,enterprise_type ,cust_cooperate_flag ,cover_flag ,update_date ,risk_flag,data_flag,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8 ,reserve_9 ,reserve_10 ,reserve_11 ,reserve_12 ,reserve_13 ) 
	select auto_id ,check_status ,customer_no ,customer_name ,branch_code ,date_id ,industry_code ,register_area ,establish_date ,legal_name ,legal_crtft_type ,legal_other_crtft_type,legal_crtft_number,controller_name ,controller_crtft_type ,controller_oher_crtft_type,controller_crtft_number,nationality_code1,nationality_code2,phone_number1,phone_number2,contact_address ,contact_area ,management_nationality_code1,management_area1 ,management_address1 ,management_nationality_code2,management_area2 ,management_address2 ,cell_phone1 ,cell_phone2 ,crtft_type ,crtft_number,crtft_due_date ,other_crtft_type,open_date ,business_license_code,business_license_expire_date ,register_balance ,register_currency,regist_certft_code,organization_code,business_scope ,unid_social_code,listing_flag ,business_rela_type ,register_inst_distance,management_inst_distance,enterprise_type ,cust_cooperate_flag ,cover_flag ,update_date ,risk_flag,data_flag,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8 ,reserve_9 ,reserve_10 ,reserve_11 ,reserve_12 ,reserve_13 from aml_customer_public where date_id = v_dt_date;
	
	 -- 更新账户信息中间表
	 set @v_sql = 'truncate table aml_account_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 
	insert into aml_account_run(auto_id,check_status,date_id,branch_code,customer_no,customer_name,acct_cata,acct_nature_type,acct_type,acct_code,acct_name,acct_open_date,bank_card_type,bank_card_other_type,acct_status,account_balance,agent_user_name,agent_user_crtft_type,agent_user_crtft_number,agent_user_nationality,cover_flag,special_type,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13 ) 
	select auto_id,check_status,date_id,branch_code,customer_no,customer_name,acct_cata,acct_nature_type,acct_type,acct_code,acct_name,acct_open_date,bank_card_type,bank_card_other_type,acct_status,account_balance,agent_user_name,agent_user_crtft_type,agent_user_crtft_number,agent_user_nationality,cover_flag,special_type,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13 from Aml_Account where date_id = v_dt_date;
	
	
	 -- 更新收益人信息中间表
	 set @v_sql = 'truncate table aml_customer_beneficiary_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 
	 insert into aml_customer_beneficiary_run(auto_id,check_status,date_id	,customer_no	,customer_name	,branch_code	,profit_type	,profit_name	,profit_area	,profit_address	,profit_crtft_type	,profit_crtft_no	,profit_crtft_expire_date	,profit_shares	,profit_position	,cover_flag	,profit_remarks	,update_date	,reserve_1	,reserve_2	,reserve_3	,reserve_4	,reserve_5	,reserve_6	,reserve_7	,reserve_8	,reserve_9	,reserve_10	,reserve_11	,reserve_12	,reserve_13) 
	 select auto_id,check_status,date_id	,customer_no	,customer_name	,branch_code	,profit_type	,profit_name	,profit_area	,profit_address	,profit_crtft_type	,profit_crtft_no	,profit_crtft_expire_date	,profit_shares	,profit_position	,cover_flag	,profit_remarks	,update_date	,reserve_1	,reserve_2	,reserve_3	,reserve_4	,reserve_5	,reserve_6	,reserve_7	,reserve_8	,reserve_9	,reserve_10	,reserve_11	,reserve_12	,reserve_13 from aml_customer_beneficiary where date_id = v_dt_date;
	
	
	
	begin
	declare cur_risk_customer_dg cursor for
	  select distinct customer_no from aml_risk_cust where customer_type = '2' order by customer_no;
	  declare continue handler for not FOUND set ret_cur_risk_customer_dg = 1;
    
		-- 开启游标cur_risk_customer_dg
		OPEN cur_risk_customer_dg;
	  Cust_Loop:LOOP
		-- 循环客户编号
	  FETCH cur_risk_customer_dg into v_customer_no;
		 if ret_cur_risk_customer_dg = 1 THEN
	            LEAVE Cust_Loop;
		 else
		
      -- 检索获取评级编码
      select concat(date_format(sysdate(),'%Y%m%d%H%i%s'),v_customer_no) into v_grade_code;
	
	

				  /******************************************* ********* *******************************************/
				  /*                                2001|客户信息的公开程度（对公）                                */
				  /******************************************* ********* *******************************************/
				 
				 
				  
				  /* -------------------- 2001001|客户信息的公开程度-基本项完整度（对公） -------------------- */
				  
		   begin
					
	        declare cur_cust_risk_2001001 cursor for
	        SELECT cols_name FROM aml_cust_risk_cols WHERE table_name = 'aml_customer_public' AND cols_isnull = 'is not null' order by cols_name;
	        declare continue handler for not FOUND set ret_cur_cust_risk_2001001 = 1;
          
					 -- 删除计算空值表
					 DELETE FROM aml_cust_risk_data;
		       -- 开启游标cur_cust_risk_2001001
		       OPEN cur_cust_risk_2001001;
	         cur_cust_risk_2001001_loop:LOOP
		       -- 循环必填字段
	         FETCH cur_cust_risk_2001001 into v_cols;
					 
						 if ret_cur_cust_risk_2001001 = 1 THEN
								LEAVE cur_cust_risk_2001001_loop;
						 else
						 SET @v_sql = concat('insert into aml_cust_risk_data(auto_id,cols_name,cols_isnull,table_name) select uuid() auto_id,''', v_cols, ''' cols_name,case when length(replace(',v_cols,','' '','''')) is null then 0 else length(replace(',v_cols, ','' '',''''))  end cols_isnull,''aml_customer_public'' table_name from aml_customer_public_run where customer_no = ''', v_customer_no,'''');
						 
						 PREPARE stmt1 FROM @v_sql;
						 EXECUTE stmt1;
						 DEALLOCATE PREPARE stmt1;
										
						 end if;
		
	         end loop cur_cust_risk_2001001_loop;
           close cur_cust_risk_2001001; 
					 
			     select count(1) into num1 FROM aml_cust_risk_data WHERE cols_isnull > 0;
			     select count(1) into num2 FROM aml_cust_risk_data;
			     select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2001001 from aml_grade_weight where subitem_no = '2001001';
			     select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2001001';   
						 
		      end;
		      -- 如果未配置子项初始化分数则通过计算公式获取
			    if v_risk_dg_2001001 is null or v_risk_dg_2001001 = '' then
						      -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) > 0 then
								      set v_risk_dg_2001001 = 0; 
					         else
									 -- 获取2001001|基本项完整度初始分数      
									  if num2 = 0 then 
											set v_risk_dg_2001001 = 0;
										 
									  else
				    					SELECT func_rule_compare_risk ( num1 / num2, '2001001' ) into v_risk_dg_2001001;
											
											-- 2001001|基本项完整度权重过滤
											SELECT cast(v_risk_dg_2001001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2001001 FROM aml_grade_weight t WHERE subitem_no = '2001001';
							      end if; 
								   end if;		
					end if;
					
					
					
				   /* -------------------- 2001002|客户信息的公开程度-是否上市(对公) -------------------- */
				    
				    select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2001002 from aml_grade_weight where subitem_no = '2001002';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2001002';
			        if v_risk_dg_2001002 is null or v_risk_dg_2001002 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2001002 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2001002|客户信息的公开程度-是否上市(对公)初始分数
								   select func_rule_in_risk(listing_flag,'2001002') into v_risk_dg_2001002 from aml_customer_public_run where customer_no = v_customer_no;
									 
								   -- 2001002|客户信息的公开程度-是否上市(对公)权重过滤
								   SELECT cast(v_risk_dg_2001002 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2001002 FROM aml_grade_weight t WHERE subitem_no = '2001002';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '001' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2001|客户信息的公开程度 分数
							set v_risk_dg_2001 = cast(v_risk_dg_2001001 as decimal(20,2))+cast(v_risk_dg_2001002  as decimal(20,2));
							-- 插入2001|客户信息的公开程度(对公)分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2001' risk_section_code,
							'2001|客户信息的公开程度(对公)' risk_section_name,
							v_risk_dg_2001 new_grade_score, 
							v_risk_dg_2001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
					
					/******************************************* ********* *******************************************/
				  /*                         2002|与客户建立或维持业务关系的渠道（对公）                           */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 2002001|与客户建立或维持业务关系的渠道-与客户建立或维护业务关系的渠道(对公) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2002001 from aml_grade_weight where subitem_no = '2002001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2002001';
			        if v_risk_dg_2002001 is null or v_risk_dg_2002001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2002001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2002001|与客户建立或维持业务关系的渠道（对公）初始分数
								   select func_rule_in_risk(business_rela_type,'2002001') into v_risk_dg_2002001 from aml_customer_public_run where customer_no = v_customer_no;
								   -- 2002001|与客户建立或维持业务关系的渠道（对公）权重过滤
								   SELECT cast(v_risk_dg_2002001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2002001 FROM aml_grade_weight t WHERE subitem_no = '2002001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '002' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2002
							set v_risk_dg_2002 = cast(v_risk_dg_2002001 as decimal(20,2));
							-- 插入2002分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2002' risk_section_code,
							'2002|与客户建立或维持业务关系的渠道（对公）' risk_section_name,
							v_risk_dg_2002 new_grade_score, 
							v_risk_dg_2002 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
					
					
					/******************************************* ********* *******************************************/
				  /*                        2003|客户所持身份证件或身份证明文件的种类(对公)                        */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 2003001|客户所持身份证件或身份证明文件的种类-客户信息-证件类型(对公) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2003001 from aml_grade_weight where subitem_no = '2003001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2003001';
			        if v_risk_dg_2003001 is null or v_risk_dg_2003001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2003001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2003001初始分数
								   select func_rule_in_risk(crtft_type,'2003001') into v_risk_dg_2003001 from aml_customer_public_run where customer_no = v_customer_no;
								   -- 2003001权重过滤
								   SELECT cast(v_risk_dg_2003001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2003001 FROM aml_grade_weight t WHERE subitem_no = '2003001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '003' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2003
							set v_risk_dg_2003 = cast(v_risk_dg_2003001 as decimal(20,2));
							-- 插入2003分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2003' risk_section_code,
							'2003|客户所持身份证件或身份证明文件的种类(对公) ' risk_section_name,
							v_risk_dg_2003 new_grade_score, 
							v_risk_dg_2003 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
								
					
					/******************************************* ********* *******************************************/
				  /*                                  2004|反洗钱交易检测记录                                      */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 2004001|反洗钱交易检测记录-客户、股东、法人、受益人通过终审的可疑交易笔数 ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2004001 from aml_grade_weight where subitem_no = '2004001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2004001';
			        if v_risk_dg_2004001 is null or v_risk_dg_2004001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2004001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2004001初始分数
		select func_rule_compare_risk(count(1),'2004001') into v_risk_dg_2004001 from aml_suspcs_case_detail ascd 
		where ascd.audit_state = '4'  and ascd.audit_type_state = '1'
		and customer_no = v_customer_no ;
								   -- 2004001权重过滤
								   SELECT cast(v_risk_dg_2004001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2004001 FROM aml_grade_weight t WHERE subitem_no = '2004001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '004' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2004
							set v_risk_dg_2004 = cast(v_risk_dg_2004001 as decimal(20,2));
							-- 插入2004分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2004' risk_section_code,
							'2004|反洗钱交易检测记录(对公)' risk_section_name,
							v_risk_dg_2004 new_grade_score, 
							v_risk_dg_2004 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
					/******************************************* ********* *******************************************/
				  /*                        2005|涉及客户的风险提示信息或权威媒体报道信息                          */
				  /******************************************* ********* *******************************************/
				 
				 
	 /* --- 2005001|涉及客户的风险提示信息或权威媒体报道信息-客户姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对公) --- */
			  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2005001 from aml_grade_weight where subitem_no = '2005001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2005001';
			        if v_risk_dg_2005001 is null or v_risk_dg_2005001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2005001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2005001初始分数
  	select func_rule_in_risk(count(1),'2005001') into v_risk_dg_2005001 from aml_customer_public_run acpr 
		where customer_no = v_customer_no and 
		(
		acpr.customer_name in (select customer_name from Aml_Blacklist where (customer_name is not null or customer_name <> '') and status = '1') 
		or 
		concat(acpr.crtft_type , acpr.crtft_number) in (select concat(id_type , id_num) from Aml_Blacklist where (id_num is not null or id_num <> '') and status = '1') 
		);
		
								   -- 2005001权重过滤
								   SELECT cast(v_risk_dg_2005001 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2005001 FROM aml_grade_weight t WHERE subitem_no = '2005001';
							   end if;
					    end if;
					
					
					
					
  /* --- 2005002|涉及客户的风险提示信息或权威媒体报道信息-股东姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对公) --- */
			  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2005002 from aml_grade_weight where subitem_no = '2005002';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2005002';
			        if v_risk_dg_2005002 is null or v_risk_dg_2005002 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2005002 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2005002初始分数
  	select func_rule_in_risk(count(1),'2005002') into v_risk_dg_2005002 from aml_customer_public_run acpr 
		where customer_no = v_customer_no and 
		(
		acpr.controller_name in (select customer_name from Aml_Blacklist where (customer_name is not null or customer_name <> '') and status = '1') 
		or 
		concat(acpr.controller_crtft_type , acpr.controller_crtft_number) in (select concat(id_type , id_num) from Aml_Blacklist where (id_num is not null or id_num <> '') and status = '1') 
		);
		
								   -- 2005002权重过滤
								   SELECT cast(v_risk_dg_2005002 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2005002 FROM aml_grade_weight t WHERE subitem_no = '2005002';
							   end if;
					    end if;	
					
					
									
		
/* --- 2005003|涉及客户的风险提示信息或权威媒体报道信息-法人姓名/证件类型+证件号码存在于黑名单"公安部通缉令"中(对公) --- */
			  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2005003 from aml_grade_weight where subitem_no = '2005003';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2005003';
			        if v_risk_dg_2005003 is null or v_risk_dg_2005003 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2005003 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2005003初始分数
  	select func_rule_in_risk(count(1),'2005003') into v_risk_dg_2005003 from aml_customer_public_run acpr 
		where customer_no = v_customer_no and 
		(
		acpr.legal_name in (select customer_name from Aml_Blacklist where black_source = '公安部通缉令' and (customer_name is not null or customer_name <> '') and status = '1') 
		or 
		concat(acpr.legal_crtft_type , acpr.legal_crtft_number) in (select concat(id_type , id_num) from Aml_Blacklist where  black_source = '公安部通缉令' and (id_num is not null or id_num <> '') and status = '1') 
		);
		
								   -- 2005003权重过滤
								   SELECT cast(v_risk_dg_2005003 as decimal(20,2)) * cast(t.subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2005003 FROM aml_grade_weight t WHERE subitem_no = '2005003';
							   end if;
					    end if;
					
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '005' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2005
							set v_risk_dg_2005 = cast(v_risk_dg_2005001 as decimal(20,2)) + cast(v_risk_dg_2005002 as decimal(20,2)) + cast(v_risk_dg_2005003 as decimal(20,2));
							-- 插入2005分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2005' risk_section_code,
							'2005|涉及客户的风险提示信息或权威媒体报道信息(对公)' risk_section_name,
							v_risk_dg_2005 new_grade_score, 
							v_risk_dg_2005 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
										
					/******************************************* ********* *******************************************/
				  /*                                     2006|客户其他风险                                         */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------- 2006001|客户其他风险-非自然人客户的存续时间(对公) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2006001 from aml_grade_weight where subitem_no = '2006001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2006001';
			        if v_risk_dg_2006001 is null or v_risk_dg_2006001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2006001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2006001初始分数
		select func_rule_compare_risk(TIMESTAMPDIFF(YEAR,open_date,SYSDATE()),'2006001') into v_risk_dg_2006001 from aml_customer_public_run where customer_no = v_customer_no ;
								   -- 2006001权重过滤
								   SELECT cast(v_risk_dg_2006001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2006001 FROM aml_grade_weight WHERE subitem_no = '2006001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '006' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2006
							set v_risk_dg_2006 = cast(v_risk_dg_2006001 as decimal(20,2));
							-- 插入2006分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2006' risk_section_code,
							'2006|客户其他风险(对公)' risk_section_name,
							v_risk_dg_2006 new_grade_score, 
							v_risk_dg_2006 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
										
					/******************************************* ********* *******************************************/
				  /*                            2007|反洗钱、反恐怖融资监控或制载情况                              */
				  /******************************************* ********* *******************************************/
				 
				 
 /* -------------- 2007001|反洗钱、反恐怖融资监控或制载情况-客户注册国籍(地区)受我国反洗钱监控或制载(对公) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2007001 from aml_grade_weight where subitem_no = '2007001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2007001';
			        if v_risk_dg_2007001 is null or v_risk_dg_2007001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2007001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2007001初始分数
	select func_rule_in_risk(COUNT(1),'2007001') into v_risk_dg_2007001 from aml_customer_public_run where customer_no = v_customer_no and concat(nationality_code1 , register_area) in 
								   (select concat(nationality_code , address_area) from Aml_High_Risk_Area where danger_desc = '1');
								   -- 2007001权重过滤
								   SELECT cast(v_risk_dg_2007001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2007001 FROM aml_grade_weight WHERE subitem_no = '2007001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '007' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2007
							set v_risk_dg_2007 = cast(v_risk_dg_2007001 as decimal(20,2));
							-- 插入2007分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2007' risk_section_code,
							'2007|反洗钱、反恐怖融资监控或制载情况(对公)' risk_section_name,
							v_risk_dg_2007 new_grade_score, 
							v_risk_dg_2007 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
						
										
					/******************************************* ********* *******************************************/
				  /*                                         2008|风险提示信息                                     */
				  /******************************************* ********* *******************************************/
				 
				 
 /* -------------- 2008001|风险提示信息-客户注册国籍(地区)被FATF/APG/EAG等权威组织互评风险结果提示(对公) ------------ */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2008001 from aml_grade_weight where subitem_no = '2008001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2008001';
			        if v_risk_dg_2008001 is null or v_risk_dg_2008001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2008001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2008001初始分数
	select func_rule_in_risk(COUNT(1),'2008001') into v_risk_dg_2008001 from aml_customer_public_run where customer_no = v_customer_no and concat(nationality_code1 , register_area) in 
								   (select concat(nationality_code , address_area) from Aml_High_Risk_Area where danger_desc = '2');
								   -- 2008001权重过滤
								   SELECT cast(v_risk_dg_2008001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2008001 FROM aml_grade_weight WHERE subitem_no = '2008001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '008' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2008
							set v_risk_dg_2008 = cast(v_risk_dg_2008001 as decimal(20,2));
							-- 插入2008分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2008' risk_section_code,
							'2008|风险提示信息(对公)' risk_section_name,
							v_risk_dg_2008 new_grade_score, 
							v_risk_dg_2008 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
								
										
					/******************************************* ********* *******************************************/
				  /*                                         2009|上游犯罪状况                                     */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2009001|上游犯罪状况-客户注册国籍(地区)处于FATF发布的严重存在毒品、走私、诈骗等上游犯罪情况的国家(地区)(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2009001 from aml_grade_weight where subitem_no = '2009001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2009001';
			        if v_risk_dg_2009001 is null or v_risk_dg_2009001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2009001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2009001初始分数
	select func_rule_in_risk(COUNT(1),'2009001') into v_risk_dg_2009001 from aml_customer_public_run where customer_no = v_customer_no and concat(nationality_code1 , register_area) in 
								   (select concat(nationality_code , address_area) from Aml_High_Risk_Area where danger_desc = '3');
								   -- 2009001权重过滤
								   SELECT cast(v_risk_dg_2009001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2009001 FROM aml_grade_weight WHERE subitem_no = '2009001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '009' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2009
							set v_risk_dg_2009 = cast(v_risk_dg_2009001 as decimal(20,2));
							-- 插入2009分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2009' risk_section_code,
							'2009|上游犯罪状况(对公)' risk_section_name,
							v_risk_dg_2009 new_grade_score, 
							v_risk_dg_2009 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
	
										
					/******************************************* ********* *******************************************/
				  /*                                         2010|特殊的金融监管风险                                     */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2010001|特殊的金融监管风险-客户注册地与金融机构相距过远(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2010001 from aml_grade_weight where subitem_no = '2010001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2010001';
			        if v_risk_dg_2010001 is null or v_risk_dg_2010001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2010001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2010001初始分数
	select func_rule_compare_risk(register_inst_distance,'2010001') into v_risk_dg_2010001 from aml_customer_public_run where customer_no = v_customer_no;
								   -- 2010001权重过滤
								   SELECT cast(v_risk_dg_2010001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2010001 FROM aml_grade_weight WHERE subitem_no = '2010001';
							   end if;
					    end if;
					
			
				 
 /* ---2010002|特殊的金融监管风险-客户经营地与金融机构相距过远(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2010002 from aml_grade_weight where subitem_no = '2010002';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2010002';
			        if v_risk_dg_2010002 is null or v_risk_dg_2010002 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2010002 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2010002初始分数
	select func_rule_compare_risk(register_inst_distance,'2010002') into v_risk_dg_2010002 from aml_customer_public_run where customer_no = v_customer_no;
								   -- 2010002权重过滤
								   SELECT cast(v_risk_dg_2010002 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2010002 FROM aml_grade_weight WHERE subitem_no = '2010002';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '010' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2010
							set v_risk_dg_2010 = cast(v_risk_dg_2010001 as decimal(20,2)) + cast(v_risk_dg_2010002 as decimal(20,2));
							-- 插入2010分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2010' risk_section_code,
							'2010|特殊的金融监管风险(对公)' risk_section_name,
							v_risk_dg_2010 new_grade_score, 
							v_risk_dg_2010 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												
					
					
										
					/******************************************* ********* *******************************************/
				  /*                                        2011|地域其他风险                                   */
				  /******************************************* ********* *******************************************/
				 
				 
          /* ---2011001|地域其他风险-无(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2011001 from aml_grade_weight where subitem_no = '2011001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2011001';
			        if v_risk_dg_2011001 is null or v_risk_dg_2011001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2011001 = 0;
						       else
									  set v_risk_dg_2011001 = 0;
							   end if;
					    end if;
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '011' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2011
							set v_risk_dg_2011 = cast(v_risk_dg_2011001 as decimal(20,2));
							-- 插入2011分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2011' risk_section_code,
							'2011|地域其他风险(对公)' risk_section_name,
							v_risk_dg_2011 new_grade_score, 
							v_risk_dg_2011 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												
					

										
					/******************************************* ********* *******************************************/
				  /*                                        2012|与现金关联程度                                    */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2012001|与现金关联程度-客户一年内发生的现金交易总额(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2012001 from aml_grade_weight where subitem_no = '2012001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2012001';
			        if v_risk_dg_2012001 is null or v_risk_dg_2012001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2012001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2012001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'2012001') into v_risk_dg_2012001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and trans_type in
	('000000','000001','000002','000003,000004','000010','000011','000012','000013','000014','000015','000016','000017','000020','000021','000030','000031','000032','000033','000034','000035','000036','000051','010101','010102','010103','010104');
								   -- 2012001权重过滤
								   SELECT cast(v_risk_dg_2012001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2012001 FROM aml_grade_weight WHERE subitem_no = '2012001';
							   end if;
					    end if;
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '012' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2012
							set v_risk_dg_2012 = cast(v_risk_dg_2012001 as decimal(20,2));
							-- 插入2012分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2012' risk_section_code,
							'2012|与现金关联程度(对公)' risk_section_name,
							v_risk_dg_2012 new_grade_score, 
							v_risk_dg_2012 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
					
					
					
	

										
					/******************************************* ********* *******************************************/
				  /*                                        2013|非面对面交易                                      */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2013001|非面对面交易-客户一年内发生的非柜台业务交易总额(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2013001 from aml_grade_weight where subitem_no = '2013001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2013001';
			        if v_risk_dg_2013001 is null or v_risk_dg_2013001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2013001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2013001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'2013001') into v_risk_dg_2013001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and no_bar_trans_type <> '@N' and no_bar_trans_type <> '' and no_bar_trans_type is not null;
								   -- 2013001权重过滤
								   SELECT cast(v_risk_dg_2013001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2013001 FROM aml_grade_weight WHERE subitem_no = '2013001';
							   end if;
					    end if;
					
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '013' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2013
							set v_risk_dg_2013 = cast(v_risk_dg_2013001 as decimal(20,2));
							-- 插入2013分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2013' risk_section_code,
							'2013|非面对面交易(对公)' risk_section_name,
							v_risk_dg_2013 new_grade_score, 
							v_risk_dg_2013 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



										
					/******************************************* ********* *******************************************/
				  /*                                          2014|跨境交易                                        */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2014001|跨境交易-客户近一年内发生的跨境交易总额(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2014001 from aml_grade_weight where subitem_no = '2014001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2014001';
			        if v_risk_dg_2014001 is null or v_risk_dg_2014001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2014001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2014001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'2014001') into v_risk_dg_2014001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and trans_nationality <> 'CHN';
								   -- 2014001权重过滤
								   SELECT cast(v_risk_dg_2014001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2014001 FROM aml_grade_weight WHERE subitem_no = '2014001';
							   end if;
					    end if;
					
					
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '014' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2014
							set v_risk_dg_2014 = cast(v_risk_dg_2014001 as decimal(20,2));
							-- 插入2014分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2014' risk_section_code,
							'2014|跨境交易(对公)' risk_section_name,
							v_risk_dg_2014 new_grade_score, 
							v_risk_dg_2014 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



										
					/******************************************* ********* *******************************************/
				  /*                                          2015|代理交易                                        */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2015001|代理交易-客户发生的代理交易总额(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2015001 from aml_grade_weight where subitem_no = '2015001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2015001';
			        if v_risk_dg_2015001 is null or v_risk_dg_2015001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2015001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2015001初始分数
	select func_rule_compare_risk(sum(trans_amount_cny),'2015001') into v_risk_dg_2015001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and (agent_user_name is not null or agent_user_name <> '' or agent_user_crtft_type is not null or agent_user_crtft_type <> '' or agent_user_crtft_number is not null or agent_user_crtft_number <> '');
								   -- 2015001权重过滤
								   SELECT cast(v_risk_dg_2015001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2015001 FROM aml_grade_weight WHERE subitem_no = '2015001';
							   end if;
					    end if;

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '015' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2015
							set v_risk_dg_2015 = cast(v_risk_dg_2015001 as decimal(20,2));
							-- 插入2015分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2015' risk_section_code,
							'2015|代理交易(对公)' risk_section_name,
							v_risk_dg_2015 new_grade_score, 
							v_risk_dg_2015 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  




										
					/******************************************* ********* *******************************************/
				  /*                                   2016|特殊业务类型的交易频率                                 */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2016001|特殊业务类型的交易频率-特殊类型账户的开户数量(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2016001 from aml_grade_weight where subitem_no = '2016001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2016001';
			        if v_risk_dg_2016001 is null or v_risk_dg_2016001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2016001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2016001初始分数
	select func_rule_compare_risk(COUNT(AAR.acct_code),'2016001') into v_risk_dg_2016001 from aml_customer_public_run  acpr
								   left join Aml_Account_Run AAR on acpr.customer_no = AAR.customer_no and AAR.special_type <> '0' and acct_status = '1'; 
								   -- 2016001权重过滤
								   SELECT cast(v_risk_dg_2016001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2016001 FROM aml_grade_weight WHERE subitem_no = '2016001';
							   end if;
					    end if;
				
	 /* ---2016002|特殊业务类型的交易频率-特殊类型账户的销户数量(对公)--- */			
          select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2016002 from aml_grade_weight where subitem_no = '2016002';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2016002';
			        if v_risk_dg_2016002 is null or v_risk_dg_2016002 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2016002 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2016002初始分数
	select func_rule_compare_risk(COUNT(AAR.acct_code),'2016002') into v_risk_dg_2016002 from aml_customer_public_run  acpr
								   left join Aml_Account_Run AAR on acpr.customer_no = AAR.customer_no and AAR.special_type <> '0' and acct_status = '2'; 
								   -- 2016002权重过滤
								   SELECT cast(v_risk_dg_2016002 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2016002 FROM aml_grade_weight WHERE subitem_no = '2016002';
							   end if;
					    end if;
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '016' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2016
							set v_risk_dg_2016 = cast(v_risk_dg_2016001 as decimal(20,2)) + cast(v_risk_dg_2016002 as decimal(20,2));
							-- 插入2016分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2016' risk_section_code,
							'2016|特殊业务类型的交易频率(对公)' risk_section_name,
							v_risk_dg_2016 new_grade_score, 
							v_risk_dg_2016 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  





										
					/******************************************* ********* *******************************************/
				  /*                                        2017|金融其他风险                                      */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---2017001|金融其他风险-非自然人一年内跨境汇款频率(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2017001 from aml_grade_weight where subitem_no = '2017001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2017001';
			        if v_risk_dg_2017001 is null or v_risk_dg_2017001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2017001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2017001初始分数
	select func_rule_compare_risk(count(1),'2017001') into v_risk_dg_2017001 from aml_transaction where 
	date_id between date_sub(date_id,interval 1 year) and date_id and customer_no = v_customer_no and trans_nationality <> 'CHN'
	and payment_flag = '02'; 
								   -- 2017001权重过滤
								   SELECT cast(v_risk_dg_2017001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2017001 FROM aml_grade_weight WHERE subitem_no = '2017001';
							   end if;
					    end if;
				
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '017' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2017
							set v_risk_dg_2017 = cast(v_risk_dg_2017001 as decimal(20,2));
							-- 插入2017分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2017' risk_section_code,
							'2017|金融其他风险(对公)' risk_section_name,
							v_risk_dg_2017 new_grade_score, 
							v_risk_dg_2017 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  


								
					/******************************************* ********* *******************************************/
				  /*                                2018|是否属于公认的较高风险的行业                              */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  2018001|是否属于公认的较高风险的行业-行业类型(对公)		--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2018001 from aml_grade_weight where subitem_no = '2018001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2018001';
			        if v_risk_dg_2018001 is null or v_risk_dg_2018001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2018001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2018001初始分数
	select func_rule_in_risk(industry_code,'2018001') into v_risk_dg_2018001 from aml_customer_public_run where customer_no = v_customer_no;
								  
								   -- 2018001权重过滤
								   SELECT cast(v_risk_dg_2018001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2018001 FROM aml_grade_weight WHERE subitem_no = '2018001';
							   end if;
					    end if;
				
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '018' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2018
							set v_risk_dg_2018 = cast(v_risk_dg_2018001 as decimal(20,2));
							-- 插入2018分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2018' risk_section_code,
							'2018|是否属于公认的较高风险的行业(对公)' risk_section_name,
							v_risk_dg_2018 new_grade_score, 
							v_risk_dg_2018 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



							
					/******************************************* ********* *******************************************/
				  /*                                   2019|是否与特定洗钱风险关联                                 */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  2019001|是否与特定洗钱风险关联-客户其实际受益人属于外国政要(对公)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2019001 from aml_grade_weight where subitem_no = '2019001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2019001';
			        if v_risk_dg_2019001 is null or v_risk_dg_2019001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2019001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2019001初始分数
	select count(afd1.dign_user_name) into num1 from aml_customer_public_run acpr
	left join aml_customer_beneficiary_run acbr on acpr.customer_no = acbr.customer_no
	left join (select * from aml_foreign_dignitaries where dign_user_name is not null and dign_user_name <> '') afd1
	on afd1.dign_user_name = acbr.profit_name
	where acpr.customer_no = v_customer_no;
	
	select count(afd2.dign_user_name) into num2 from aml_customer_public_run acpr
	left join aml_customer_beneficiary_run acbr on acpr.customer_no = acbr.customer_no
	left join (select * from aml_foreign_dignitaries where dign_user_crtft_number is not null and dign_user_crtft_number <> '') afd2
	on concat(afd2.dign_user_crtft_type,dign_user_crtft_number) = concat(acbr.profit_crtft_type,acbr.profit_crtft_no)
	where acpr.customer_no = v_customer_no;
	
	select func_rule_compare_risk(num1+num2,'2019001') into v_risk_dg_2019001;

								  
				  -- 2019001权重过滤
							SELECT cast(v_risk_dg_2019001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2019001 FROM aml_grade_weight WHERE subitem_no = '2019001';
							   end if;
					    end if;
				
								

					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '019' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2019
							set v_risk_dg_2019 = cast(v_risk_dg_2019001 as decimal(20,2));
							-- 插入2019分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2019' risk_section_code,
							'2019|是否与特定洗钱风险关联(对公)' risk_section_name,
							v_risk_dg_2019 new_grade_score, 
							v_risk_dg_2019 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  




							
					/******************************************* ********* *******************************************/
				  /*                                      2020|行业现金密集程度                                    */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  2020001|行业现金密集程度-行业类型(对公)	--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2020001 from aml_grade_weight where subitem_no = '2020001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2020001';
			        if v_risk_dg_2020001 is null or v_risk_dg_2020001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2020001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2020001初始分数
	select  func_rule_in_risk(industry_code,'2020001') into v_risk_dg_2020001 from aml_customer_public_run where customer_no = v_customer_no;
							     
				  -- 2020001权重过滤
							SELECT cast(v_risk_dg_2020001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2020001 FROM aml_grade_weight WHERE subitem_no = '2020001';
							   end if;
					    end if;
				
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '020' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2020
							set v_risk_dg_2020 = cast(v_risk_dg_2020001 as decimal(20,2));
							-- 插入2020分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2020' risk_section_code,
							'2020|行业现金密集程度(对公)' risk_section_name,
							v_risk_dg_2020 new_grade_score, 
							v_risk_dg_2020 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  



										
					/******************************************* ********* *******************************************/
				  /*                                      2021|行业或职业其他风险                                  */
				  /******************************************* ********* *******************************************/
				 
				 
          /* ---2021001|地域其他风险-无(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2011001 from aml_grade_weight where subitem_no = '2021001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2021001';
			        if v_risk_dg_2021001 is null or v_risk_dg_2021001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2021001 = 0;
						       else
									  set v_risk_dg_2021001 = 0;
							   end if;
					    end if;
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '021' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2021
							set v_risk_dg_2021 = cast(v_risk_dg_2021001 as decimal(20,2));
							-- 插入2021分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2021' risk_section_code,
							'2021|行业或职业其他风险(对公)' risk_section_name,
							v_risk_dg_2021 new_grade_score, 
							v_risk_dg_2021 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
												


							
					/******************************************* ********* *******************************************/
				  /*                               2022|配合提供信息的积极程度与态度等                             */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  2022001|配合提供信息的积极程度与态度等-非自然人客户的股权或控制权结构-企业类型(对公)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2022001 from aml_grade_weight where subitem_no = '2022001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2022001';
			        if v_risk_dg_2022001 is null or v_risk_dg_2022001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2022001 = 0;
						       else
									 
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2022001初始分数
	select  func_rule_in_risk(enterprise_type,'2022001') into v_risk_dg_2022001 from aml_customer_public_run where customer_no = v_customer_no;
							     
				  -- 2022001权重过滤
							SELECT cast(v_risk_dg_2022001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(v_risk_grade as decimal(20,2)) into v_risk_dg_2022001 FROM aml_grade_weight WHERE subitem_no = '2022001';
							   end if;
					    end if;
				
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '022' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2022
							set v_risk_dg_2022 = cast(v_risk_dg_2022001 as decimal(20,2));
							-- 插入2022分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2022' risk_section_code,
							'2022|配合提供信息的积极程度与态度等(对公)' risk_section_name,
							v_risk_dg_2022 new_grade_score, 
							v_risk_dg_2022 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  




										
					/******************************************* ********* *******************************************/
				  /*                                      2023|自定义其他风险                                      */
				  /******************************************* ********* *******************************************/
				 
				 
          /* ---2023001|自定义其他风险-无(对公)--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2010001 from aml_grade_weight where subitem_no = '2023001';
						select cast(t.subitem_weight as decimal(20,2)) into v_subitem_weight from aml_grade_weight t where subitem_no = '2023001';
			        if v_risk_dg_2023001 is null or v_risk_dg_2023001 = '' then
						  -- 如果权重为0
				           if cast(v_subitem_weight as decimal(20,2)) = 0 then
								    set v_risk_dg_2023001 = 0;
						       else
									  set v_risk_dg_2023001 = 0;
							   end if;
					    end if;
					
					-- 获取大项总权重，如果权重为0，则不录入评分明细表
				    select sum(cast(t.subitem_weight as decimal(20,2))) into v_subitem_weight from aml_grade_weight t where subitem_category = '023' and customer_type = '2';
				    
					if cast(v_subitem_weight as decimal(20,2)) > 0 then
							-- 汇总 2023
							set v_risk_dg_2023 = cast(v_risk_dg_2023001 as decimal(20,2));
							-- 插入2023分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2023' risk_section_code,
							'2023|自定义其他风险(对公)' risk_section_name,
							v_risk_dg_2023 new_grade_score, 
							v_risk_dg_2023 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
						end if;  
											




							
					/******************************************* ********* *******************************************/
				  /*                                       2999|附加项风险(对公)                                   */
				  /******************************************* ********* *******************************************/
				 
				 
 /* ---  2999001|附加项风险-客户名称/证件被列入我国发布或承认的应实施反洗钱监控措施的名单；(对公)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2999001 from aml_grade_weight where subitem_no = '2999001';
			        if v_risk_dg_2999001 is null or v_risk_dg_2999001 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
								   -- 获取2999001初始分数

							  select func_rule_in_risk(COUNT(customer_no),'2999001') into v_risk_dg_2999001 from aml_customer_public_run where customer_no = v_customer_no
							  and (customer_name in (select customer_name from aml_blacklist where status = '1' and (customer_name is not null or customer_name <> ''))
							  or
							   concat(crtft_type,crtft_number) in
							   (select concat(id_type,id_num) from  aml_blacklist where status = '1' and  (id_num is not null or id_num <> ''))
							   );
				
				
				       end if;
				
				
		
 /* ---  2999002|附加项风险-客户实际受益人为外国政要或其亲属、关系密切人；(对公)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2999002 from aml_grade_weight where subitem_no = '2999002';
			        if v_risk_dg_2999002 is null or v_risk_dg_2999002 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
										select count(afd1.dign_user_name) into num1 from aml_customer_public_run acpr
	left join aml_customer_beneficiary_run acbr on acpr.customer_no = acbr.customer_no
	left join (select * from aml_foreign_dignitaries where dign_user_name is not null and dign_user_name <> '') afd1
	on afd1.dign_user_name = acbr.profit_name
	where acpr.customer_no = v_customer_no;
	
	select count(afd2.dign_user_name) into num2 from aml_customer_public_run acpr
	left join aml_customer_beneficiary_run acbr on acpr.customer_no = acbr.customer_no
	left join (select * from aml_foreign_dignitaries where dign_user_crtft_number is not null and dign_user_crtft_number <> '') afd2
	on concat(afd2.dign_user_crtft_type,dign_user_crtft_number) = concat(acbr.profit_crtft_type,acbr.profit_crtft_no)
	where acpr.customer_no = v_customer_no;
	
	select func_rule_compare_risk(num1+num2,'2999002') into v_risk_dg_2999002;
				
				       end if;
						
						
		
 /* ---  2999003|附加项风险-客户多次涉及可疑交易报告；(对公)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2999003 from aml_grade_weight where subitem_no = '2999003';
			        if v_risk_dg_2999003 is null or v_risk_dg_2999003 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
										
										select func_rule_compare_risk(COUNT(ascd.customer_no),'2999003') into v_risk_dg_2999003 from aml_customer_public_run acpr
							  left join aml_suspcs_case_detail ascd on ascd.customer_no = acpr.customer_no and ascd.audit_state = '4'  and ascd.audit_type_state = '1'
							  where acpr.customer_no = v_customer_no;
	
				       end if;
										
						
 /* ---  2999004|附加项风险-客户配合金融机构依法开展的客户尽职调查工作；(对公)			--- */
				  
					select case when subitem_score = '' then null else subitem_score end into v_risk_dg_2999004 from aml_grade_weight where subitem_no = '2999004';
			        if v_risk_dg_2999004 is null or v_risk_dg_2999004 = '' then
									  DROP table if exists aml_func_dist_str_tmp01;
	                  create table aml_func_dist_str_tmp01(str1 varchar(2000));
										
									select func_rule_in_risk(COUNT(1),'2999004') into v_risk_dg_2999004 from aml_customer_public_run acpr
							  where acpr.customer_no = v_customer_no and cust_cooperate_flag = '1';
	
				       end if;
										
		
						
							-- 汇总 2999
							set v_risk_dg_2999 = cast(v_risk_dg_2999001 as decimal(20,2))+ cast(v_risk_dg_2999002 as decimal(20,2))+ cast(v_risk_dg_2999003 as decimal(20,2))+ cast(v_risk_dg_2999004 as decimal(20,2));
							-- 插入2999分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select uuid() auto_id,
							v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							sysdate() grade_date,
							'2999' risk_section_code,
							'2999|附加项风险(对公)' risk_section_name,
							v_risk_dg_2999 new_grade_score, 
							v_risk_dg_2999 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = v_dt_date and customer_no = v_customer_no;
					


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
	'2',
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
	aml_customer_public_run acpg 
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
	
    CLOSE cur_risk_customer_dg; -- 关闭游标cur_risk_customer_dg
		end;
    END