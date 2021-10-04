function fn() {   
    var env = karate.env; // get java system property 'karate.env'
    karate.log('karate.env system property was:', env);
    var config = { // base config JSON
      baseUrl: java.lang.System.getenv('APP_BASEURL') + '/supporting/email/v3',
    };
    karate.configure('connectTimeout', 60000);
    karate.configure('readTimeout', 60000);
    return config;
  }