function fn() {   
    var env = karate.env; // get java system property 'karate.env'
    karate.log('karate.env system property was:', env);
    var config = { // base config JSON
      baseUrl: 'http://email-api.servicefactorydv.svc:8080/supporting/email/v3',
    };
    karate.configure('connectTimeout', 60000);
    karate.configure('readTimeout', 60000);
    return config;
  }
