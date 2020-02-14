mvn clean compile exec:java -Dexec.mainClass="com.datastax.banking.Main"  -DcontactPoints=node0 -Dcreate=true -DnoOfTransactions=100000 -DnoOfCustomers=100000 -DnoOfDays=5


