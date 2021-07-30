#  easiest to look up a credit card using redinsight and edit this script to find an existing credit card
# date can be looked up and put in range
curl -X GET -H "Content-Type: application/json"  'http://localhost:8080/creditCardTransactions/?creditCard=efa36d62x99f6x41dcx9b89xb4c01622dbdf&account=Acct2&from=02/25/2021&to=07/31/2021'
