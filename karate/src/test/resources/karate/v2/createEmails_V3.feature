Feature: Create emails v2
  
Background:
  * configure logPrettyRequest = true
  * configure logPrettyResponse = true
  * configure ssl = true
  * url baseUrl

Scenario: Create email with single recipient - 201
  Given path 'emails'
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

Scenario: Create email with multiple recipients - 201
  Given path 'emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "to": "Lorem@ipsum.com, lorum@ipsum.com",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor",
    "html": "Lorem ipsum dolor"
  }
  """
  When method POST
  Then status 201

Scenario: Create email with attachments - 201
  Given path 'emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "to": "servicefactory_email@digipolis.gent",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor",
    "html": "Lorem ipsum dolor Lorem ipsum dolorLorem ipsum dolor Lorem ipsum dolor Lorem ipsum dolor",
    "attachments": [
      {
        "id" : "0",
        "name" : "message.xml",
        "content" : "CiAgICAgIAogICAgICAgICB0ZXN0aW50ZWdyYXRpb24KICAgICAgICAgdGVzdGNvcnJlbGF0aW9uCiAgICAgICAgIGtyaXMudmFuaHVsbGVAZGlnaXBvbGlzLmdlbnQKICAgICAgICAgc3ViamVjdAogICAgICAgICB0cnVlCiAgICAgICAgIAogICAgICAgICAKICAgICAgICAgCiAgICAgICAgIAogICAgICAgICAKICAgICAgICAgCiAgICAgICAgIAogICAgICAgICB0ZXN0CiAgICAgICAgIAogICAgICAgICA8aDE+dGl0ZWw8L2gxPmJsYSBibGEgYmxhPGJyIC8+bm9nIGJsYSBibGE8YSBocmVmPSJ0ZXN0Ij5ibGFoPC9hPgogICAgICAKICAg",
        "contentType" : "application/xml"
      },
      {
        "id" : "1",
        "name" : "dummy.txt",
        "content" : "test",
        "contentType" : "text/plain"
      },
      {
        "id" : "2",
        "name" : "dummy.xml",
        "content": "PHNlbjpTZW5kTWFpbFJlcXVlc3QgeG1sbnM6c2VuPSJodHRwOi8vd3d3LmRpZ2lwb2xpcy5nZW50L3NlbmRtYWlsL3YxL2ludGVyYWN0aW9uL3NlbmRtYWlsb3BlcmF0aW9uIj4KICAgICAgICAgPHNlbjppbnRlZ3JhdGlvbj50ZXN0aW50ZWdyYXRpb248L3NlbjppbnRlZ3JhdGlvbj4KICAgICAgICAgPHNlbjpjb3JyZWxhdGlvbklkPnRlc3Rjb3JyZWxhdGlvbjwvc2VuOmNvcnJlbGF0aW9uSWQ+CiAgICAgICAgIDxzZW46dG8+a3Jpcy52YW5odWxsZUBkaWdpcG9saXMuZ2VudDwvc2VuOnRvPgogICAgICAgICA8c2VuOnN1YmplY3Q+c3ViamVjdDwvc2VuOnN1YmplY3Q+CiAgICAgICAgIDxzZW46c2VuZE1haWw+dHJ1ZTwvc2VuOnNlbmRNYWlsPgogICAgICAgICA8IS0tT3B0aW9uYWw6LS0+CiAgICAgICAgIAogICAgICAgICA8IS0tT3B0aW9uYWw6LS0+CiAgICAgICAgIAogICAgICAgICA8IS0tT3B0aW9uYWw6LS0+CiAgICAgICAgIAogICAgICAgICA8IS0tT3B0aW9uYWw6LS0+CiAgICAgICAgIAogICAgICAgICA8IS0tT3B0aW9uYWw6LS0+CiAgICAgICAgIAogICAgICAgICA8IS0tT3B0aW9uYWw6LS0+CiAgICAgICAgIDxzZW46aHRtbD5ibGEgYmxhJmx0O2JyIC8+Jmx0O2JyIC8+bm9nICZsdDtiPmJsYSZsdDsvYj4gYmxhPC9zZW46aHRtbD48c2VuOmluY2x1ZGVCb2R5PnRydWU8L3NlbjppbmNsdWRlQm9keT4KICAgICAgPC9zZW46U2VuZE1haWxSZXF1ZXN0Pg==",
        "contentType": "application/xml"
      }
    ]
  }
  """
  When method POST
  Then status 201

Scenario: Create email without body - 400
  Given path 'emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And header Content-Type = 'application/json'
  And request ''
  When method POST
  Then status 400

Scenario: Create email without recipient - 400
  Given path 'emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor",
    "html": "Lorem ipsum dolor"
  }
  """
  When method POST
  Then status 400

Scenario: Create email with wrong recipient - 400
  Given path 'emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "to": "servicefactory@@@digipolis.gent",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor",
    "html": "Lorem ipsum dolor"
  }
  """
  When method POST
  Then status 400

Scenario: Create email without sender - 400
  Given path 'emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "to": "Lorem@ipsum.com",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor",
    "html": "Lorem ipsum dolor"
  }
  """
  When method POST
  Then status 400

Scenario: Create email with wrong sender - 400
  Given path 'emails'
  And header tenantId = java.lang.System.getenv('APPLICATION_ID')
  And request
  """
  {
    "from": "lorum@@@@ipsum.com",
    "to": "Lorem@ipsum.com",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor",
    "html": "Lorem ipsum dolor"
  }
  """
  When method POST
  Then status 400

Scenario: Create email without tenantId - 403
  Given path 'emails'
  And request
  """
  {
    "from": "servicefactory_email@digipolis.gent",
    "to": "servicefactory_email@digipolis.gent",
    "subject": "Lorem ipsum dolor",
    "text": "Lorem ipsum dolor",
    "html": "Lorem ipsum dolor"
  }
  """
  When method POST
  Then status 403