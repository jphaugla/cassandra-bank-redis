## Cassandra Bank day
This github integrates cassandra and redisearch.  Since many banks use cassandra, they can initially leverage their cassandra investment and use redisearch as and index and search capability on top of cassandra.  

### This is a diagram of the solution
![diagram solution][images/diagram.png]

### Redisinsight is one of the docker containers
To access redinsight, use `http://localhost:8001/` in the browser
login using the redis  database name of `redis`

### To create the schema, run the following.  
```bash
cqlsh -u cassandra -p jph -f src/main/resources/cql/create_schema.cql 
```
### To compile the code use maven or intellij
```bash
mvn package
```

### To start the API layer, use runit script
```bash
./runit.sh
```
### To create the customers, accounts and transactions
Note: script can be modified to change the number of each entity to be created
This uses an API call to generate the data
```bash
./scripts/generateData.sh	
```
The api for the webservices are in the ./scripts directory each script may need to be customized depending on the amount of data generated.  
Each script has a short explanation.  To get initial values go to redinsight and use these queries.  
In the Redsearch interface, from the dropdowns, select SEARCH, TRANSACTION and enter "@account_no:ACCT2" for the query.  Use these values for any transaction API's.  Next, select SEARCH, CUSTOMER and enter "@state_abbreviation:MN".


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

