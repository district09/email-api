#!/usr/bin/env groovy

// @Library('jenkins-shared-pipelines@skipDeploy') _

d09Project  kind: "maven",
            namespace: "supporting",
            service: ["email-api-v3"],
            gitOpsPath: "email-service/email-api-v3",
            skipTest: "true",
            skipBuild: "true",
            useSemanticRelease: "true"
