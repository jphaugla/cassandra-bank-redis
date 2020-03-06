Datastax Bank Techday
========================

NOTE:  This asset has been changed heavily for DataStax internal use in our Assethub.  All the nodenames have been changed to "node0".  For this to work outside of the Assethub, edit your /etc/hosts file to add this node0 line.

#
127.0.0.1   node0
#

To create the schema, run the following.  Note:  if search is not enabled the create index will fail but since it is last step, it is ok to terminate job and cassandr schema will be in place.

	mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup" -DcontactPoints=localhost

To create the customers, accounts and transactions, run the following (note the create parameter to create customers and accounts as well)
	
	mvn clean compile exec:java -Dexec.mainClass="com.datastax.banking.Main"  -DcontactPoints=localhost -Dcreate=true

You can use the following parameters to change the default no of transactions, customers and no of days.
	
	-DnoOfTransactions=10000000 -DnoOfCustomers=1000000 -DnoOfDays=5

RealTime transactions
When all historical transactions are loaded, the process will start creating random transactions for todays date and time. If you wish just to run real time transactions specify -DnoOfDays=0.

To do the streaming, look at the README.md file in ./streaming

To use the web service run 

	mvn jetty:run
	
The api for the webservices are 

Get Transactions for a Customer

	http://{server}:8080/datastax-bank-techday/rest/get/customer/{customer_id}

	http://localhost:8080/datastax-bank-techday/rest/get/customer/1000111

Get Customer Accounts
	
	http://{server}:8080/datastax-bank-techday/rest/get/accounts/{customer_id}
	
	http://localhost:8080/datastax-bank-techday/rest/get/accounts/1000111
	
Get Transactions For Account 
	
	http://{server}:8080/datastax-bank-techday/rest/get/transactions/{account_id}
	
	http://localhost:8080/datastax-bank-techday/rest/get/transactions/eeceed17-5d7e-40de-be07-bdc2f075feb6

Get Customers by email - email string can have wildcards
	
	http://{server}:8080/datastax-bank-techday/rest/get/customerByEmail/{phoneString}

	http://localhost:8080/datastax-bank-techday/rest/get/customerByEmail/100011*

Get Customers by phone number - phone string can have wildcards
	
	http://{server}:8080/datastax-bank-techday/rest/get/customerByPhone/{phoneString}

	http://localhost:8080/datastax-bank-techday/rest/get/customerByPhone/100011*

Get Customers by full name and phone number - both strings can have wildcards

	http://{server}:8080/datastax-bank-techday/rest/get/customerByFullNamePhone/{fullName}/{phoneString}/
	http://localhost:8080/datastax-bank-techday/rest/get/customerByFullNamePhone/*Jason*/100011*/

Get Customers by credit card number (wildcards work) start and end transaction date

	http://{server}:8080/datastax-bank-techday/rest/get/getcctransactions/{cardnum}/{fromDate}/{toDate}/
	http://localhost:8080/datastax-bank-techday/rest/get/getcctransactions/5a4e5d9e-56c6-41bd-855a-3c38884be07f/20170925/20180101/

Get Customers by credit card number (wildcards work) start and end transaction date with particular TAG

	http://{server}:8080/datastax-bank-techday/rest/get/getcctransactionsTag/{cardnum}/{fromDate}/{toDate}/{tag}/
	http://localhost:8080/datastax-bank-techday/rest/get/getcctransactionsTag/5a4e5d9e-56c6-41bd-855a-3c38884be07f/20170925/20180101/Work

To remove the tables and the schema, run the following.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaTeardown"
    

Resources in src/main/resources/cql

    queries.txt       - cql queries on the customer table 
    trans_queries.txt - cql queries on the transaction table 
    createSolr.cql    - create solr core with cql
    copyToCSV.cql     - copy all table to csv files (run from main directory for output to go in export folder) 	

Resources in src/main/resources/api/

    addTag.html    - add tag to a transaction by opening this file from browser
    removeTag.html - remove tag from a transaction by opening this file from browser
    addTag.sh      - add tag to a transaction using curl command
    removeTag.sh   - remove tag from a transaction using curl command
    addCustChange.sh - use API to add a row to the bank.cust_change table.  The cust_change table is joined to transaction stream to flag transactions with recent customer change

scripts in src/main/scripts/  (all of these must be run from root directory)

    compileSetup.sh - compile and run including creating customers and accounts and transactions
    runTrans.sh - compile and run without creating customers and accounts (only transactions)
    top100.sh  - put top 100 records to sharing directory from export directory for each csv 

