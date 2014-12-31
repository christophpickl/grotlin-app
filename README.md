grotlin-app
===========

[![Travis CI Status](https://travis-ci.org/christophpickl/grotlin-app.svg?branch=master)](https://travis-ci.org/christophpickl/grotlin-app)

See [Wiki](https://github.com/christophpickl/grotlin-app/wiki) for more info.

FIXMEs
-------
# org.jboss.resteasy.spi.UnhandledException: org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure: Could not find MessageBodyWriter for response object of type: at.cpickl.grotlin.endpoints.FaultRto of media type: application/octet-stream

TODOs
-------
# @multi: resetDb on startup (when running locally)
# @app: use mockito: http://www.vogella.com/tutorials/Mockito/article.html#mockito_verify
# @js-client: channel JS load via javascript (instead hardcoded in HTML)



resource filtering
-------
// http://stackoverflow.com/questions/12813887/gradle-replace-tokens-by-finding-the-tokens-from-property-file

// http://www.gradle.org/docs/current/userguide/working_with_files.html
// http://www.gradle.org/docs/current/javadoc/org/gradle/api/tasks/Copy.html

task filterSwirlConfig(type: Copy) {
//    from("src/main/resources") {
//        include '**/*.properties'
//        filter(ReplaceTokens, tokens: ["artifact.version": project.properties["version"], "build.date": buildDate])
//    }
    from "src/main/resources/swirl.config.properties"
    into "build/resources/main"
    filter(ReplaceTokens, tokens: ["artifact.version": project.properties["version"], "build.date": buildDate])
//    expand("artifact.version": project.property("version"), "build.date": buildDate)
//    expand(project.properties)
}
build.dependsOn filterSwirlConfig
appengineUpdate.dependsOn filterSwirlConfig


//tasks.whenTaskAdded { task ->
//    if (task.name == 'build') {
//        task.dependsOn filterSwirlConfig
//    }
//}
