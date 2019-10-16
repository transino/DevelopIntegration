create procedure [dbo].[proc_cust_dg_risk]
@dt_date varchar(20)
as
begin
   /*
  对公客户评级模型:
  评级模型描述：23项常规大项+1项附加大项
  模型参数引用：
  1.dt_date:数据日期
  */  
  -- 评级编码
  DECLARE @v_grade_code VARCHAR(50); 
  -- 大项编号
  DECLARE @v_risk_section_code VARCHAR(32); 
  -- 大项名称
  DECLARE @v_risk_section_name VARCHAR(512); 
  -- 数据日期
  DECLARE @v_dt_date VARCHAR(20); 
  -- 执行sql
  DECLARE @v_sql NVARCHAR(2000); 
  -- 空标识
  DECLARE @null_flag decimal(20,2); 
  -- 客户编号
  DECLARE @v_customer_no VARCHAR(200); 
  -- 字段名称
  DECLARE @v_cols VARCHAR(200); 
  -- 字段内容大小
  DECLARE @v_cols_count VARCHAR(200); 
  -- 风险等级
  DECLARE @v_risk_grade VARCHAR(1); 
  -- 数据记录条数
  DECLARE @counti VARCHAR(20); 
  -- 评级次数
  DECLARE @v_grade_num int;
  -- 明细子项权重
  DECLARE @subitem_weight VARCHAR(20);
  -- 客户信息的公开程度-基本项完整度(对公)
  DECLARE @v_risk_dg_2001001 decimal(20,2); 
  -- 客户信息的公开程度-是否上市(对公)
  DECLARE @v_risk_dg_2001002 decimal(20,2); 
  -- 客户信息的公开程度汇总分数(对公)
  DECLARE @v_risk_dg_2001 decimal(20,2); 
  -- 与客户建立或维持业务关系的渠道-与客户建立或维护业务关系的渠道(对公)
  DECLARE @v_risk_dg_2002001 decimal(20,2); 
  -- 与客户建立或维持业务关系的渠道汇总分数(对公)
  DECLARE @v_risk_dg_2002 decimal(20,2); 
  -- 客户所持身份证件或身份证明文件的种类-客户信息-证件类型(对公)
  DECLARE @v_risk_dg_2003001 decimal(20,2); 
  -- 客户所持身份证件或身份证明文件的种类汇总分数(对公)
  DECLARE @v_risk_dg_2003 decimal(20,2); 
  -- 反洗钱交易检测记录-客户、股东、法人、受益人通过终审的可疑交易笔数(对公)
  DECLARE @v_risk_dg_2004001 decimal(20,2); 
  -- 反洗钱交易检测记录汇总分数(对公)
  DECLARE @v_risk_dg_2004 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息-客户证件类型+证件号码(对公)
  DECLARE @v_risk_dg_2005001 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息-股东证件类型+证件号码(对公)
  DECLARE @v_risk_dg_2005002 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息-法人证件类型+证件号码(对公)
  DECLARE @v_risk_dg_2005003 decimal(20,2); 
  -- 涉及客户的风险提示信息或权威媒体报道信息汇总分数(对公)
  DECLARE @v_risk_dg_2005 decimal(20,2); 
  -- 客户其他风险-非自然人客户的存续时间(对公)
  DECLARE @v_risk_dg_2006001 decimal(20,2); 
  -- 客户其他风险汇总分数(对公)
  DECLARE @v_risk_dg_2006 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-客户注册国籍被反恐怖融资监控或制载(对公)
  DECLARE @v_risk_dg_2007001 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-客户注册地代码、经营地址代码、联系地址代码存在高风险(对公)
  DECLARE @v_risk_dg_2007002 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-股东国籍被反恐怖融资监控或制载(对公)
  DECLARE @v_risk_dg_2007003 decimal(20,2); 
  -- 反洗钱、反恐怖融资监控或制载情况-股东出生地代码、客户住址代码、联系地址代码存在高风险(对公)
  DECLARE @v_risk_dg_2007004 decimal(20,2);
  -- 反洗钱、反恐怖融资监控或制载情况-法人国籍被反恐怖融资监控或制载(对公)
  DECLARE @v_risk_dg_2007005 decimal(20,2);  
  -- 反洗钱、反恐怖融资监控或制载情况-法人出生地代码、客户住址代码、联系地址代码存在高风险(对公)
  DECLARE @v_risk_dg_2007006 decimal(20,2);  
  -- 反洗钱、反恐怖融资监控或制载情况汇总分数(对公)
  DECLARE @v_risk_dg_2007 decimal(20,2); 
  -- 风险提示信息-FATF发布的反洗钱恐怖融资国家地区(对公)
  DECLARE @v_risk_dg_2008001 decimal(20,2); 
  -- 风险提示信息汇总分数(对公)
  DECLARE @v_risk_dg_2008 decimal(20,2); 
  -- 上游犯罪状况-FATF发布的参与恐怖及支持恐怖活动的严重国家地区(对公)
  DECLARE @v_risk_dg_2009001 decimal(20,2); 
  -- 上游犯罪状况汇总分数(对公)
  DECLARE @v_risk_dg_2009 decimal(20,2); 
  -- 特殊的金融监管风险-客户注册地、经营地址与金融机构相距过远(对公)
  DECLARE @v_risk_dg_2010001 decimal(20,2); 
  -- 特殊的金融监管风险汇总分数(对公)
  DECLARE @v_risk_dg_2010 decimal(20,2); 
  -- 地域其他风险-无(对公)
  DECLARE @v_risk_dg_2011001 decimal(20,2); 
  -- 地域其他风险汇总分数(对公)
  DECLARE @v_risk_dg_2011 decimal(20,2);
  -- 与现金关联程度-客户一年内发生的现金交易总额(对公)
  DECLARE @v_risk_dg_2012001 decimal(20,2);
  -- 与现金关联程度汇总分数(对公)
  DECLARE @v_risk_dg_2012 decimal(20,2); 
  -- 非面对面交易-客户一年内发生的非柜台业务交易总额(对公)
  DECLARE @v_risk_dg_2013001 decimal(20,2); 
  -- 非面对面交易-非面对面交易(对公)
  DECLARE @v_risk_dg_2013002 decimal(20,2); 
  -- 非面对面交易汇总分数(对公)
  DECLARE @v_risk_dg_2013 decimal(20,2); 
  -- 跨境交易-客户一年内产生的可疑跨境交易次数(对公)
  DECLARE @v_risk_dg_2014001 decimal(20,2); 
  -- 跨境交易汇总分数(对公)
  DECLARE @v_risk_dg_2014 decimal(20,2);
  -- 代理交易-开户代办人代理账户个数(对公)
  DECLARE @v_risk_dg_2015001 decimal(20,2); 
  -- 代理交易-开户代办人涉及可疑报告(对公)
  DECLARE @v_risk_dg_2015002 decimal(20,2); 
  --代理交易-开户代办人涉及名单(对公)
  DECLARE @v_risk_dg_2015003 decimal(20,2); 
  -- 代理交易汇总分数(对公)
  DECLARE @v_risk_dg_2015 decimal(20,2);
  -- 特殊业务类型的交易频率-开户数量(对公)
  DECLARE @v_risk_dg_2016001 decimal(20,2); 
  -- 特殊业务类型的交易频率-销户数量(对公)
  DECLARE @v_risk_dg_2016002 decimal(20,2);
  -- 特殊业务类型的交易频率汇总分数(对公) 
  DECLARE @v_risk_dg_2016 decimal(20,2); 
  -- 金融其他风险-非自然人一年内跨境汇款频率(对公)
  DECLARE @v_risk_dg_2017001 decimal(20,2); 
  -- 金融其他风险汇总分数(对公)
  DECLARE @v_risk_dg_2017 decimal(20,2); 
  -- 是否属于公认的较高风险的行业-行业分类(对公)
  DECLARE @v_risk_dg_2018001 decimal(20,2); 
  -- 是否属于公认的较高风险的行业汇总分数(对公)
  DECLARE @v_risk_dg_2018 decimal(20,2);
  -- 是否与特定洗钱风险关联-其实际受益人、法人、股东属于外国政要(对公)
  DECLARE @v_risk_dg_2019001 decimal(20,2); 
  -- 是否与特定洗钱风险关联汇总分数(对公)
  DECLARE @v_risk_dg_2019 decimal(20,2); 
  -- 行业现金密集程度-企业类型(对公)
  DECLARE @v_risk_dg_2020001 decimal(20,2); 
  -- 行业现金密集程度汇总分数(对公)
  DECLARE @v_risk_dg_2020 decimal(20,2); 
  -- 行业或职业其他风险-无(对公)
  DECLARE @v_risk_dg_2021001 decimal(20,2); 
  -- 行业或职业其他风险汇总分数(对公)
  DECLARE @v_risk_dg_2021 decimal(20,2); 
  -- 配合提供信息的积极程度与态度等-非自然人客户的股权或控制权结构(对公)
  DECLARE @v_risk_dg_2022001 decimal(20,2); 
  -- 配合提供信息的积极程度与态度等汇总分数(对公)
  DECLARE @v_risk_dg_2022 decimal(20,2); 
  -- 自定义其他风险-无(对公)
  DECLARE @v_risk_dg_2023001 decimal(20,2); 
  -- 自定义其他风险汇总分数(对公)
  DECLARE @v_risk_dg_2023 decimal(20,2); 
  -- 附加项风险-客户、客户实际控制人或实际受益人被列入我国发布或承认的应实施反洗钱监控措施的名单；(对公)
  DECLARE @v_risk_dg_2999001 decimal(20,2);
  -- 附加项风险-客户、客户实际控制人或实际受益人为外国政要或其亲属、关系密切人；(对公)
  DECLARE @v_risk_dg_2999002 decimal(20,2); 
  -- 附加项风险-客户多次涉及可疑交易报告；(对公)
  DECLARE @v_risk_dg_2999003 decimal(20,2); 
  -- 附加项风险-客户拒绝金融机构依法开展的客户尽职调查工作；(对公)
  DECLARE @v_risk_dg_2999004 decimal(20,2); 
  -- 附加项风险汇总分数(对公)
  DECLARE @v_risk_dg_2999 decimal(20,2); 

  -- 受益人编号
  DECLARE @profit_auto_id varchar(200);


    select @v_dt_date = convert(date,@dt_date);
	-- 获取当前使用的风险等级
	select @v_risk_grade = '5';
	
	-- 更新客户信息中间表
	truncate table aml_customer_public_run;
	insert into aml_customer_public_run(auto_id ,check_status ,customer_no ,customer_name ,branch_code ,date_id ,industry_code ,register_area ,establish_date ,legal_name ,legal_crtft_type ,legal_other_crtft_type,legal_crtft_number,controller_name ,controller_crtft_type ,controller_oher_crtft_type,controller_crtft_number,nationality_code1,nationality_code2,phone_number1,phone_number2,contact_address ,contact_area ,management_nationality_code1,management_area1 ,management_address1 ,management_nationality_code2,management_area2 ,management_address2 ,cell_phone1 ,cell_phone2 ,crtft_type ,crtft_number,crtft_due_date ,other_crtft_type,open_date ,business_license_code,business_license_expire_date ,register_balance ,register_currency,regist_certft_code,organization_code,business_scope ,unid_social_code,listing_flag ,business_rela_type ,register_inst_distance,management_inst_distance,enterprise_type ,cust_cooperate_flag ,cover_flag ,update_date ,risk_flag,data_flag,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8 ,reserve_9 ,reserve_10 ,reserve_11 ,reserve_12 ,reserve_13 ) 
	select auto_id ,check_status ,customer_no ,customer_name ,branch_code ,date_id ,industry_code ,register_area ,establish_date ,legal_name ,legal_crtft_type ,legal_other_crtft_type,legal_crtft_number,controller_name ,controller_crtft_type ,controller_oher_crtft_type,controller_crtft_number,nationality_code1,nationality_code2,phone_number1,phone_number2,contact_address ,contact_area ,management_nationality_code1,management_area1 ,management_address1 ,management_nationality_code2,management_area2 ,management_address2 ,cell_phone1 ,cell_phone2 ,crtft_type ,crtft_number,crtft_due_date ,other_crtft_type,open_date ,business_license_code,business_license_expire_date ,register_balance ,register_currency,regist_certft_code,organization_code,business_scope ,unid_social_code,listing_flag ,business_rela_type ,register_inst_distance,management_inst_distance,enterprise_type ,cust_cooperate_flag ,cover_flag ,update_date ,risk_flag,data_flag,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8 ,reserve_9 ,reserve_10 ,reserve_11 ,reserve_12 ,reserve_13  from aml_customer_public where date_id = @v_dt_date;
	
	-- 更新账户信息中间表
	truncate table aml_account_run;
	insert into aml_account_run(auto_id,check_status,date_id,branch_code,customer_no,customer_name,acct_cata,acct_nature_type,acct_type,acct_code,acct_name,acct_open_date,bank_card_type,bank_card_other_type,acct_status,account_balance,agent_user_name,agent_user_crtft_type,agent_user_crtft_number,agent_user_nationality,cover_flag,special_type,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13 ) 
	select auto_id,check_status,date_id,branch_code,customer_no,customer_name,acct_cata,acct_nature_type,acct_type,acct_code,acct_name,acct_open_date,bank_card_type,bank_card_other_type,acct_status,account_balance,agent_user_name,agent_user_crtft_type,agent_user_crtft_number,agent_user_nationality,cover_flag,special_type,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13 from Aml_Account where date_id = @v_dt_date;
	
	-- 更新受益人信息中间表
	truncate table aml_customer_beneficiary_run;
	insert into aml_customer_beneficiary_run(auto_id,check_status,date_id,customer_no,customer_name,branch_code,profit_type,profit_name,profit_area,profit_address,profit_crtft_type,profit_crtft_no,profit_crtft_expire_date,profit_shares,profit_position,cover_flag,profit_remarks,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13) 
	select auto_id,check_status,date_id,customer_no,customer_name,branch_code,profit_type,profit_name,profit_area,profit_address,profit_crtft_type,profit_crtft_no,profit_crtft_expire_date,profit_shares,profit_position,cover_flag,profit_remarks,update_date,reserve_1,reserve_2,reserve_3,reserve_4,reserve_5,reserve_6,reserve_7,reserve_8,reserve_9,reserve_10,reserve_11,reserve_12,reserve_13 from Aml_Customer_Beneficiary where date_id = @v_dt_date;
	
	
	begin
	
    declare @cur_risk_customer_dg_isopen int;
    select @cur_risk_customer_dg_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_risk_customer_dg');
    
    if @cur_risk_customer_dg_isopen > 0 
		begin
		  close cur_risk_customer_dg;
		  deallocate cur_risk_customer_dg;
		end  
	  
	  declare cur_risk_customer_dg cursor for
	  select distinct customer_no from aml_risk_cust where customer_type = '2' order by customer_no;
      
		open cur_risk_customer_dg;
		
			fetch next from cur_risk_customer_dg into @v_customer_no;
			
			while @@FETCH_STATUS = 0  
			begin
			
			      
				  -- 检索获取评级编码
				  set @v_grade_code = convert(varchar,replace(replace(replace(replace(convert(varchar,getdate(),120),' ',''),'-',''),'.',''),':',''))+convert(varchar,@v_customer_no);
	
	

				  /******************************************* ********* *******************************************/
				  /*                                2001|客户信息的公开程度（对公）                                */
				  /******************************************* ********* *******************************************/
				 
				 
