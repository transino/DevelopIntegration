CREATE DEFINER=`aml`@`%` FUNCTION `func_suspcs_code`(suspcs_code varchar(20)) RETURNS varchar(20) CHARSET utf8
begin
declare suspcs_codes varchar(200);
declare inti int;
  
  set inti = 0;
	set suspcs_codes = '';
  if (suspcs_code is null or suspcs_code = '' or suspcs_code = '0') then
     set suspcs_codes = '00000001';
  elseif(length(suspcs_code) = 8 and (suspcs_code REGEXP '[^0-9.]') = 0) then
     while (inti< 8-length(cast(suspcs_code as signed)+1)) do
       set suspcs_codes = concat('0',suspcs_codes);
       set inti = inti + 1;
     end while;  
     set suspcs_codes = concat(suspcs_codes,cast(suspcs_code as signed)+1);
  else
    set suspcs_codes = '0';
  end if;

  return suspcs_codes;

end