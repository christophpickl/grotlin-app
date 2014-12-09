grotlin-app
===========

This is off-spin of the famous round based strategy game "mission risk".
It's implemented in kotlin (a bleeding edge JVM language) using gradle.
Target platform is (FTM) android. JavaScript (web) is in plan, maybe there will be an iOS client as well.


TODO
!! unify logging. logback vs log4j2? slf4j for android + app engine.

app
---------

TODO
!! logging doesnt work

multi
---------

* for local startup execute "gradle appengineRun"
  - http://localhost:8888/_ah/admin
  - http://localhost:8888/version
* deploy via "gradle appengineUpdate"
  - http://swirl-engine.appspot.com/version



TODO
! audio (roll dices, win/lose, button clicks)
! improve game UI (end turn button, player list, better response messages)
- settings (change audio settings)
- invalidate access token after x hours