/* -------------------- 2001001|客户信息的公开程度-基本项完整度（对公） -------------------- */
				  
					  
				   declare @cur_cust_risk_2001001_isopen int;
				   select @cur_cust_risk_2001001_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_cust_risk_2001001');
				   
				   if @cur_cust_risk_2001001_isopen > 0 
						begin
						  close cur_cust_risk_2001001;
						  deallocate cur_cust_risk_2001001;
						end  
					  
				   DECLARE cur_cust_risk_2001001 CURSOR FOR 
				   SELECT cols_name FROM aml_cust_risk_cols WHERE table_name = 'aml_customer_public' AND cols_isnull = 'is not null' order by cols_name;
						
						open cur_cust_risk_2001001;
						-- 删除计算空值表
						DELETE FROM aml_cust_risk_data;
						-- 移动游标 循环获取必填字段
						fetch next from cur_cust_risk_2001001 into @v_cols;
								-- 提取成功，进行下一条数据的提取操作
								while @@FETCH_STATUS = 0  
								begin
									
									SET @v_sql = 'insert into aml_cust_risk_data(auto_id,cols_name,cols_isnull,table_name) select newid() auto_id,'''+ @v_cols+ ''' cols_name,case when len(replace('+ @v_cols +','' '','''')) is null then 0 else len(replace('+ @v_cols+ ','' '',''''))  end cols_isnull,''aml_customer_public'' table_name from aml_customer_public_run where customer_no = '''+ @v_customer_no+'''';
								    
								    exec sp_executesql @v_sql;
									
									fetch next from cur_cust_risk_2001001 into @v_cols;
								
								end;
								 
						close cur_cust_risk_2001001; 
						deallocate cur_cust_risk_2001001;
			    
			     -- 判断受益人个数统计游标是否开启 
					 declare @cur_cust_risk_2001001_1_isopen int;
				   waitfor delay '000:00:00:500'
				   select @cur_cust_risk_2001001_1_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_cust_risk_2001001_1');
				   
				   if @cur_cust_risk_2001001_1_isopen > 0 
						begin
						  close cur_cust_risk_2001001_1;
						  deallocate cur_cust_risk_2001001_1;
						end  
					       
				   DECLARE cur_cust_risk_2001001_1 CURSOR FOR 
				   SELECT auto_id FROM Aml_Customer_Beneficiary_Run WHERE customer_no = @v_customer_no order by auto_id;
						
				   open cur_cust_risk_2001001_1;

			           -- 移动游标 循环获取必填字段
				   fetch next from cur_cust_risk_2001001_1 into @profit_auto_id;

			           -- 提取成功，进行下一条数据的提取操作
				   while @@FETCH_STATUS = 0  

				   begin
       			           -- 判断受益人必填字段游标是否开启 
       				   declare @cur_cust_risk_2001001_2_isopen int;
       				   select @cur_cust_risk_2001001_2_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_cust_risk_2001001_2');
       				   
       				   if @cur_cust_risk_2001001_2_isopen > 0 
       						begin
       						  close cur_cust_risk_2001001_2;
       						  deallocate cur_cust_risk_2001001_2;
       						end  
       					  
       				   DECLARE cur_cust_risk_2001001_2 CURSOR FOR 
       				   SELECT cols_name FROM aml_cust_risk_cols WHERE table_name = 'aml_customer_beneficiary' AND cols_isnull = 'is not null' order by cols_name;
       						
       						open cur_cust_risk_2001001_2;     
       			           -- 移动游标 循环获取必填字段
       						fetch next from cur_cust_risk_2001001_2 into @v_cols;
       								-- 提取成功，进行下一条数据的提取操作
       								while @@FETCH_STATUS = 0  
       								begin
       			      
       			      		SET @v_sql = 'insert into aml_cust_risk_data(auto_id,cols_name,cols_isnull,table_name) select newid() auto_id,'''+ @v_cols+ ''' cols_name,case when len(replace('+ @v_cols +','' '','''')) is null then 0 else len(replace('+ @v_cols+ ','' '',''''))  end cols_isnull,''aml_customer_beneficiary'' table_name from aml_customer_beneficiary_run where auto_id = '''+ @profit_auto_id +'''';
       								    
       								    exec sp_executesql @v_sql;
       								    fetch next from cur_cust_risk_2001001_2 into @v_cols;
       								
       								end;
       								 
       						close cur_cust_risk_2001001_2; 
       						deallocate cur_cust_risk_2001001_2;
       						
			      
       				fetch next from cur_cust_risk_2001001_1 into @profit_auto_id;
			      
			       end 
			        
			        
			        
			        declare @public_nnum decimal(20,2);
			        declare @public_snum decimal(20,2);
			        
			        declare @beneficiary_nnum decimal(20,2);
			        declare @beneficiary_snum decimal(20,2);
			        
			        select @public_nnum = count(1) from aml_cust_risk_data where table_name = 'aml_customer_public' and cols_isnull > 0;
			        select @public_snum = count(1) from aml_cust_risk_data where table_name = 'aml_customer_public';
			        
			        select @beneficiary_nnum = count(1) from aml_cust_risk_data where table_name = 'aml_customer_beneficiary' and cols_isnull > 0;
			        select @beneficiary_snum = count(1) from aml_cust_risk_data where table_name = 'aml_customer_beneficiary';
			        
			        -- 如果对公客户受益人条数为0,则默认空缺一条受益人信息
			        if (@beneficiary_snum = 0)
			        begin
			             SELECT @beneficiary_nnum = 0;
			             SELECT @beneficiary_snum = count(1) FROM aml_cust_risk_cols WHERE table_name = 'aml_customer_beneficiary' AND cols_isnull = 'is not null';
			        end
			        
			        
			        select @v_risk_dg_2001001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2001001';
			        
			        -- 如果未配置子项初始化分数则通过计算公式获取
			        if @v_risk_dg_2001001 is null or @v_risk_dg_2001001 = ''
						  begin
						   -- 如果权重为0
				           if cast(@subitem_weight as decimal(20,2)) = 0
							   begin
								    set @v_risk_dg_2001001 = 0;
							   end
						   else
							   begin
									-- 获取2001001|基本项完整度初始分数      
									if @public_snum = 0
										begin    
											set @v_risk_dg_2001001 = 0;
										end     
									else
										begin
				    						SELECT @v_risk_dg_2001001 = dbo.func_rule_compare_risk ((@public_nnum+@beneficiary_nnum) / (@public_snum+@beneficiary_snum), '2001001' );
											
											
											
											-- 2001001|基本项完整度权重过滤
											SELECT @v_risk_dg_2001001 = cast(@v_risk_dg_2001001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
											FROM aml_grade_weight WHERE subitem_no = '2001001';
							            end
							    end
						end
						
				  	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2001001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2001001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2001001' risk_section_code,
							'2001001|客户信息的公开程度-基本项完整度(对公)' risk_section_name,
							@v_risk_dg_2001001 new_grade_score, 
							@v_risk_dg_2001001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
					 
					
					
				   /* -------------------- 2001002|客户信息的公开程度-是否上市(对公) -------------------- */
				    
				    select @v_risk_dg_2001002 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2001002'
			        if @v_risk_dg_2001002 is null or @v_risk_dg_2001002 = ''
						begin
						-- 如果权重为0
				           if cast(@subitem_weight as decimal(20,2)) = 0
							   begin
								    set @v_risk_dg_2001002 = 0;
							   end
						   else
							   begin
								   -- 获取2001002|客户信息的公开程度-是否上市(对公)初始分数        
								   select @v_risk_dg_2001002 = dbo.func_rule_in_risk(listing_flag,'2001002') from aml_customer_public_run where customer_no = @v_customer_no;
								   -- 2001002|客户信息的公开程度-是否上市(对公)权重过滤
								   SELECT @v_risk_dg_2001002 = cast(@v_risk_dg_2001002 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
								   FROM aml_grade_weight WHERE subitem_no = '2001002';
							   end
					    end
					    
					    
							
				  	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2001002';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2001002分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2001002' risk_section_code,
							'2001002|客户信息的公开程度-是否上市(对公)' risk_section_name,
							@v_risk_dg_2001002 new_grade_score, 
							@v_risk_dg_2001002 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
					
					
				     
				     
				  /******************************************* ********* *******************************************/
				  /*                                2006|客户其他风险（对公）                                */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------------- 2006001|客户其他风险-非自然人客户的存续时间(对公) -------------------- */
				  
				   select @v_risk_dg_2006001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2006001'
			       if @v_risk_dg_2006001 is null or @v_risk_dg_2006001 = ''
			            begin
						-- 如果权重为0
				           if cast(@subitem_weight as decimal(20,2)) = 0
							    begin
								    set @v_risk_dg_2006001 = 0;
							    end
						   else
								begin
								   -- 获取2006001|客户其他风险-非自然人客户的存续时间(对公)初始分数        
								   select @v_risk_dg_2006001 = dbo.func_rule_compare_risk(DATEDIFF(year,open_date,GETDATE()),'2006001') from aml_customer_public_run where customer_no = @v_customer_no;
								   -- 2006001|客户其他风险-非自然人客户的存续时间(对公)权重过滤
								   SELECT @v_risk_dg_2006001 = cast(@v_risk_dg_2006001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
								   FROM aml_grade_weight WHERE subitem_no = '2006001';
								   
								end
					    end
			       
			           
							
				  	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2006001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2006001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2006001' risk_section_code,
							'2006001|客户其他风险-非自然人客户的存续时间(对公)' risk_section_name,
							@v_risk_dg_2006001 new_grade_score, 
							@v_risk_dg_2006001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
			       
				  
				  /******************************************* ********* *******************************************/
				  /*                          2007|反洗钱、反恐怖融资监控或制载情况（对公）                        */
				  /******************************************* ********* *******************************************/
				 
				  
				  /* -------------------- 2007001|反洗钱、反恐怖融资监控或制载情况-客户注册国籍(地区)受我国反洗钱监控或制载(对公) -------------------- */
				  
				   select @v_risk_dg_2007001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2007001'
			       if @v_risk_dg_2007001 is null or @v_risk_dg_2007001 = ''
					   begin
						   -- 如果权重为0
						   if cast(@subitem_weight as decimal(20,2)) = 0
								begin
									set @v_risk_dg_2007001 = 0;
								end
						   else
								begin
								   -- 获取2007001|反洗钱、反恐怖融资监控或制载情况-客户注册国籍(地区)受我国反洗钱监控或制载(对公)初始分数      
								   select @v_risk_dg_2007001 = dbo.func_rule_in_risk(COUNT(1),'2007001') from aml_customer_public_run where customer_no = @v_customer_no and cast(nationality_code1 as varchar)+cast(register_area as varchar) in 
								   (select cast(nationality_code as varchar)+cast(address_area as varchar) from Aml_High_Risk_Area where danger_desc = '1');
									-- 2007001|反洗钱、反恐怖融资监控或制载情况-客户注册国籍被反恐怖融资监控或制载(对公)权重过滤
								   SELECT @v_risk_dg_2007001 = cast(@v_risk_dg_2007001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
								   FROM aml_grade_weight WHERE subitem_no = '2007001';
								end
					   end
					   
					 	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2007001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2007001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2007001' risk_section_code,
							'2007001|反洗钱、反恐怖融资监控或制载情况-客户注册国籍(地区)被我国反洗钱监控或制载(对公)' risk_section_name,
							@v_risk_dg_2007001 new_grade_score, 
							@v_risk_dg_2007001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
					  
				   
				   
				  /******************************************* ********* *******************************************/
				  /*                                    2008|风险提示信息(对公)                                    */
				  /******************************************* ********* *******************************************/
				 
				 
				   /* -------------------- 2008001|风险提示信息-客户注册国籍(地区)被FATF/APG/EAG等权威组织互评风险结果提示(对公) -------------------- */
				  
				   -- 获取2008001|风险提示信息-客户注册国籍(地区)被FATF/APG/EAG等权威组织互评风险结果提示(对公)初始分数      
				  select @v_risk_dg_2008001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2008001'
					if @v_risk_dg_2008001 is null or @v_risk_dg_2008001 = ''
						begin
							-- 如果权重为0
							if cast(@subitem_weight as decimal(20,2)) = 0
								begin
									set @v_risk_dg_2008001 = 0;
								end
							else
								begin
					  
								   select @v_risk_dg_2008001 = dbo.func_rule_in_risk(COUNT(1),'2008001') from aml_customer_public_run where customer_no = @v_customer_no and cast(nationality_code1 as varchar)+cast(register_area as varchar) in 
								   (select cast(nationality_code as varchar)+cast(address_area as varchar) from Aml_High_Risk_Area where danger_desc = '2');
								   -- 风险提示信息-2008001|风险提示信息-客户注册国籍(地区)被FATF/APG/EAG等权威组织互评风险结果提示(对公)权重过滤
								   SELECT @v_risk_dg_2008001 = cast(@v_risk_dg_2008001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
								   FROM aml_grade_weight WHERE subitem_no = '2008001';
								   
								end
						end
						
						 
					
					 	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2008001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2008001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2008001' risk_section_code,
							'2008001|风险提示信息-客户注册国籍(地区)被FATF/APG/EAG等权威组织互评风险结果提示(对公)' risk_section_name,
							@v_risk_dg_2007001 new_grade_score, 
							@v_risk_dg_2007001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
					   	
						
						
				   
				   
				  /******************************************* ********* *******************************************/
				  /*                                      2009|上游犯罪状况(对公)                                  */
				  /******************************************* ********* *******************************************/
				 
				 
				   
				   /* -------------------- 2009001|上游犯罪状况-客户注册国籍(地区)处于FATF发布的严重存在毒品、走私、诈骗等上游犯罪情况的国家(地区)(对公) -------------------- */
				  
				   -- 获取2009001|上游犯罪状况-客户注册国籍(地区)处于FATF发布的严重存在毒品、走私、诈骗等上游犯罪情况的国家(地区)(对公)初始分数      
				   
				   select @v_risk_dg_2009001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2009001'
					if @v_risk_dg_2009001 is null or @v_risk_dg_2009001 = ''
						begin
						-- 如果权重为0
						   if cast(@subitem_weight as decimal(20,2)) = 0
								begin
									set @v_risk_dg_2009001 = 0;
								end
						   else
								begin
				   
								   select @v_risk_dg_2009001 = dbo.func_rule_in_risk(COUNT(1),'2009001') from aml_customer_public_run where customer_no = @v_customer_no and cast(nationality_code1 as varchar)+cast(register_area as varchar) in 
								   (select cast(nationality_code as varchar)+cast(address_area as varchar) from Aml_High_Risk_Area where danger_desc = '3');
								   -- 风险提示信息-2009001|上游犯罪状况-客户注册国籍(地区)处于FATF发布的严重存在毒品、走私、诈骗等上游犯罪情况的国家(地区)(对公)权重过滤
								   SELECT @v_risk_dg_2009001 = cast(@v_risk_dg_2009001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
								   FROM aml_grade_weight WHERE subitem_no = '2009001';
								
								end
						end
					
					 	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2009001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2009001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2009001' risk_section_code,
							'2009001|上游犯罪状况-客户注册国籍(地区)处于FATF发布的严重存在毒品、走私、诈骗等上游犯罪情况的国家(地区)(对公)' risk_section_name,
							@v_risk_dg_2009001 new_grade_score, 
							@v_risk_dg_2009001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
						
				   
				   
				  /******************************************* ********* *******************************************/
				  /*                               2016|特殊业务类型的交易频率(对公)                               */
				  /******************************************* ********* *******************************************/
				 
				 
				   
				   /* -------------------- 2016001|特殊业务类型的交易频率-开户数量(对公) -------------------- */
				   
				   select @v_risk_dg_2016001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2016001'
				   if @v_risk_dg_2016001 is null or @v_risk_dg_2016001 = ''
						begin
						-- 如果权重为0
						   if cast(@subitem_weight as decimal(20,2)) = 0
								begin
									set @v_risk_dg_2016001 = 0;
								end
						   else
								begin
								   -- 获取2016001|特殊业务类型的交易频率-开户数量(对公)初始分数      
								   select @v_risk_dg_2016001 = dbo.func_rule_compare_risk(COUNT(AAR.acct_code),'2016001') from aml_customer_public_run  acpr
								   left join Aml_Account_Run AAR on acpr.customer_no = AAR.customer_no and AAR.special_type <> '0' and acct_status = '1'
								   where acpr.customer_no = @v_customer_no  
								   -- 2016001|特殊业务类型的交易频率-开户数量(对公)权重过滤
								   SELECT @v_risk_dg_2016001 = cast(@v_risk_dg_2016001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
								   FROM aml_grade_weight WHERE subitem_no = '2016001';
				                end
				        end
				   
				   
				    	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2016001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2016001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2016001' risk_section_code,
							'2016001|特殊业务类型的交易频率-开户数量(对公)' risk_section_name,
							@v_risk_dg_2016001 new_grade_score, 
							@v_risk_dg_2016001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
				   
				   
				   
				        
				   /* -------------------- 2016002|特殊业务类型的交易频率-销户数量(对公) -------------------- */
				     
				   select @v_risk_dg_2016002 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2016002'
				   if @v_risk_dg_2016002 is null or @v_risk_dg_2016002 = ''
						begin
						-- 如果权重为0
						   if cast(@subitem_weight as decimal(20,2)) = 0
								begin
									set @v_risk_dg_2016002 = 0;
								end
						   else
								begin
									   -- 获取2016002|特殊业务类型的交易频率-销户数量(对公)初始分数   
									   select @v_risk_dg_2016002 = dbo.func_rule_compare_risk(COUNT(AAR.acct_code),'2016002') from aml_customer_public_run  acpr
									   left join Aml_Account_Run AAR on acpr.customer_no = AAR.customer_no and AAR.special_type <> '0' and acct_status = '2'
									   where acpr.customer_no = @v_customer_no  
									   -- 2016002|特殊业务类型的交易频率-销户数量(对公)权重过滤
									   SELECT @v_risk_dg_2016002 = cast(@v_risk_dg_2016002 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
									   FROM aml_grade_weight WHERE subitem_no = '2016002';
				                end
				        end
				        
				        
				        
				    
				   
				    	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2016002';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2016001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2016002' risk_section_code,
							'2016002|特殊业务类型的交易频率-销户数量(对公)' risk_section_name,
							@v_risk_dg_2016002 new_grade_score, 
							@v_risk_dg_2016002 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
				   
				        
				   
				   
				   
				  /******************************************* ********* *******************************************/
				  /*                              2018|是否属于公认的较高风险的行业(对公)                          */
				  /******************************************* ********* *******************************************/
				 
				   /* -------------------- 2018001|是否属于公认的较高风险的行业-行业分类(对公) -------------------- */
				   
				   select @v_risk_dg_2018001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2018001'
				   if @v_risk_dg_2018001 is null or @v_risk_dg_2018001 = ''
						begin
						-- 如果权重为0
						   if cast(@subitem_weight as decimal(20,2)) = 0
								begin
									set @v_risk_dg_2018001 = 0;
								end
						   else
								begin
				   
								   -- 获取2018001|是否属于公认的较高风险的行业-行业分类(对公)初始分数        
								   select @v_risk_dg_2018001 = dbo.func_rule_in_risk(industry_code,'2018001') from aml_customer_public_run where customer_no = @v_customer_no;
								   -- 2018001|是否属于公认的较高风险的行业-行业分类(对公)权重过滤
								   SELECT @v_risk_dg_2018001 = cast(@v_risk_dg_2018001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
								   FROM	aml_grade_weight WHERE subitem_no = '2018001';
								   
								end
						end		
				   
				   
				    	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2018001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2018001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2018001' risk_section_code,
							'2018001|是否属于公认的较高风险的行业-行业类型(对公)' risk_section_name,
							@v_risk_dg_2018001 new_grade_score, 
							@v_risk_dg_2018001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end   
				   
				   
				   
				  /******************************************* ********* *******************************************/
				  /*                                    2020|行业现金密集程度(对公)                                */
				  /******************************************* ********* *******************************************/
				 
				  /* -------------------- 2020001|行业现金密集程度-行业类型(对公) -------------------- */
				  
				  select @v_risk_dg_2020001 = case when subitem_score = '' then null else subitem_score end,@subitem_weight = cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2020001'
				  if @v_risk_dg_2020001 is null or @v_risk_dg_2020001 = ''
					 begin
					 -- 如果权重为0
					    if cast(@subitem_weight as decimal(20,2)) = 0
							begin
								set @v_risk_dg_2020001 = 0;
							end
					    else
							begin
							   -- 获取2020001|行业现金密集程度-行业类型(对公)初始分数        
							   select @v_risk_dg_2020001 = dbo.func_rule_in_risk(industry_code,'2020001') from aml_customer_public_run where customer_no = @v_customer_no;
							   -- 2020001|行业现金密集程度-行业类型(对公)权重过滤
							   SELECT @v_risk_dg_2020001 = cast(@v_risk_dg_2020001 as decimal(20,2)) * cast(subitem_weight as decimal(20,2)) / cast(@v_risk_grade as decimal(20,2)) 
							   FROM	aml_grade_weight WHERE subitem_no = '2020001';
							end
					 end
				   
				   
				   
				    	--获取子项总权重，如果权重为0，则不录入评分明细表
				    select @subitem_weight =  cast(subitem_weight as decimal(20,2)) from aml_grade_weight where subitem_no = '2020001';
				    
				    if cast(@subitem_weight as decimal(20,2)) > 0
						begin
							-- 插入2020001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2020001' risk_section_code,
							'2020001|行业现金密集程度-行业类型(对公)' risk_section_name,
							@v_risk_dg_2020001 new_grade_score, 
							@v_risk_dg_2020001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						end 
				   
				   
				   
				  /******************************************* ********* *******************************************/
				  /*                                        2999|附加项风险                                        */
				  /******************************************* ********* *******************************************/
				 
				 
				  /* -------------------- 2999001|附加项风险-客户姓名或证件存在于我国发布或承认的应实施反洗钱监控措施的名单(对公) -------------------- */
				  select @v_risk_dg_2999001 = case when subitem_score = '' then null else subitem_score end from aml_grade_weight where subitem_no = '2999001'
					if @v_risk_dg_2999001 is null or @v_risk_dg_2999001 = ''
						
						begin 
							  select @v_risk_dg_2999001 = dbo.func_rule_in_risk(COUNT(customer_no),'2999001') from aml_customer_public_run where customer_no = @v_customer_no
							  and (customer_name in (select customer_name from aml_blacklist where status = '1' and (customer_name is not null or customer_name <> ''))
							  or
							   cast(crtft_type as varchar) + cast(crtft_number as varchar) in
							   (select CAST(id_type as varchar) + CAST(id_num as varchar) from  aml_blacklist where status = '1' and  (id_num is not null or id_num <> ''))
							   );
							 
		                end
		                
		                
		             	--获取子项总权重，如果权重为0，则不录入评分明细表
				    
				   
							-- 插入2999001分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2999001' risk_section_code,
							'2999001|附加项风险-客户姓名或证件存在于我国发布或承认的应实施反洗钱监控措施的名单(对公)' risk_section_name,
							@v_risk_dg_2999001 new_grade_score, 
							@v_risk_dg_2999001 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						 
				   
				  /* -------------------- 2999003|附加项风险-客户多次涉及可疑交易报告(对公) -------------------- */
				  select @v_risk_dg_2999003 = case when subitem_score = '' then null else subitem_score end from aml_grade_weight where subitem_no = '2999003'
					if @v_risk_dg_2999003 is null or @v_risk_dg_2999003 = ''
						
						begin  
						
							  select @v_risk_dg_2999003 = dbo.func_rule_compare_risk(COUNT(ascd.customer_no),'2999003') from aml_customer_public_run acpr
							  left join aml_suspcs_case_detail ascd on ascd.customer_no = acpr.customer_no and ascd.audit_state = '4'  and ascd.audit_type_state = '1'
							  where acpr.customer_no = @v_customer_no;
						
						end	  
				  
				   
							-- 插入2999003分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2999003' risk_section_code,
							'2999003|附加项风险-客户多次涉及可疑交易报告；(对公)' risk_section_name,
							@v_risk_dg_2999003 new_grade_score, 
							@v_risk_dg_2999003 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						
				   
						   
				  /* -------------------- 2999004|附加项风险-客户配合金融机构依法开展的客户尽职调查工作(对公) -------------------- */
				  select @v_risk_dg_2999004 = case when subitem_score = '' then null else subitem_score end from aml_grade_weight where subitem_no = '2999004'
					if @v_risk_dg_2999004 is null or @v_risk_dg_2999004 = ''
						
						begin  
									 
							  select @v_risk_dg_2999004 = dbo.func_rule_in_risk(COUNT(1),'2999004') from aml_customer_public_run acpr
							  where acpr.customer_no = @v_customer_no and cust_cooperate_flag = '1';
							  
						end    
						
						
						
				   
							-- 插入2999004分数
							insert into aml_grade_detail(auto_id,grade_code,customer_no,customer_name,grade_date,risk_section_code,risk_section_name,new_grade_score,system_grade_score,rule_source,grade_source,grade_cust,grade_reason)
							select newid() auto_id,
							@v_grade_code grade_code,
							customer_no customer_no,
							customer_name customer_name,
							getdate() grade_date,
							'2999004' risk_section_code,
							'2999004|附加项风险-客户配合金融机构依法开展的客户尽职调查工作；(对公)' risk_section_name,
							@v_risk_dg_2999004 new_grade_score, 
							@v_risk_dg_2999004 system_grade_score,
							'1' rule_source,  -- 评分规则来源 规则评分
							'1' grade_source, -- 评分数据来源 系统评分
							'系统' grade_cust,
							'模型计算' grade_reason from Aml_Customer_Public_Run where date_id = @v_dt_date and customer_no = @v_customer_no;
						
				   
	    -- 输出评级总分表
	    update aml_grade set grade_state = '2' where customer_no = @v_customer_no and grade_state = '0';
		-- 获取评级次数
		select @v_grade_num = case when max(grade_number) IS null then 1 else max(grade_number)+1 end from aml_grade where customer_no = @v_customer_no
		
		insert into aml_grade
		select 
		newid() auto_id,
		'0' audit_state,
		@v_grade_code grade_code,
		@v_dt_date date_id,
		'2' customer_type,
		acpr.customer_no customer_no,
		acpr.customer_name customer_name,
		agd.grade_score grade_score,
		ad3.constDetailNum credit_rate_code3,
		ad4.constDetailNum credit_rate_code4,
		ad5.constDetailNum credit_rate_code5,
		@v_grade_num grade_number,
		getdate() grade_date,
		'0' grade_state,
		convert(varchar,getdate()+convert(int,ad3limit.constDetailName),111) risk_due_date3,
		convert(varchar,getdate()+convert(int,ad4limit.constDetailName),111) risk_due_date4,
		convert(varchar,getdate()+convert(int,ad5limit.constDetailName),111) risk_due_date5,
		'' junior_help_user,
		'' repeat_help_user,
		'' cutout_help_user,
		acpr.branch_code branch_code
		from 
		aml_customer_public_run acpr 
		left join (select sum(new_grade_score) grade_score,max(customer_no) customer_no,grade_code from aml_grade_detail where grade_code = @v_grade_code group by grade_code) agd on agd.customer_no = acpr.customer_no
		left join aml_const_detail ad3 on ad3.constNum = 'sys_057' and agd.grade_score between substring(ad3.constDetailName,1,charindex('-',ad3.constDetailName)-1) and substring(ad3.constDetailName,charindex('-',ad3.constDetailName)+1,LEN(ad3.constDetailName)-charindex('-',ad3.constDetailName)) and ad3.constDetailNum not like '%limit%'
		left join aml_const_detail ad3limit on ad3limit.constNum = 'sys_057' and ad3limit.constDetailNum like '%limit%' and ad3.constDetailNum = substring(ad3limit.constDetailNum,1,charindex('_',ad3limit.constDetailNum)-1)
		
		left join aml_const_detail ad4 on ad4.constNum = 'sys_058' and agd.grade_score between substring(ad4.constDetailName,1,charindex('-',ad4.constDetailName)-1) and substring(ad4.constDetailName,charindex('-',ad4.constDetailName)+1,LEN(ad4.constDetailName)-charindex('-',ad4.constDetailName)) and ad4.constDetailNum not like '%limit%'
		left join aml_const_detail ad4limit on ad4limit.constNum = 'sys_058' and ad4limit.constDetailNum like '%limit%' and ad4.constDetailNum = substring(ad4limit.constDetailNum,1,charindex('_',ad4limit.constDetailNum)-1)
		
		left join aml_const_detail ad5 on ad5.constNum = 'sys_059' and agd.grade_score between substring(ad5.constDetailName,1,charindex('-',ad5.constDetailName)-1) and substring(ad5.constDetailName,charindex('-',ad5.constDetailName)+1,LEN(ad5.constDetailName)-charindex('-',ad5.constDetailName)) and ad5.constDetailNum not like '%limit%'
		left join aml_const_detail ad5limit on ad5limit.constNum = 'sys_059' and ad5limit.constDetailNum like '%limit%' and ad5.constDetailNum = substring(ad5limit.constDetailNum,1,charindex('_',ad5limit.constDetailNum)-1)
		where acpr.customer_no = @v_customer_no;
	 
	  -- 移动游标到下一条记录
	  fetch next from cur_risk_customer_dg into @v_customer_no;  
	  
	                     
    end;
			
	  -- 关闭游标
      close cur_risk_customer_dg;
	  deallocate cur_risk_customer_dg;
	  
   end


end