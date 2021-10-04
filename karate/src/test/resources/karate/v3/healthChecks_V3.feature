Feature: Health checks v2
  
Background:
  * configure logPrettyRequest = true
  * configure logPrettyResponse = true
  * configure ssl = true
  * url baseUrl
  
Scenario: Ask Service Status
  Given path '/status/am-i-up'
  When method GET
  Then status 200

  Scenario: Ask aggregate status
  Given path '/status/aggregate'
  When method GET
  Then status 200

Scenario: Ask Service Information
  Given path '/status/about'
  When method GET
  Then status 200

Scenario: Ask activemq dependency status
  Given path '/status/activeMq'
  When method GET
  Then status 200

Scenario: Ask mssql dependency status
  Given path '/status/msSql'
  When method GET
  Then status 200

Scenario: Ask non-existent dependency status
  Given path '/status/dependencyDoesNotExist'
  When method GET
  Then status 404