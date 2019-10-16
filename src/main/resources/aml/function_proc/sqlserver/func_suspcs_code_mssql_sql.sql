
CREATE FUNCTION [dbo].[func_suspcs_code](@suspcs_code varchar(20)) 
RETURNS varchar(20)
as
begin

	declare @suspcs_codes varchar(20);
	declare @inti int;
  
    set @inti = 0;
    set @suspcs_codes = '';
    
  if (@suspcs_code is null or @suspcs_code = '' or @suspcs_code = '0') 
     begin
       set @suspcs_codes = '00000001';
       
     end
  else if(len(@suspcs_code) = 8 and ISNUMERIC(@suspcs_code) = 1) 
     begin
         while (@inti< 8-len(cast(@suspcs_code as int))) 
		 begin
		   set @suspcs_codes = convert(varchar,'0')+@suspcs_codes;
		   set @inti = @inti + 1;
		 end; 
		 
         set @suspcs_codes = convert(varchar,@suspcs_codes)+convert(varchar,cast(@suspcs_code as int)+1);
		 
     end 
     
  else
    begin
      set @suspcs_codes = '0';
    end
  
  return @suspcs_codes;

end