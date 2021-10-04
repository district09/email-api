Feature: Search emails v2
  
Background:
  * configure logPrettyRequest = true
  * configure logPrettyResponse = true
  * configure ssl = true
  * url baseUrl

Scenario: Search email with id - 200
  Given path '/supporting/email/v2' + '/emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "to": "servicefactory_email@digipolis.gent",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor",
    "html": "Lorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor",
    "replyTo": "servicefactory_email@digipolis.gent"
  }
  """
  When method POST
  Then status 201
  And def location = response.locations[0]

  Given path location
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 200

Scenario: Search email with invalid id - 400
  Given path '/supporting/email/v2' + '/emails/a'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 400

Scenario: Search email with nonexistent id - 404
  Given path '/supporting/email/v2' + '/emails/999999999999999'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  When method GET
  Then status 404

Scenario: Search email with id with other tenantId - 403
  Given path '/supporting/email/v2' + '/emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "to": "servicefactory_email@digipolis.gent",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor",
    "html": "Lorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor",
    "replyTo": "servicefactory_email@digipolis.gent"
  }
  """
  When method POST
  Then status 201
  And def location = response.locations[0]

  Given path location
  And header tenantId = 'otherTenantId'
  When method GET
  Then status 403

Scenario: Search email with id without tenantId - 403
  Given path '/supporting/email/v2' + '/emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "to": "servicefactory_email@digipolis.gent",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor",
    "html": "Lorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor \n Dit is de volgende regelLorem ipsum dolor",
    "replyTo": "servicefactory_email@digipolis.gent"
  }
  """
  When method POST
  Then status 201
  And def location = response.locations[0]

  Given path location
  When method GET
  Then status 403