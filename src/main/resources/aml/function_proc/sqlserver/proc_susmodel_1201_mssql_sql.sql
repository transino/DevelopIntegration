create procedure [dbo].[proc_susmodel_1201]
@dt_date date
as
begin
   /*
  可疑模型代码：1201
  可疑模型描述：黑名单历史交易回溯,证件类型+证件号码匹配客户、客户法人、客户股东、客户受益人、交易对手、交易代办人件类型+证件号码；黑名单名称匹配客户、客户法人、客户股东、客户受益人、交易对手、交易代办人名称
  可疑模型参数引用：
  1.dt_date:数据日期
  */ 
	
	declare @v_sql varchar(8); -- 可疑事件识别码
	declare @v_suspcs_code varchar(8); -- 可疑事件识别码
 	declare @v_trans_code  varchar(32); -- 可疑交易编号
 	declare @v_dt_date     varchar(20); -- 数据日期
 	declare @v_tosc        varchar(4000); -- 涉嫌犯罪类型
 	declare @v_stcr        varchar(4000); -- 可疑特征码
	declare @v_stcb        varchar(4000); -- 资金交易及客户行为情况
	declare @v_aosp        varchar(4000); -- 疑点分析
	declare @null_flag        varchar(10); -- 空标识
	
	declare @black_recall_day   varchar(10); -- 回溯年数
	
	select @black_recall_day = paramValue from aml_modelparam where paramCode = 'black_recall_day';
	
	
    declare @aml_black_trans table
    (
    trans_code  varchar(200),
    v_stcr      varchar(8),
    v_tosc      varchar(2000),
    v_stcb      varchar(2000),
    v_aosp      varchar(2000)
    );
      
	       
	begin
	
		select @v_dt_date = cast(@dt_date as date);
		-- 加载基础数据到中间表
		truncate table aml_transaction_run
		
		insert into aml_transaction_run
		select *
		  from aml_transaction
		 where suspcs_flag = '1'
		   and date_id between DATEADD(YEAR,-CONVERT(decimal(20,2),@black_recall_day),@v_dt_date) and @v_dt_date;
		   
		   
		-- 清空黑名单检测中间表
		delete from @aml_black_trans;
		
		
	    -- 对公/对私客户证件
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80021' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '客户证件涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_public_run acpg
          on atr.customer_no = acpg.customer_no
         and acpg.date_id = @v_dt_date
        left join aml_customer_personal_run acps
          on atr.customer_no = acps.customer_no
         and acps.date_id = @v_dt_date
        left join Aml_Blacklist ab1
          on acpg.crtft_type + acpg.crtft_number =
             ab1.id_type + ab1.id_num
         and ab1.status = '1' and ab1.back_flag = '0'
        left join Aml_Blacklist ab2
          on acps.crtft_type + acps.crtft_number =
             ab2.id_type + ab2.id_num
         and ab2.status = '1' and ab2.back_flag = '0'
        left join aml_stcr ams on ams.featureCode = '80021'
       where atr.SUSPCS_FLAG = '1'
         and (ab1.id_type + ab1.id_num is not null or
             ab2.id_type + ab2.id_num is not null)
			   and 1 = (select enable from aml_stcr where featurecode = '80021')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80021%';
	  
	  
	    -- 对公客户法人证件
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80022' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '对公客户法人证件涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_public_run acpg
          on atr.customer_no = acpg.customer_no
         and acpg.date_id = @v_dt_date
        left join aml_stcr ams on ams.featureCode = '80022'
       inner join Aml_Blacklist ab1
          on acpg.legal_crtft_type +  acpg.legal_crtft_number  =
             ab1.id_type +  ab1.id_num 
         and ab1.status = '1' and ab1.back_flag = '0'
       where atr.SUSPCS_FLAG = '1'
       and 1 = (select enable from aml_stcr where featurecode = '80022')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80022%';
	  
	  
	    -- 对公客户股东或实际控制人证件
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80023' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '对公客户股东或实际控制人证件涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_public_run acpg
          on atr.customer_no = acpg.customer_no
         and acpg.date_id = @v_dt_date
        left join aml_stcr ams on ams.featureCode = '80023'
        inner join Aml_Blacklist ab1
          on acpg.controller_crtft_type + acpg.controller_crtft_number  =
             ab1.id_type +  ab1.id_num 
         and ab1.status = '1' and ab1.back_flag = '0'
        where atr.SUSPCS_FLAG = '1'
        and 1 = (select enable from aml_stcr where featurecode = '80023')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80023%';
	  
	  
	    -- 对公客户受益人证件
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80024' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '对公客户受益人证件涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_beneficiary_run acb
          on atr.customer_no = acb.customer_no
         and acb.date_id = @v_dt_date
        left join aml_stcr ams on ams.featureCode = '80024'
        inner join Aml_Blacklist ab1
          on acb.profit_crtft_type + acb.profit_crtft_no  =
             ab1.id_type + ab1.id_num 
         and ab1.status = '1' and ab1.back_flag = '0'
        where atr.SUSPCS_FLAG = '1'
        and 1 = (select enable from aml_stcr where featurecode = '80024')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80024%';
        
	  
	    -- 交易对手证件
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80025' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '交易对手证件涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
       inner join Aml_Blacklist ab1
          on atr.other_customer_crtft_type + atr.other_customer_crtft_number  = ab1.id_type + ab1.id_num
         and ab1.status = '1' and ab1.back_flag = '0'
        left join aml_stcr ams on ams.featureCode = '80025'
       where atr.SUSPCS_FLAG = '1'
      and 1 = (select enable from aml_stcr where featurecode = '80025')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80025%';
        
	  
	  	-- 交易代办人证件
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80026' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '交易代办人证件涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
       inner join Aml_Blacklist ab1
          on atr.agent_user_crtft_type + atr.agent_user_crtft_number  = ab1.id_type + ab1.id_num
         and ab1.status = '1' and ab1.back_flag = '0'
        left join aml_stcr ams on ams.featureCode = '80026'
       where atr.SUSPCS_FLAG = '1'
      and 1 = (select enable from aml_stcr where featurecode = '80026')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80026%';
		
	    -- 对公/对私客户姓名
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80027' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '客户姓名涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_public_run acpg
          on atr.customer_no = acpg.customer_no
         and acpg.date_id = @v_dt_date
        left join aml_customer_personal_run acps
          on atr.customer_no = acps.customer_no
         and acps.date_id = @v_dt_date
        left join Aml_Blacklist ab1
          on acpg.customer_name = ab1.customer_name
         and ab1.status = '1' and ab1.back_flag = '0'
        left join Aml_Blacklist ab2
          on acps.customer_name = ab2.customer_name
         and ab2.status = '1' and ab2.back_flag = '0'
        left join aml_stcr ams on ams.featureCode = '80027'
       where atr.SUSPCS_FLAG = '1'
         and (ab1.customer_name is not null or
             ab2.customer_name is not null)
			   and 1 = (select enable from aml_stcr where featurecode = '80027')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80027%';
	  
	  
	  
	    -- 对公客户法人名称
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80028' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '对公客户法人名称涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_public_run acpg
          on atr.customer_no = acpg.customer_no
         and acpg.date_id = @v_dt_date
        left join aml_stcr ams on ams.featureCode = '80028'
       inner join Aml_Blacklist ab1
          on acpg.legal_name = ab1.customer_name
         and ab1.status = '1' and ab1.back_flag = '0'
       where atr.SUSPCS_FLAG = '1'
       and 1 = (select enable from aml_stcr where featurecode = '80028')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80028%';
	  
	  
	    -- 对公客户股东或实际控制人名称
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80029' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '对公客户股东或实际控制人名称涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_public_run acpg
          on atr.customer_no = acpg.customer_no
         and acpg.date_id = @v_dt_date
        left join aml_stcr ams on ams.featureCode = '80029'
        inner join Aml_Blacklist ab1
          on acpg.controller_name = ab1.customer_name
         and ab1.status = '1' and ab1.back_flag = '0'
        where atr.SUSPCS_FLAG = '1'
        and 1 = (select enable from aml_stcr where featurecode = '80029')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80029%';
	  
	  
	    -- 对公客户受益人证件
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80030' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '对公客户受益人名称涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
        left join aml_customer_beneficiary_run acb
          on atr.customer_no = acb.customer_no
         and acb.date_id = @v_dt_date
        left join aml_stcr ams on ams.featureCode = '80030'
        inner join Aml_Blacklist ab1
          on acb.profit_name  = ab1.customer_name
         and ab1.status = '1' and ab1.back_flag = '0'
        where atr.SUSPCS_FLAG = '1'
        and 1 = (select enable from aml_stcr where featurecode = '80030')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80030%';
        
	  
	    -- 交易对手名称
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80031' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '交易对手名称涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
       inner join Aml_Blacklist ab1
          on other_customer_name  = ab1.customer_name
         and ab1.status = '1' and ab1.back_flag = '0'
        left join aml_stcr ams on ams.featureCode = '80031'
       where atr.SUSPCS_FLAG = '1'
      and 1 = (select enable from aml_stcr where featurecode = '80031')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80031%';
        
	  
	  	-- 交易代办人名称
		insert into @aml_black_trans
        select atr.trans_code trans_code, -- 交易流水编号
        '80032' v_stcr, --可疑特征码
        ams.TOSCs v_tosc, --犯罪类型
        '交易金额'+convert(varchar,atr.trans_amount_cny)+'元' v_stcb, --资金交易及客户行为情况
        '交易代办人名称涉嫌黑名单记录' v_aosp --疑点分析
        from aml_transaction_run atr
       inner join Aml_Blacklist ab1
          on atr.agent_user_name = ab1.customer_name
         and ab1.status = '1' and ab1.back_flag = '0'
        left join aml_stcr ams on ams.featureCode = '80032'
       where atr.SUSPCS_FLAG = '1'
      and 1 = (select enable from aml_stcr where featurecode = '80032')
       and (select featureCodes from aml_susmodel where susModelCode = '1201') like '%80032%';
	  
	
		-- 检索获取可疑模型识别码
		select @v_suspcs_code = case when count(1) = 0 then 
		(select dbo.func_suspcs_code(max(suspcs_cdoe))
		from aml_suspcs_case
		where date_id = @v_dt_date and suspcs_model_code <> '1201')
		else max(suspcs_cdoe) end
		from aml_suspcs_case
		where date_id = @v_dt_date and suspcs_model_code = '1201';
        
    -- 检查游标是否未被正常关闭
    declare @cur_sus_120101_isopen int;
    select @cur_sus_120101_isopen = (select count(1) from sys.dm_exec_cursors(0) where is_open = 1 and name = 'cur_sus_120101_');
    -- 如果没有关闭则进行关闭
    if @cur_sus_120101_isopen > 0 
		begin
		  close cur_sus_120101_;
		  deallocate cur_sus_120101_;
		end  
	  
	  declare cur_sus_120101_ cursor for
	  SELECT B.trans_code,
		dbo.func_dist_str(LEFT(t1,LEN(t1)-1),',') as v_stcr,
		dbo.func_dist_str(LEFT(t2,LEN(t2)-1),',') as v_tosc,
		dbo.func_dist_str(LEFT(t3,LEN(t3)-1),',') as v_stcb,
		dbo.func_dist_str(LEFT(t4,LEN(t4)-1),',') as v_aosp FROM (
		SELECT trans_code,
		(SELECT v_stcr+',' FROM @aml_black_trans 
		  WHERE trans_code=A.trans_code 
		  FOR XML PATH('')) AS t1,
		  (SELECT v_tosc+',' FROM @aml_black_trans 
		  WHERE trans_code=A.trans_code 
		  FOR XML PATH('')) AS t2,
		  (SELECT v_stcb+',' FROM @aml_black_trans 
		  WHERE trans_code=A.trans_code 
		  FOR XML PATH('')) AS t3,
		  (SELECT v_aosp+',' FROM @aml_black_trans 
		  WHERE trans_code=A.trans_code 
		  FOR XML PATH('')) AS t4
		FROM @aml_black_trans A GROUP BY trans_code) B order by B.trans_code;
        
        
        
		-- 开启游标
		open cur_sus_120101_;
		
				--移动游标 循环黑名单交易信息
				fetch next from cur_sus_120101_ into @v_trans_code,@v_stcr,@v_tosc,@v_stcb,@v_aosp;
				
				-- 插入前删除事件交易明细记录
		        delete FROM aml_suspcs_case_detail where date_id = @v_dt_date and suspcs_model_code = '1201';
				--提取成功 进行下一条数据的提取操作
				while @@FETCH_STATUS = 0  
				begin
						
							 -- 循环插入最新事件交易明细记录
							 insert into aml_suspcs_case_detail
							(AUTO_ID,
							 DATA_FROM,
							 DELETE_STATE,
							 data_state,
							 CHECK_STATE,
							 AUDIT_OPINION,
							 AUDIT_STATE,
							 AUDIT_TYPE_STATE,
							 suspcs_cdoe,
							 suspcs_model_code,
							 DATE_ID,
							 TRANS_CODE,
							 BRANCH_CODE,
							 CUSTOMER_TYPE,
							 CUSTOMER_NO,
							 CUSTOMER_NAME,
							 ACCT_CODE,
							 TRANS_AMOUNT,
							 TRANS_USE,
							 CURRENCY,
							 PAYMENT_FLAG,
							 TRANS_TYPE,
							 TRANS_DATE,
							 TRANS_nationality,
							 TRANS_address,
							 TOSC,
							 STCR,
							 STCB,
							 AOSP,
							 SEVC,
							 SENM,
							 SETP,
							 OITP,
							 SEID,
							 STNT,
							 SCIF,
							 SRNM,
							 SRIT,
							 ORIT,
							 SRID,
							 SCNM,
							 SCIT,
							 OCIT,
							 SCID,
							 SCBA,
							 SCBN,
							 OTHER_INST_LATTICE_NAME,
							 OTHER_INST_LATTICE_TYPE,
							 OTHER_INST_LATTICE_CODE,
							 OTHER_INST_LATTICE_AREA,
							 OTHER_CUSTOMER_NAME,
							 OTHER_CUSTOMER_CRTFT_TYPE,
							 OTHER_CUSTOMER_CRTFT_NUMBER,
							 OTHER_CUSTOMER_ACCT_TYPE,
							 OTHER_CUSTOMER_ACCT_CODE,
							 AGENT_USER_NAME,
							 AGENT_USER_CRTFT_TYPE,
							 AGENT_USER_CRTFT_NUMBER,
							 AGENT_USER_NATIONALITY)
						 select newid() AUTO_ID,t.* from (
							select distinct 
								   '0' DATA_FROM,
								   '0' DELETE_STATE,
								   '4' data_state,
								   '0' CHECK_STATE, -- 校验状态 0 未校验；1 校验失败；2 校验部分成功；3 校验成功
								   '' AUDIT_OPINION, -- 审核意见
								   '0' AUDIT_STATE, -- 审核状态 2 初审驳回；0  未审核；4 终审通过；6  复审通过；1  复审驳回；3  初审通过；5  终审驳回
								   '1' AUDIT_TYPE_STATE, -- 报送标识 1 上报；2 不上报
								   @v_suspcs_code SUSPCS_CODE, -- 可疑事件识别码
								   '1201' suspcs_model_code, -- 可疑模型编码
								   @v_dt_date DATE_ID, -- 数据日期
								   atr.trans_code TRANS_CODE, -- 交易流水号
								   atr.branch_code BRANCH_CODE, -- 机构号
								   atr.customer_type CUSTOMER_TYPE, -- 客户类型 1 对私；2 对公
								   atr.customer_no CUSTOMER_NO, -- 客户编号
								   atr.customer_name CUSTOMER_NAME, -- 客户姓名
								   atr.acct_code ACCT_CODE, -- 账号
								   atr.trans_amount TRANS_AMOUNT, -- 交易金额
								   atr.trans_use TRANS_USE, -- 资金用途
								   atr.currency CURRENCY, -- 客户国籍
								   atr.payment_flag PAYMENT_FLAG, -- 资金收付标识 01 收；02 付
								   atr.trans_type TRANS_TYPE, -- 交易方式
								   atr.trans_date TRANS_DATE, -- 交易日期
								   atr.trans_nationality trans_nationality, -- 交发生地国籍
								   atr.trans_address trans_address, -- 交发生地代码
								   @v_tosc TOSC, -- 可疑犯罪类型
								   @v_stcr STCR, -- 可疑特征码
								   @v_stcb STCB, -- 客户资金行为情况
								   @v_aosp AOSP, -- 疑点分析
								   case atr.customer_type WHEN
														   '1' then
														   acps.industry_code WHEN
														   '2' then
														   acpg.industry_code END SEVC, -- 可疑主体职业（对私）或行业（对公）
								   case atr.customer_type WHEN
														   '1' then
														   acps.customer_name WHEN
														   '2' then
														   acpg.customer_name END SENM, -- 可疑主体姓名/名称
									case atr.customer_type WHEN
														   '1' then
														   acps.crtft_type WHEN
														   '2' then
														   acpg.crtft_type  END SETP, -- 可疑主体身份证件/证明文件类型
								   case atr.customer_type WHEN
														   '1' then
														   acps.other_crtft_type WHEN
														   '2' then
														   acpg.other_crtft_type END OITP, -- 其他身份证件/证明文件类型
								   case atr.customer_type WHEN
														   '1' then
														   acps.crtft_number WHEN
														   '2' then
														   acpg.crtft_number END SEID, -- 可疑主体身份证件/证明文件号码
										  case atr.customer_type WHEN
														   '1' then
														   (case when acps.nationality_code2 is null or acps.nationality_code2 = '' then
															  acps.nationality_code1
															 else
															  convert(varchar,acps.nationality_code1+','+acps.nationality_code2)
														   end)  WHEN
														   '2' then
														   (case when acpg.nationality_code2  
																									 is null or acpg.nationality_code2 = '' then
															  acpg.nationality_code1
														   else
															  convert(varchar,acpg.nationality_code1+','+acpg.nationality_code2)
														   end) end STNT, -- 可疑主体国籍
														   case  atr.customer_type when
														   '1' then
														   (case when acps.phone_number2 
															  is null or acps.phone_number2  = '' then
															  acps.phone_number1
															 else
															  convert(varchar,acps.phone_number1 + ',' + acps.phone_number2)
														   end) WHEN
														   '2' then
														   (case when acpg.phone_number2 
															  is null or acpg.phone_number2  = '' then
															  acpg.phone_number1
															 else
															  convert(varchar,acpg.phone_number1 + ',' +acpg.phone_number2)
														   end)
																								end SCIF, -- 可疑主体联系方式
								   acpg.legal_name SRNM, -- 可疑主体法定代表人姓名
								   acpg.legal_crtft_type SRIT, -- 可疑主体法定代表人证件类型
								   acpg.legal_other_crtft_type ORIT, -- 法定代表人其他证件类型
								   acpg.legal_crtft_number SRID, -- 可疑主体法定代表人身份证件号码
								   acpg.controller_name SCNM, -- 可疑主体控股股东或实际控制人名称
								   acpg.controller_crtft_type SCIT, -- 可疑主体控股股东或实际控制人身份证件/证明文件类型
								   acpg.controller_oher_crtft_type OCIT, -- 可疑主体控股股东或实际控制人身份证件/证明文件类型
								   acpg.controller_crtft_number SCID, -- 可疑主体控股股东或实际控制人身份证件/证明文件号码
								   atr.bank_card_code SCBA, -- 可疑主体所在银行账号
								   atr.bank_card_name SCBN, -- 可疑主体所在银行名称
								   atr.other_inst_lattice_name OTHER_INST_LATTICE_NAME,
								   atr.other_inst_lattice_type OTHER_INST_LATTICE_TYPE,
								   atr.other_inst_lattice_code OTHER_INST_LATTICE_CODE,
								   atr.other_inst_lattice_area OTHER_INST_LATTICE_AREA,
								   atr.other_customer_name OTHER_CUSTOMER_NAME,
								   atr.other_customer_crtft_type OTHER_CUSTOMER_CRTFT_TYPE,
								   atr.other_customer_crtft_number OTHER_CUSTOMER_CRTFT_NUMBER,
								   atr.other_customer_acct_type OTHER_CUSTOMER_ACCT_TYPE,
								   atr.other_customer_acct_code OTHER_CUSTOMER_ACCT_CODE,
								   atr.agent_user_name AGENT_USER_NAME,
								   atr.agent_user_crtft_type AGENT_USER_CRTFT_TYPE,
								   atr.agent_user_crtft_number AGENT_USER_CRTFT_NUMBER,
								   atr.agent_user_nationality AGENT_USER_NATIONALITY
							  from aml_transaction_run atr
							 left join aml_customer_personal_run acps on acps.customer_no = atr.customer_no and acps.date_id = @v_dt_date
							 left join aml_customer_public_run acpg
							  on atr.customer_no = acpg.customer_no
							 and acpg.date_id = @v_dt_date
							 where atr.trans_code = @v_trans_code
							 ) t; 
				
				fetch next from cur_sus_120101_ into @v_trans_code,@v_stcr,@v_tosc,@v_stcb,@v_aosp;
				
				end;
        
         close cur_sus_120101_; 
		 deallocate cur_sus_120101_;
		 
     	-- 汇总可疑特征码
     	select @v_stcr = dbo.func_dist_str(stuff((select ','+ stcr from (select distinct stcr from aml_suspcs_case_detail where date_id = @v_dt_date and suspcs_model_code = '1201') t for xml path('')),1,1,''),',');
		-- 汇总涉嫌犯罪类型
		select @v_tosc = dbo.func_dist_str(stuff((select ','+ tosc from (select distinct tosc from aml_suspcs_case_detail where date_id = @v_dt_date and suspcs_model_code = '1201') t for xml path('')),1,1,''),',');
		-- 汇总资金交易行为情况
		select @v_stcb = '交易总笔数'+convert(varchar,COUNT(1))+'笔,交易金额' + convert(varchar,sum(cast(trans_amount as decimal(20,2)))) + '元' from aml_suspcs_case_detail where date_id = @v_dt_date and suspcs_model_code = '1201';
		-- 汇总疑点分析
		select @v_aosp = dbo.func_dist_str(stuff((select ','+ aosp from (select distinct aosp from aml_suspcs_case_detail where date_id = @v_dt_date and suspcs_model_code = '1201') t for xml path('')),1,1,''),',');	
	     
	     
		-- 输出可疑事件表
		delete from aml_suspcs_case where date_id = @v_dt_date and suspcs_model_code = '1201';
		select @null_flag = count(1)  from aml_suspcs_case_detail where date_id = @v_dt_date and suspcs_model_code = '1201';
		if @null_flag > 0 
		begin
		insert into aml_suspcs_case
		(check_state,
		 auto_id,
		 data_from,
		 delete_state,
		 show_logic1,
		 show_logic2,
		 show_logic3,
		 show_logic4,
		 show_logic5,
		 show_logic6,
		 show_logic7,
		 show_logic8,
		 report_zpi_name,
		 report_xml_name,
		 receipt_state,
		 data_state,
		 produce_state,
		 date_id,
		 suspcs_cdoe,
		 suspcs_model_code,
		 RICD,
		 RPNC,
		 RITP,
		 DETR,
		 TORP,
		 ORXN,
		 TOSC,
		 STCR,
		 ODRP,
		 DORP,
		 TPTR,
		 OTPR,
		 STCB,
		 AOSP,
		 SBDT,
		 SEDT,
		 ROTF1,
		 ROTF2)
		select '0' check_state, -- 0 未校验;1 校验失败;2 校验部分成功;3 校验成功;
			   newid() auto_id,
			   '0' data_from, -- 数据来源 0 系统生成；2 手工补录;
			   '0' delete_state, -- 删除状态
			   '1' show_logic1,
			   '0' show_logic2,
			   '0' show_logic3,
			   '0' show_logic4,
			   '0' show_logic5,
			   '0' show_logic6,
			   '0' show_logic7,
			   '0' show_logic8,
			   '' report_zpi_name,
			   '' report_xml_name,
			   '3' receipt_state, -- 回执错误（0），回执补正（1），回执警告（2），未回执（3），回执成功（4）
			   '4' data_state, -- 补正（0），纠错（1），删除（2），警告（3），新增（4）
			   '0' produce_state, -- 0：未生成 1：已生成
			   @v_dt_date date_id, -- 数据日期
			   @v_suspcs_code suspcs_cdoe, -- 可疑事件识别码
			   '1201' suspcs_model_code, -- 可疑模型编码
			   as1.RICD RICD, -- 报告机构编码
			   as1.RPNC RPNC, -- 上报网点代码
			   as1.RITP RITP, -- 行业类别
			   as1.DETR DETR, -- 通用可疑交易报告紧急程度
			   '1' TORP, -- 报送次数标志
			   '' ORXN, -- 初次报送的通用可疑交易报告报文名称
			   @v_tosc TOSC, -- 可疑主体犯罪类型
			   @v_stcr STCR, -- 可疑交易特征代码
			   as1.ODRP ODRP,
			   as1.DORP DORP,
			   as1.TPTR TPTR,
			   as1.OTPR OTPR,
			   @v_stcb STCB,
			   @v_aosp AOSP,
			   ascd1.min_trans_date SBDT,
			   ascd1.max_trans_date SEDT,
			   '' ROTF1,
			   '' ROTF2
		  from (select * from aml_susmodel where susModelCode = '1201') as1,
			   (select min(convert(varchar,trans_date,112)) min_trans_date,
					   max(convert(varchar,trans_date,112)) max_trans_date
				  from aml_suspcs_case_detail
				 where date_id = @v_dt_date
				   and suspcs_model_code = '1201') ascd1; 

      end;
      -- 更新回溯标识状态
      update Aml_Blacklist set back_flag = '1' where back_flag = '0' and status = '1';
    end;
	
end;