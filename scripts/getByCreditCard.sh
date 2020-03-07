#  easiest to look up a credit card using redinsight and edit this script to find an existing credit card
# date can be looked up and put in range
curl -X GET -H "Content-Type: application/json"  'http://localhost:8080/creditCardTransactions/?creditCard=e0d36dd2x019fx4396xb90ex07cbc92f3bdc&account=Acct2&from=02/25/2020&to=03/04/2020'
