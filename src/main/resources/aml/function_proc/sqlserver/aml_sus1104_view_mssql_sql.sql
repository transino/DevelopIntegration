create view [dbo].[aml_sus1104_view] as
select atr.customer_no AS customer_no,
atr.currency AS currency,
atr.payment_flag AS payment_flag,
sum(cast(atr.trans_amount as float)) AS trans_amount,
sum(cast(atr.trans_amount_cny as float)) AS trans_amount_cny,
count(1) AS sum_number,
customer_type customer_type
from aml_transaction_run atr 
group by atr.customer_no,atr.currency,atr.payment_flag,atr.customer_type