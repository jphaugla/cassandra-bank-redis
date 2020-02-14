curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' -d 'accountNo=627619a1-b9a5-4ba6-8d9f-58324d48e089' -d 'customerId=627619a1' -d 'chgdate=2018-07-29 19:53:13.165 GMT'   "http://localhost:8081/datastax-bank-techday/rest/custChange"
sleep 30
curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' -d 'accountNo=486f2572-4e8e-465b-a3db-d64ac4ba2360' -d 'customerId=486f2572' -d 'chgdate=2018-07-29 19:53:13.165 GMT'   "http://localhost:8081/datastax-bank-techday/rest/custChange"
sleep 30
curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' -d 'accountNo=56d18578-2488-47e5-8f4b-d729a528468e' -d 'customerId=56d18578' -d 'chgdate=2018-07-29 19:53:13.165 GMT'   "http://localhost:8081/datastax-bank-techday/rest/custChange"
