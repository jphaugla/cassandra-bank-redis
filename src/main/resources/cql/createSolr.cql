CREATE SEARCH INDEX if not exists ON bank.customer with columns address_line1
                ,city
                ,email_address
                ,first_name
                ,full_name
                ,last_name
                ,middle_name
		,government_id
		,government_id_type
                ,phone_numbers
                ,state_abbreviation
                ,zipcode
		,custaccounts
;
CREATE SEARCH INDEX if not exists ON bank.transaction with columns tranPostDt
                ,merchantName
                ,merchantCtgyDesc
                ,cardNum
;
