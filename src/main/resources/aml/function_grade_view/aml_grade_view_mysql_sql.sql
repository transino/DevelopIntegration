﻿create view aml_grade_view as
select agd.auto_id AS auto_id,ag.audit_state AS audit_state,ag.grade_code AS grade_code,ag.date_id AS date_id,ag.customer_type AS customer_type,ag.customer_no AS customer_no,ag.customer_name AS customer_name,ag.grade_score AS grade_score,ag.credit_rate_code3 AS credit_rate_code3,ag.credit_rate_code4 AS credit_rate_code4,ag.credit_rate_code5 AS credit_rate_code5,ag.risk_due_date3 AS risk_due_date3,ag.risk_due_date4 AS risk_due_date4,ag.risk_due_date5 AS risk_due_date5,ag.junior_help_user AS junior_help_user,ag.repeat_help_user AS repeat_help_user,ag.cutout_help_user AS cutout_help_user,ag.grade_number AS grade_number,ag.grade_date AS ag_grade_date,ag.grade_state AS grade_state,ag.branch_code AS branch_code,agd.grade_date AS agd_grade_date,agd.risk_section_code AS risk_section_code,agd.risk_section_name AS risk_section_name,agd.new_grade_score AS new_grade_score,agd.system_grade_score AS system_grade_score,agd.rule_source AS rule_source,agd.grade_source AS grade_source,agd.grade_cust AS grade_cust,agd.grade_reason AS grade_reason from (aml_grade ag left join aml_grade_detail agd on((ag.grade_code = agd.grade_code))) order by ag.grade_state,risk_section_code,grade_code