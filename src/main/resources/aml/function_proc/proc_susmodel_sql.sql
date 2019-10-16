CREATE DEFINER=`aml`@`%` PROCEDURE `proc_susmodel`(in dt_date date)
BEGIN
   declare v_sql varchar(4000);
   
	 -- 加载基础数据到中间表
	 set @v_sql = 'truncate table aml_customer_public_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 insert into aml_customer_public_run
	 select * from aml_customer_public where date_id = cast(dt_date as date);
	 
	 set @v_sql = 'truncate table aml_customer_personal_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 insert into aml_customer_personal_run
	 select * from aml_customer_personal where date_id = cast(dt_date as date);
	 
	 set @v_sql = 'truncate table aml_customer_beneficiary_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 insert into aml_customer_beneficiary_run
	 select * from aml_customer_beneficiary where date_id = cast(dt_date as date);
	 
	 set @v_sql = 'truncate table aml_account_run';
   PREPARE stmt1 FROM @v_sql;
	 EXECUTE stmt1;
	 DEALLOCATE PREPARE stmt1;
	 insert into aml_account_run
	 select * from aml_account where date_id = cast(dt_date as date);
	 
	 
   select enable into @v_str from aml_susmodel where susModelCode = '1101';
   if @v_str = '1' then
   call proc_susmodel_1101(dt_date);
	 end if;
	 
	 select enable into @v_str from aml_susmodel where susModelCode = '1102';
   if @v_str = '1' then
   call proc_susmodel_1102(dt_date);
	 end if;
	 
	 select enable into @v_str from aml_susmodel where susModelCode = '1103';
   if @v_str = '1' then
   call proc_susmodel_1103(dt_date);
   end if;
	 
	 select enable into @v_str from aml_susmodel where susModelCode = '1104';
   if @v_str = '1' then
   call proc_susmodel_1104(dt_date);
   end if;
	 	 
	 select enable into @v_str from aml_susmodel where susModelCode = '1105';
   if @v_str = '1' then
   call proc_susmodel_1105(dt_date);
   end if;
	 	 
	 select enable into @v_str from aml_susmodel where susModelCode = '1106';
   if @v_str = '1' then
   call proc_susmodel_1106(dt_date);
   end if;
	 	 
	 select enable into @v_str from aml_susmodel where susModelCode = '1107';
   if @v_str = '1' then
   call proc_susmodel_1107(dt_date);
   end if;
	 
	 select enable into @v_str from aml_susmodel where susModelCode = '1201';
   if @v_str = '1' then
   call proc_susmodel_1201(dt_date);
   end if;
	 
	 
	 call proc_whitemodel(dt_date);
	 
	 	 
end