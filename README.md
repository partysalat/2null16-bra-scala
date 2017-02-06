# 2null16-bra

====================================================

Software dependencies
----------------------------------------------------


Install on Rasperry Pi
----------------------------------------------------
## Deployment
In order to deploy to raspberry pi look into bin/deploy.sh, there you should see whats going on.

## Restart on boot

To restart the application on boot, you just add a script somewhere with:
```
#!/usr/bin/env bash
nohup /home/pi/2null16-bra-scala/2null16-bra-1.0-SNAPSHOT/bin/2null16-bra -J-Xms128M -J-Xmx256M -Dconfig.resource=application-pi.conf -Dhttp.port=9000 -Dplay.crypto.secret="QCY?tAnfk?aZnfui823hu8bfuiqnwk"
```

and reference it in the /etc/rc.local with
```
su - pi -c <FULL_PATH_TO_SCRIPT>/start.sh &
```



# TODO

* add a little bit more achievements
* INDEX on some fields
* Add photo news functionality