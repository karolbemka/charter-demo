# Charter Demo application #

# Overview #
This demo app allows creating customers and related to them transactions.
Upon every transaction bonus points are calculated with below rules:
 - for every dollar spend over 50$ - customer receives 1 point
 - for every dollar spend over 100$ - customer receives 2 points
 - example: customers transaction amount is 120$ - he will receive 90$ (50 x 1 point + 20 x 2 points)

 Points limits and amount of points per each limit are parametrized. Default values are set as described above.
 - `demoapp.points.firstLimit`
 - `demoapp.points.secondLimit`
 - `demoapp.points.firstLimitPoints`
 - `demoapp.points.secondLimitPoints`

Endpoint returning given customer points will show current calendar month points and points gathered within last 3 calendar months 
i.e. today is September, therefore returned values will represent points gathered for:
- currentMonthPoints (September)
- threeMonthsPeriodPoints (July, August, September)

Application is filled with small sample of test data. See `schema.sql` for details.

### To run tests 
`./mvnw clean test`

### To run application
`./mvnw spring-boot:run`

### Auto generated Swagger API
`http://localhost:8080/swagger-ui/`