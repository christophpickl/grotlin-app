grotlin-app
===========

[![Travis CI Status](https://travis-ci.org/christophpickl/grotlin-app.svg?branch=master)](https://travis-ci.org/christophpickl/grotlin-app)

This is off-spin of the famous round based strategy game "mission risk".
It's implemented in kotlin (a bleeding edge JVM language) using gradle.
Target platform is (FTM) android. JavaScript (web) is in plan, maybe there will be an iOS client as well.


TODO
!!! find a product name!!!
!! unify logging. logback vs log4j2? slf4j for android + app engine.
! use a custom (vnd) accept type (instead of application/version) to support versioning

app
---------

TODO
* zoom map
* reset game
* bugfix on orientation (saveInstance, ..)
! testing requires some effort (android.jar is just a stub); also how to run regular unit tests??
! black background should only apply to activity (not dialogs, toasts, etc)
! logging doesnt work, SLF4J <= DEBUG doesnt get printed in logcat
* have a look at: https://github.com/vladlichonos/kotlinAndroidLib
? gauge vs jbehave vs ... bdd?

multi
---------

* add swagger


NOTE for local startup execute "gradle appengineRun"
  - http://localhost:8888/_ah/admin
  - http://localhost:8888/version
NOTE  deploy via "gradle appengineUpdate"
  - http://swirl-engine.appspot.com/version



TODO
! hacky hack gradle filter resources: build.dependsOn filterSwirlConfig
! audio (roll dices, win/lose, button clicks)
! improve game UI (end turn button, player list, better response messages)
- settings (change audio settings)
- invalidate access token after x hours
- during build, dont cache version.properties, so buildDate is up2date
// use http://java-websocket.org instead?

links
--------
http://stackoverflow.com/questions/23875648/android-studio-gradle-failed-to-complete-gradle-execution
https://cloud.google.com/appengine/docs/java/endpoints/getstarted/clients/android/connect_backend