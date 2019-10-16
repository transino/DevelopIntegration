CREATE  PROCEDURE [dbo].[proc_susmodel]
@dt_date date
as
BEGIN
   declare @v_sql varchar(4000);
   declare @v_str varchar(4000);
   
		-- 加载基础数据到中间表
		truncate table aml_customer_public_run;
		insert into aml_customer_public_run
		select * from aml_customer_public where date_id = @dt_date;

		truncate table aml_customer_personal_run;
		insert into aml_customer_personal_run
		select * from aml_customer_personal where date_id = @dt_date;

		truncate table aml_customer_beneficiary_run;
		insert into aml_customer_beneficiary_run
		select * from aml_customer_beneficiary where date_id = @dt_date;

		truncate table aml_account_run
		insert into aml_account_run
		select * from aml_account where date_id = @dt_date;
	 
		select @v_str = enable from aml_susmodel where susModelCode = '1101';
		if @v_str = '1' begin
		  exec proc_susmodel_1101 @dt_date ;
		end;
		 
	   select @v_str = enable from aml_susmodel where susModelCode = '1102';
	   if @v_str = '1' begin
		  exec proc_susmodel_1102 @dt_date;
	   end;
		 
	   select @v_str = enable from aml_susmodel where susModelCode = '1103';
	   if @v_str = '1' begin
		  exec proc_susmodel_1103 @dt_date;
	   end;
		 
	   select @v_str = enable from aml_susmodel where susModelCode = '1104';
	   if @v_str = '1' begin
	   exec proc_susmodel_1104 @dt_date;
	   end; 
	   
	   select @v_str = enable from aml_susmodel where susModelCode = '1201';
	   if @v_str = '1' begin
	   exec proc_susmodel_1201 @dt_date;
	   end;
	   
	   select @v_str = enable from aml_susmodel where susModelCode = '2001';
	   if @v_str = '1' begin
	   exec proc_susmodel_2001 @dt_date;
	   end;
	   
	   -- 执行白名单过滤
	   exec proc_whitemodel @dt_date;
	  
	   
	 	 
   --select @v_str = enable  from aml_susmodel where susModelCode = '1105';
   --if @v_str = '1' begin
   --exec proc_susmodel_1105 @dt_date;
   --end;
	 	 
   --select @v_str = enable  from aml_susmodel where susModelCode = '1106';
   --if @v_str = '1' begin
   --exec proc_susmodel_1106 @dt_date;
   --end;
	 	 
   --select @v_str = enable  from aml_susmodel where susModelCode = '1107';
   --if @v_str = '1' begin
   --exec proc_susmodel_1107 @dt_date;
   --end;
   
	 	 
end
