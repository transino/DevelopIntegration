CREATE DEFINER=`aml`@`%` PROCEDURE `proc_susmodel_1103`(in dt_date varchar(20))
BEGIN
   /*
  可疑模型代码：1103
  可疑模型描述：长期闲置账户近期大量资金频繁转入转出
  可疑模型参数引用：
	1、trans_day:近期天数
	2、long_idle_day：长期闲置交易天数
	3、long_idle_money：长期闲置交易金额
	4、long_idle_number：长期闲置交易笔数
	5、large_money_dg_cny：对公大额(人民币)
	6、large_money_ds_cny：个人大额(人民币)
	7、often_number： 短期频繁交易次数
  */  
	declare trans_day varchar(8); -- 近期天数
	declare long_idle_day varchar(8); -- 长期闲置交易天数
	declare long_idle_dg_cny NUMERIC(20,2); -- 对公长期闲置交易金额(人民币)
	declare long_idle_ds_cny NUMERIC(20,2); -- 个人长期闲置交易金额(人民币)
	declare long_idle_number varchar(8); -- 长期闲置交易笔数
	declare large_money_dg_cny NUMERIC(20,2); -- 对公大额(人民币)
	declare large_money_ds_cny NUMERIC(20,2); -- 个人大额(人民币)
	declare often_number varchar(8); -- 短期频繁交易次数
	declare v_acct_code varchar(20); -- 账号
	declare v_currency varchar(3); -- 币种
	declare v_customer_type varchar(20); -- 客户类型
	declare v_stcr varchar(10000); -- 可疑特征码
	declare v_tosc varchar(10000); -- 涉嫌犯罪类型
	declare v_stcb varchar(10000); -- 资金交易行为情况
	declare v_aosp varchar(10000); -- 疑点分析
	declare v_dt_date varchar(20); -- 数据日期
	declare v_suspcs_code varchar(8); -- 可疑事件识别码
 	declare v_trans_code  varchar(32); -- 可疑交易编号
	declare null_flag        varchar(10); -- 空标识
	declare ret_cur_suspcs_black int DEFAULT 0;
	
		
		select paramValue into trans_day from aml_modelparam where paramCode = 'trans_day';
		select paramValue into often_number from aml_modelparam where paramCode = 'often_number';
		select paramValue into large_money_dg_cny from aml_modelparam where paramCode = 'large_money_dg_cny';
		select paramValue into large_money_ds_cny from aml_modelparam where paramCode = 'large_money_ds_cny';
		select paramValue into long_idle_day from aml_modelparam where paramCode = 'long_idle_day';
		select paramValue into long_idle_dg_cny from aml_modelparam where paramCode = 'long_idle_dg_cny';
		select paramValue into long_idle_ds_cny from aml_modelparam where paramCode = 'long_idle_ds_cny';
		select paramValue into long_idle_number from aml_modelparam where paramCode = 'long_idle_number';
		
		
		drop table if exists aml_reserve_trans;
	  create table aml_reserve_trans
    (
    acct_code  varchar(200),
    customer_type  varchar(200),
    v_stcr      varchar(2000), 
    v_tosc     varchar(2000),
    v_stcb     varchar(2000), -- 资金交易及客户行为情况
	  v_aosp     varchar(2000)  -- 疑点分析
    );
		
		drop table if exists aml_reserve_acct;
		create table aml_reserve_acct
    (
    acct_code  varchar(200),
    customer_type  varchar(8)
    );   
		
  select cast(dt_date as date) into v_dt_date;
	 -- 加载基础数据到中间表
	 set @v_sql = 'truncate table aml_transaction_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
  insert into aml_transaction_run
    select *
      from aml_transaction
     where suspcs_flag = '1'
       and date_id between date_sub(v_dt_date,interval long_idle_day+trans_day day) and v_dt_date;

   -- 检索获取可疑模型识别码
  select case when count(1) = 0 then 
	(select func_suspcs_code(max(suspcs_cdoe))
    from aml_suspcs_case
   where date_id = v_dt_date and suspcs_model_code <> '1103')
	 else suspcs_cdoe end
    into v_suspcs_code
    from aml_suspcs_case
   where date_id = v_dt_date and suspcs_model_code = '1103';
	 
    -- 提取短期内大额频繁交易账户
		insert into aml_reserve_acct
		select acct_code,customer_type from
		(
		select acct_code,customer_type,sum(trans_amount_cny) trans_amount,count(1) sum_number 
			 from aml_transaction_run atr 
			 where date_id between date_sub(v_dt_date,interval trans_day day) and v_dt_date
			 Group by acct_code,customer_type
			 ) t where (trans_amount >= large_money_dg_cny and sum_number >= often_number and customer_type = '2') or
			           (trans_amount >= large_money_ds_cny and sum_number >= often_number and customer_type = '1');
		

     -- 对公长期闲置交易账户短期频繁大量资金转入转出
		 insert into aml_reserve_trans
		 select acct_code,customer_type,
		 '80201' v_stcr,
		 ams.TOSCs v_tosc, -- 犯罪类型
		 concat(long_idle_day,'天内折人民币交易金额小于等于',long_idle_dg_cny,'元，且交易笔数小于等于',long_idle_number,'笔的对公账户，在近期',trans_day,'天内折人民币交易金额大于等于',large_money_dg_cny,'元且交易笔数大于等于',often_number,'笔') ,
		 '长期闲置账户近期频繁资金转入转出'
		  from 
		 (
   select ara.acct_code,ara.customer_type,case when sum(atr.trans_amount_cny) is null then '0' else sum(atr.trans_amount_cny) end trans_amount,count(atr.trans_code) sum_number 
		 from aml_transaction_run atr
		 right join aml_reserve_acct ara on ara.customer_type = '2' and ara.acct_code=atr.acct_code
		 and atr.date_id between date_sub(v_dt_date,interval long_idle_day+trans_day day) and date_sub(v_dt_date,interval trans_day day)
		 Group by ara.acct_code,ara.customer_type
		 ) t 
         left join aml_stcr ams on ams.featureCode = '80201'
		 where  t.trans_amount <= long_idle_dg_cny and t.sum_number <= long_idle_number and t.customer_type = '2' 
		 and 1 = (select enable from aml_stcr where featureCode = '80201')
       and (select featureCodes from aml_susmodel where susModelCode = '1103') like '%80201%';

     
		 -- 个人长期闲置交易账户短期频繁大量资金转入转出
		 insert into aml_reserve_trans
		 select acct_code,customer_type,
		 '80202' v_stcr,
		 ams.TOSCs v_tosc, -- 犯罪类型
		 concat(long_idle_day,'天内折人民币交易金额小于等于',long_idle_ds_cny,'元，且交易笔数小于等于',long_idle_number,'笔的个人账户，在近期',trans_day,'天内折人民币交易金额大于等于',large_money_ds_cny,'元且交易笔数大于等于',often_number,'笔') ,
		 '长期闲置账户近期频繁资金转入转出'
		  from 
		 (
   select ara.acct_code,ara.customer_type,case when sum(atr.trans_amount_cny) is null then '0' else sum(atr.trans_amount_cny) end trans_amount,count(atr.trans_code) sum_number 
		 from aml_transaction_run atr
		 right join aml_reserve_acct ara on ara.customer_type = '1' and ara.acct_code=atr.acct_code
		 and atr.date_id between date_sub(v_dt_date,interval long_idle_day+trans_day day) and date_sub(v_dt_date,interval trans_day day)
		 Group by ara.acct_code,ara.customer_type
		 ) t 
         left join aml_stcr ams on ams.featureCode = '80202'
		 where  t.trans_amount <= long_idle_ds_cny and t.sum_number <= long_idle_number and t.customer_type = '1' 
		 and 1 = (select enable from aml_stcr where featureCode = '80202')
       and (select featureCodes from aml_susmodel where susModelCode = '1103') like '%80202%';
		 
		BEGIN
    -- 创建游标
		declare cur_sus_trans_ cursor for
		select * FROM aml_reserve_trans A where (acct_code is not null or acct_code <> '') order by acct_code;	 
        
		declare continue handler for not FOUND set ret_cur_suspcs_black = 1;
		OPEN cur_sus_trans_;
		-- 插入前删除事件交易明细记录
		delete FROM aml_suspcs_case_detail where date_id = v_dt_date and suspcs_model_code = '1103';
	  CUR_Loop:LOOP
	  FETCH cur_sus_trans_ into v_acct_code,v_customer_type,v_stcr,v_tosc,v_stcb,v_aosp;
		
		 if ret_cur_suspcs_black = 1 THEN
	            LEAVE CUR_Loop;
		 else
		 
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
     select uuid() AUTO_ID,t.* from (
        select distinct 
               '0' DATA_FROM,
               '0' DELETE_STATE,
               '4' data_state,
               '0' CHECK_STATE, -- 校验状态 0 未校验；1 校验失败；2 校验部分成功；3 校验成功
               '' AUDIT_OPINION, -- 审核意见
               '0' AUDIT_STATE, -- 审核状态 2 初审驳回；0  未审核；4 终审通过；6  复审通过；1  复审驳回；3  初审通过；5  终审驳回
               '1' AUDIT_TYPE_STATE, -- 报送标识 1 上报；2 不上报
               v_suspcs_code SUSPCS_CODE, -- 可疑事件识别码
               '1103' suspcs_model_code, -- 可疑模型编码
               v_dt_date DATE_ID, -- 数据日期
               atr.trans_code TRANS_CODE, -- 交易流水号
               atr.branch_code BRANCH_CODE, -- 机构号
               atr.customer_type CUSTOMER_TYPE, -- 客户类型 1 对私；2 对公
               atr.customer_no CUSTOMER_NO, -- 客户编号
               atr.customer_name CUSTOMER_NAME, -- 客户姓名
               atr.acct_code ACCT_CODE, -- 账号
               atr.trans_amount_cny TRANS_AMOUNT, -- 交易金额
               atr.trans_use TRANS_USE, -- 资金用途
               atr.currency CURRENCY, -- 客户国籍
               atr.payment_flag PAYMENT_FLAG, -- 资金收付标识 01 收；02 付
               atr.trans_type TRANS_TYPE, -- 交易方式
               atr.trans_date TRANS_DATE, -- 交易日期
																atr.trans_nationality trans_nationality, -- 交发生地国籍
																atr.trans_address trans_address, -- 交发生地代码
               v_tosc TOSC, -- 可疑犯罪类型
               v_stcr STCR, -- 可疑特征码
               v_stcb STCB, -- 客户资金行为情况
               v_aosp AOSP, -- 疑点分析
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
                                          concat(acps.nationality_code1,',',acps.nationality_code2)
                                       end)  WHEN
                                       '2' then
                                       (case when acpg.nationality_code2  
																			     is null or acpg.nationality_code2 = '' then
                                          acpg.nationality_code1
                                       else
                                          concat(acpg.nationality_code1,',',acpg.nationality_code2)
                                       end) end STNT, -- 可疑主体国籍
                                       case  atr.customer_type when
                                       '1' then
                                       (case when acps.phone_number2 
                                          is null or acps.phone_number2  = '' then
                                          acps.phone_number1
                                         else
                                          concat(acps.phone_number1 , ',' , acps.phone_number2)
                                       end) WHEN
                                       '2' then
                                       (case when acpg.phone_number2 
                                          is null or acpg.phone_number2  = '' then
                                          acpg.phone_number1
                                         else
                                          concat(acpg.phone_number1 , ',' ,acpg.phone_number2)
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
         left join aml_customer_personal_run acps on acps.customer_no = atr.customer_no and acps.date_id = v_dt_date
         left join aml_customer_public_run acpg
          on atr.customer_no = acpg.customer_no
         and acpg.date_id = v_dt_date
         where atr.acct_code = v_acct_code and atr.customer_type = v_customer_type 
         ) t; 
			
							
		 end if;
		 
  end loop CUR_Loop;
	
    CLOSE cur_sus_trans_; -- 关闭游标
		
	end;
	

	
	
	DROP table if exists aml_func_dist_str_tmp01;
	create table aml_func_dist_str_tmp01(str1 varchar(2000));
	-- 汇总可疑特征码
	select func_dist_str(GROUP_CONCAT(distinct stcr),',') into v_stcr from aml_suspcs_case_detail where date_id = v_dt_date and suspcs_model_code = '1103';
	-- 汇总涉嫌犯罪类型
	select func_dist_str(GROUP_CONCAT(distinct tosc),',') into v_tosc from aml_suspcs_case_detail where date_id = v_dt_date and suspcs_model_code = '1103';
	-- 汇总资金交易行为情况
	select func_dist_str(GROUP_CONCAT(distinct stcb),',') into v_stcb from aml_suspcs_case_detail where date_id = v_dt_date and suspcs_model_code = '1103';
	-- 汇总疑点分析
	select func_dist_str(GROUP_CONCAT(distinct aosp),',') into v_aosp from aml_suspcs_case_detail where date_id = v_dt_date and suspcs_model_code = '1103';	
	
		
		-- 输出可疑事件表
  delete from aml_suspcs_case where date_id = v_dt_date and suspcs_model_code = '1103';
	select count(1) into null_flag from aml_suspcs_case_detail where date_id = v_dt_date and suspcs_model_code = '1103';
	if null_flag > 0 then
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
           uuid() auto_id,
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
           v_dt_date date_id, -- 数据日期
           v_suspcs_code suspcs_cdoe, -- 可疑事件识别码
           '1103' suspcs_model_code, -- 可疑模型编码
           as1.RICD RICD, -- 报告机构编码
           as1.RPNC RPNC, -- 上报网点代码
           as1.RITP RITP, -- 行业类别
           as1.DETR DETR, -- 通用可疑交易报告紧急程度
           '1' TORP, -- 报送次数标志
           '' ORXN, -- 初次报送的通用可疑交易报告报文名称
           v_tosc TOSC, -- 可疑主体犯罪类型
           v_stcr STCR, -- 可疑交易特征代码
           as1.ODRP ODRP,
           as1.DORP DORP,
           as1.TPTR TPTR,
           as1.OTPR OTPR,
           v_stcb STCB,
           v_aosp AOSP,
           ascd1.min_trans_date SBDT,
           ascd1.max_trans_date SEDT,
           '' ROTF1,
           '' ROTF2
      from (select * from aml_susmodel where susModelCode = '1103') as1,
           (select min(date_format(trans_date,'%Y%m%d')) min_trans_date,
                   max(date_format(trans_date,'%Y%m%d')) max_trans_date
              from aml_suspcs_case_detail
             where date_id = v_dt_date
               and suspcs_model_code = '1103') ascd1;
		end if;
    END