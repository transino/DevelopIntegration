CREATE Function [dbo].[func_dist_str](@str varchar(4000),@cut_str varchar(200)) 
 Returns varchar(4000)
 As 
--该函数用于字符串用逗号分隔去重后重新组装
Begin 
declare @aml_func_dist_str_tmp01 table
		 (
		 str1 varchar(4000)
		 );
 set @str = @str+',' 
 Declare @insertStr varchar(500) --截取后的第一个字符串 
 Declare @newstr varchar(2000) --截取第一个字符串后剩余的字符串 
set @insertStr = left(@str,charindex(@cut_str,@str)-1) 
 set @newstr = stuff(@str,1,charindex(@cut_str,@str),'') 
 Insert @aml_func_dist_str_tmp01 Values(@insertStr) 
 while(len(@newstr)>0) 
 begin 
 set @insertStr = left(@newstr,charindex(@cut_str,@newstr)-1) 
 Insert @aml_func_dist_str_tmp01 Values(@insertStr) 
 set @newstr = stuff(@newstr,1,charindex(@cut_str,@newstr),'') 
 end 
 select @str = stuff(( select ','+ str1 from (select distinct str1 from @aml_func_dist_str_tmp01) t for xml path('')),1,1,'')
 Return @str
 End