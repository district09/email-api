Feature: Search emails v2
  
Background:
  * configure logPrettyRequest = true
  * configure logPrettyResponse = true
  * configure ssl = true
  * url baseUrl

Scenario: Search all emails - 200
  Given path '/emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 200

Scenario: Search all emails from sender - 200
  Given path '/emails'
  And param $filter = 'from+eq+servicefactory_email@digipolis.gent'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 200

Scenario: Search all emails from nonexistent sender - 200
  Given path '/emails'
  And param $filter = 'from+eq+nonexistent@digipolis.gent'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 200

Scenario: Search all emails from invalid sender - 200
  Given path '/emails'
  And param $filter = 'from+eq+foobardigipolis.gent'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 200

Scenario: Search emails with invalid filter query - 400
  Given path '/emails'
  And param $filter = 'text+eqqqqq+Lorem'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 200

Scenario: Search emails with invalid filter argument - 400
  Given path '/emails'
  And param $filter = 'whatIsThis+eq+Lorem'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 200

Scenario: Search all emails with other tenantId - 200
  Given path '/emails'
  And header tenantId = 'otherTenantId'
  When method GET
  Then status 200

Scenario: Search all emails without tenantId - 403
  Given path '/emails'
  When method GET
  Then status 403