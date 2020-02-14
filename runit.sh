  mvn clean compile exec:java -Dexec.mainClass="com.datastax.banking.Main"  -DcontactPoints=localhost -Dcreate=true -DnoOfTransactions=10000 -DnoOfCustomers=1000 -DnoOfDays=5
