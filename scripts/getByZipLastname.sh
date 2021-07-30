#  get by zipcode and lastname.  Lastname is a generated integer
curl -X GET -H "Content-Type: application/json"  'http://localhost:8080/customerByZipcodeLastname/?zipcode=55441&lastname=00383'
