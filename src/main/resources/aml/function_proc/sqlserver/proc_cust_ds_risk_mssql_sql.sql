CREATE procedure [dbo].[proc_cust_ds_risk]
@dt_date varchar(20)
as
begin
   /*
  对公客户评级模型:
  评级模型描述：23项常规大项+1项附加大项
  模型参数引用：
  1.v_type:传入参数类型（1:当v_type为1时,v_str传入客户编号,多个客户编号以逗号隔开；2:当v_type为2时,v_str传入某表中某列的所有值）
  2.v_str:根据v_type的类型传入不同的值,当传入的是某表中某列的所有值时,格式为'xxtable.xxcols'
  3.dt_date:数据日期
  */  
  -- 评级编码
  DECLARE @v_grade_code VARCHAR(50); 
  -- 大项编号
  DECLARE @v_risk_section_code VARCHAR(32); 
  -- 大项名称
  DECLARE @v_risk_section_name VARCHAR(512); 
  -- 数据日期
  DECLARE @v_dt_date VARCHAR(20); 
  -- 空标识
  DECLARE @null_flag VARCHAR(10); 
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
  -- 客户信息的公开程度-基本项完整度
  DECLARE @v_risk_ds_1001001 VARCHAR(10); 
  -- 客户信息的公开程度汇总分数
  DECLARE @v_risk_ds_1001 VARCHAR(10); 
  -- 与客户建立或维持业务关系的渠道-与客户建立或维护业务关系的渠道
  DECLARE @v_risk_ds_1002001 VARCHAR(10); 
  -- 与客户建立或维持业务关系的渠道汇总分数
  DECLARE @v_risk_ds_1002 VARCHAR(10); 
  -- 客户所持身份证件或身份证明文件的种类-客户信息-证件类型
  DECLARE @v_risk_ds_1003001 VARCHAR(10); 
  -- 客户所持身份证件或身份证明文件的种类汇总分数
  DECLARE @v_risk_ds_1003 VARCHAR(10); 
  -- 反洗钱交易检测记录-客户通过终审的可疑交易笔数
  DECLARE @v_risk_ds_1004001 VARCHAR(10); 
  -- 反洗钱交易检测记录汇总分数
  DECLARE @v_risk_ds_1004 VARCHAR(10); 
  -- 涉及客户的风险提示信息或权威媒体报道信息-客户证件类型+证件号码
  DECLARE @v_risk_ds_1005001 VARCHAR(10); 
  -- 涉及客户的风险提示信息或权威媒体报道信息汇总分数
  DECLARE @v_risk_ds_1005 VARCHAR(10); 
  -- 客户其他风险-自然人客户年龄
  DECLARE @v_risk_ds_1006001 VARCHAR(10); 
  -- 客户其他风险汇总分数
  DECLARE @v_risk_ds_1006 VARCHAR(10); 
  -- 反洗钱、反恐怖融资监控或制载情况-客户国籍被反恐怖融资监控或制载
  DECLARE @v_risk_ds_1007001 VARCHAR(10); 
  -- 反洗钱、反恐怖融资监控或制载情况-客户出生地代码、客户住址代码、联系地址代码存在高风险
  DECLARE @v_risk_ds_1007002 VARCHAR(10); 
  -- 反洗钱、反恐怖融资监控或制载情况汇总分数
  DECLARE @v_risk_ds_1007 VARCHAR(10); 
  -- 风险提示信息-FATF发布的反洗钱恐怖融资国家地区
  DECLARE @v_risk_ds_1008001 VARCHAR(10); 
  -- 风险提示信息汇总分数
  DECLARE @v_risk_ds_1008 VARCHAR(10); 
  -- 上游犯罪状况-FATF发布的参与恐怖及支持恐怖活动的的严重国家地区
  DECLARE @v_risk_ds_1009001 VARCHAR(10); 
  -- 上游犯罪状况汇总分数
  DECLARE @v_risk_ds_1009 VARCHAR(10); 
  -- 特殊的金融监管风险-客户出生地、客户住址与金融机构相距过远
  DECLARE @v_risk_ds_1010001 VARCHAR(10); 
  -- 特殊的金融监管风险汇总分数
  DECLARE @v_risk_ds_1010 VARCHAR(10); 
  -- 地域其他风险-无
  DECLARE @v_risk_ds_1011001 VARCHAR(10); 
  -- 地域其他风险汇总分数
  DECLARE @v_risk_ds_1011 VARCHAR(10); 
  -- 与现金关联程度-客户一年内发生的现金交易总额
  DECLARE @v_risk_ds_1012001 VARCHAR(10);
  -- 与现金关联程度汇总分数
  DECLARE @v_risk_ds_1012 VARCHAR(10); 
  -- 非面对面交易-客户一年内发生的非柜台业务交易总额
  DECLARE @v_risk_ds_1013001 VARCHAR(10); 
  -- 非面对面交易-非面对面交易
  DECLARE @v_risk_ds_1013002 VARCHAR(10); 
  -- 非面对面交易汇总分数
  DECLARE @v_risk_ds_1013 VARCHAR(10); 
  -- 跨境交易-客户一年内产生的可疑跨境交易次数
  DECLARE @v_risk_ds_1014001 VARCHAR(10); 
  -- 跨境交易汇总分数
  DECLARE @v_risk_ds_1014 VARCHAR(10); 
  -- 代理交易-开户代办人代理账户个数
  DECLARE @v_risk_ds_1015001 VARCHAR(10); 
  -- 代理交易-开户代办人涉及可疑报告
  DECLARE @v_risk_ds_1015002 VARCHAR(10); 
  -- 代理交易-开户代办人涉及名单
  DECLARE @v_risk_ds_1015003 VARCHAR(10); 
  -- 代理交易汇总分数
  DECLARE @v_risk_ds_1015 VARCHAR(10);
  -- 特殊业务类型的交易频率-开户数量
  DECLARE @v_risk_ds_1016001 VARCHAR(10); 
  -- 特殊业务类型的交易频率-销户数量
  DECLARE @v_risk_ds_1016002 VARCHAR(10);
  -- 特殊业务类型的交易频率汇总分数 
  DECLARE @v_risk_ds_1016 VARCHAR(10); 
  -- 金融其他风险-自然人一年内跨境汇款频率
  DECLARE @v_risk_ds_1017001 VARCHAR(10); 
  -- 金融其他风险汇总分数
  DECLARE @v_risk_ds_1017 VARCHAR(10); 
  -- 是否属于公认的较高风险的行业-个人职业
  DECLARE @v_risk_ds_1018001 VARCHAR(10); 
  -- 是否属于公认的较高风险的行业汇总分数
  DECLARE @v_risk_ds_1018 VARCHAR(10); 
  -- 是否与特定洗钱风险关联-身份为外国政要
  DECLARE @v_risk_ds_1019001 VARCHAR(10); 
  -- 是否与特定洗钱风险关联汇总分数
  DECLARE @v_risk_ds_1019 VARCHAR(10); 
  -- 行业现金密集程度-个人职业
  DECLARE @v_risk_ds_1020001 VARCHAR(10); 
  -- 行业现金密集程度汇总分数
  DECLARE @v_risk_ds_1020 VARCHAR(10); 
  -- 行业或职业其他风险-无
  DECLARE @v_risk_ds_1021001 VARCHAR(10); 
  -- 行业或职业其他风险汇总分数
  DECLARE @v_risk_ds_1021 VARCHAR(10); 
  -- 配合提供信息的积极程度与态度等-无
  DECLARE @v_risk_ds_1022001 VARCHAR(10); 
  -- 配合提供信息的积极程度与态度等汇总分数
  DECLARE @v_risk_ds_1022 VARCHAR(10); 
  -- 自定义其他风险-无
  DECLARE @v_risk_ds_1023001 VARCHAR(10); 
  -- 自定义其他风险汇总分数
  DECLARE @v_risk_ds_1023 VARCHAR(10); 
  -- 附加项风险-客户、客户实际控制人或实际受益人被列入我国发布或承认的应实施反洗钱监控措施的名单；
  DECLARE @v_risk_ds_1999001 VARCHAR(10);
  -- 附加项风险-客户、客户实际控制人或实际受益人为外国政要或其亲属、关系密切人；
  DECLARE @v_risk_ds_1999002 VARCHAR(10); 
  -- 附加项风险-客户多次涉及可疑交易报告；
  DECLARE @v_risk_ds_1999003 VARCHAR(10); 
  -- 附加项风险-客户拒绝金融机构依法开展的客户尽职调查工作；
  DECLARE @v_risk_ds_1999004 VARCHAR(10); 
  -- 附加项风险汇总分数
  DECLARE @v_risk_ds_1999 VARCHAR(10); 
  --申请客户信息临时表
  DECLARE @aml_customer_personal_run TABLE
  (
     [auto_id]                      [VARCHAR](45) NOT NULL,
     [check_status]                 [VARCHAR](1) NULL,
     [customer_no]                  [VARCHAR](32) NULL,
     [customer_name]                [VARCHAR](512) NULL,
     [branch_code]                  [VARCHAR](32) NULL,
     [date_id]                      [DATETIME] NULL,
     [industry_code]                [VARCHAR](32) NULL,
     [register_area]                [VARCHAR](6) NULL,
     [establish_date]               [DATETIME] NULL,
     [nationality_code1]            [VARCHAR](3) NULL,
     [nationality_code2]            [VARCHAR](3) NULL,
     [phone_number1]                [VARCHAR](64) NULL,
     [phone_number2]                [VARCHAR](64) NULL,
     [contact_address]              [VARCHAR](512) NULL,
     [contact_area]                 [VARCHAR](6) NULL,
     [management_nationality_code1] [VARCHAR](3) NULL,
     [management_area1]             [VARCHAR](6) NULL,
     [management_address1]          [VARCHAR](512) NULL,
     [management_nationality_code2] [VARCHAR](3) NULL,
     [management_area2]             [VARCHAR](6) NULL,
     [management_address2]          [VARCHAR](512) NULL,
     [cell_phone1]                  [VARCHAR](512) NULL,
     [cell_phone2]                  [VARCHAR](512) NULL,
     [crtft_type]                   [VARCHAR](6) NULL,
     [crtft_number]                 [VARCHAR](128) NULL,
     [crtft_due_date]               [DATETIME] NULL,
     [other_crtft_type]             [VARCHAR](128) NULL,
     [open_date]                    [DATETIME] NULL,
     [business_rela_type]           [VARCHAR](2) NULL,
     [register_inst_distance]       [VARCHAR](2) NULL,
     [management_inst_distance]     [VARCHAR](2) NULL,
     [customer_gender]              [VARCHAR](2) NULL,
     [cust_cooperate_flag]          [VARCHAR](2) NULL,
     [cover_flag]                   [VARCHAR](2) NULL,
     [update_date]                  [DATETIME] NULL,
     [risk_flag]                    [VARCHAR](1) NULL,
     [data_flag]                    [VARCHAR](1) NULL
  );

    select @v_dt_date = convert(date,@dt_date);
	-- 获取当前使用的风险等级
	select @v_risk_grade = (select constDetailNum  from aml_const_detail where constNum = 'sys_056' and constDetailName = '1');
	
	-- 更新客户信息中间表
	insert into @aml_customer_personal_run(auto_id,check_status,customer_no,customer_name,branch_code,date_id,industry_code,register_area,establish_date,nationality_code1,nationality_code2,phone_number1,phone_number2,contact_address,contact_area,management_nationality_code1,management_area1,management_address1,management_nationality_code2,management_area2,management_address2,cell_phone1,cell_phone2,crtft_type,crtft_number,crtft_due_date,other_crtft_type,open_date,business_rela_type,register_inst_distance,management_inst_distance,customer_gender,cust_cooperate_flag,cover_flag,update_date,risk_flag,data_flag) 
	select auto_id,check_status,customer_no,customer_name,branch_code,date_id,industry_code,register_area,establish_date,nationality_code1,nationality_code2,phone_number1,phone_number2,contact_address,contact_area,management_nationality_code1,management_area1,management_address1,management_nationality_code2,management_area2,management_address2,cell_phone1,cell_phone2,crtft_type,crtft_number,crtft_due_date,other_crtft_type,open_date,business_rela_type,register_inst_distance,management_inst_distance,customer_gender,cust_cooperate_flag,cover_flag,update_date,risk_flag,data_flag from aml_customer_personal where date_id = @v_dt_date;
	
	
	
	begin
	
	
    -- 检查游标是否未被正常关闭
    declare @cur_risk_customer_isopen int;
    select @cur_risk_customer_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_risk_customer_');
    -- 如果没有关闭则进行关闭
    if @cur_risk_customer_isopen > 0 
		begin
		  close cur_risk_customer_;
		  deallocate cur_risk_customer_;
		end  
	  
	  declare cur_risk_customer_ cursor for
	  select distinct customer_no from aml_risk_cust where customer_type = '1' order by customer_no;
      
		-- 开启游标
		open cur_risk_customer_;
		
				--移动游标 循环客户编号
				fetch next from cur_risk_customer_ into @v_customer_no;
				--提取成功 进行下一条数据的提取操作
				while @@FETCH_STATUS = 0  
				begin
								-- 检索获取评级编码
								set @v_grade_code = convert(varchar,replace(replace(replace(replace(convert(varchar,getdate(),120),' ',''),'-',''),'.',''),':',''))+convert(varchar,@v_customer_no);
			  
			  
								-- 检查游标是否未被正常关闭
								declare @cur_cust_risk_isopen int;
								select @cur_cust_risk_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_cust_risk_');
								-- 如果没有关闭则进行关闭
								if @cur_cust_risk_isopen > 0 
							    begin
								  close cur_cust_risk_;
								  deallocate cur_cust_risk_;
								end 
								-- 加载所有评级大项
								declare cur_cust_risk_ cursor for
								select constDetailNum,constDetailName from aml_const_detail where constNum = 'sys_050' order by constDetailNum;
									
								-- 开启游标
								open cur_cust_risk_;
								
										delete from aml_grade_detail where grade_code = @v_grade_code;
										--移动游标 循环获取大项编号
										fetch next from cur_cust_risk_ into @v_risk_section_code,@v_risk_section_name;
										--提取成功，进行下一条数据的提取操作
										while @@FETCH_STATUS = 0  
										 
										begin
											-- 循环插入客户评分
											insert into aml_grade_detail
											select newid(),
											@v_grade_code grade_code,
											customer_no,
											customer_name,
											getdate(),
											@v_risk_section_code,
											@v_risk_section_name,
											'2',
											'2',
											'1', -- 评分规则来源 规则评分
											'1', -- 评分数据来源 系统评分
											'',
											'' from aml_customer_personal where date_id = @v_dt_date and customer_no = @v_customer_no;
										
										fetch next from cur_cust_risk_ into @v_risk_section_code,@v_risk_section_name;
										
										end;
										
								-- 关闭游标	   
			                    close cur_cust_risk_; 
			                    deallocate cur_cust_risk_;
				 
	    
			
	    -- 输出评级总分表
	    update aml_grade set grade_state = '2' where customer_no = @v_customer_no and grade_state = '0';
		
		insert into aml_grade
		select 
		newid() auto_id,
		'0' audit_state,
		@v_grade_code grade_code,
		@v_dt_date date_id,
		'1' customer_type,
		acps.customer_no customer_no,
		acps.customer_name customer_name,
		agd.grade_score grade_score,
		ad3.constDetailNum credit_rate_code3,
		ad4.constDetailNum credit_rate_code4,
		ad5.constDetailNum credit_rate_code5,
		'1' grade_number,
		getdate() grade_date,
		'0' grade_state,
		convert(varchar,getdate()+convert(int,ad3limit.constDetailName),111) risk_due_date3,
		convert(varchar,getdate()+convert(int,ad4limit.constDetailName),111) risk_due_date4,
		convert(varchar,getdate()+convert(int,ad5limit.constDetailName),111) risk_due_date5,
		'' junior_help_user,
		'' repeat_help_user,
		'' cutout_help_user,
		'C1' branch_code
		from 
		aml_customer_personal acps 
		left join (select sum(new_grade_score) grade_score,max(customer_no) customer_no,grade_code from aml_grade_detail where grade_code = @v_grade_code group by grade_code) agd on agd.customer_no = acps.customer_no
		left join aml_const_detail ad3 on ad3.constNum = 'sys_057' and agd.grade_score between substring(ad3.constDetailName,1,charindex('-',ad3.constDetailName)-1) and substring(ad3.constDetailName,charindex('-',ad3.constDetailName)+1,LEN(ad3.constDetailName)-charindex('-',ad3.constDetailName)) and ad3.constDetailNum not like '%limit%'
		left join aml_const_detail ad3limit on ad3limit.constNum = 'sys_057' and ad3limit.constDetailNum like '%limit%' and ad3.constDetailNum = substring(ad3limit.constDetailNum,1,charindex('_',ad3limit.constDetailNum)-1)
		
		left join aml_const_detail ad4 on ad4.constNum = 'sys_058' and agd.grade_score between substring(ad4.constDetailName,1,charindex('-',ad4.constDetailName)-1) and substring(ad4.constDetailName,charindex('-',ad4.constDetailName)+1,LEN(ad4.constDetailName)-charindex('-',ad4.constDetailName)) and ad4.constDetailNum not like '%limit%'
		left join aml_const_detail ad4limit on ad4limit.constNum = 'sys_058' and ad4limit.constDetailNum like '%limit%' and ad4.constDetailNum = substring(ad4limit.constDetailNum,1,charindex('_',ad4limit.constDetailNum)-1)
		
		left join aml_const_detail ad5 on ad5.constNum = 'sys_059' and agd.grade_score between substring(ad5.constDetailName,1,charindex('-',ad5.constDetailName)-1) and substring(ad5.constDetailName,charindex('-',ad5.constDetailName)+1,LEN(ad5.constDetailName)-charindex('-',ad5.constDetailName)) and ad5.constDetailNum not like '%limit%'
		left join aml_const_detail ad5limit on ad5limit.constNum = 'sys_059' and ad5limit.constDetailNum like '%limit%' and ad5.constDetailNum = substring(ad5limit.constDetailNum,1,charindex('_',ad5limit.constDetailNum)-1)
		where acps.date_id = @v_dt_date and acps.customer_no = @v_customer_no;
	 
	  -- 移动游标到下一条记录
	  fetch next from cur_risk_customer_ into @v_customer_no;  
	  
	  end;
	                     
    end;
			
	  -- 关闭游标
      close cur_risk_customer_;
	  deallocate cur_risk_customer_;
	  
   end
