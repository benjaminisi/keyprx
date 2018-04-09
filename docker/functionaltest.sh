#! bin/bash  

DOMAIN=localhost:8087

# create a reservable block with overbooked capacity of 4.  This is three plue 50% of three (tossing away fractional rooms)
curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"Hotel David\",  \"capacity\" : \"3\",  \"overbookPercent\" : \"50\" }" http://${DOMAIN}/api/blocks

curl -H 'Accept:application/json' http://${DOMAIN}/api/blocks
    
curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"DavidBenjamin\",  \"email\" : \"davidkeyprx@dbenjamin.com\",  \"startTime\" : \"$((`date +%s` + 99999))000\",  \"endTime\" : \"$((`date +%s` + 199999))000\" }" http://${DOMAIN}/api/reservations

curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"DavidBenjamin\",  \"email\" : \"davidkeyprx@dbenjamin.com\",  \"startTime\" : \"$((`date +%s` + 99999))000\",  \"endTime\" : \"$((`date +%s` + 199999))000\" }" http://${DOMAIN}/api/reservations

curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"DavidBenjamin\",  \"email\" : \"davidkeyprx@dbenjamin.com\",  \"startTime\" : \"$((`date +%s` + 99999))000\",  \"endTime\" : \"$((`date +%s` + 199999))000\" }" http://${DOMAIN}/api/reservations

curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"DavidBenjamin\",  \"email\" : \"davidkeyprx@dbenjamin.com\",  \"startTime\" : \"$((`date +%s` + 99999))000\",  \"endTime\" : \"$((`date +%s` + 199999))000\" }" http://${DOMAIN}/api/reservations

# this one should be rejected
curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"DavidBenjamin\",  \"email\" : \"davidkeyprx@dbenjamin.com\",  \"startTime\" : \"$((`date +%s` + 99999))000\",  \"endTime\" : \"$((`date +%s` + 199999))000\" }" http://${DOMAIN}/api/reservations

# the "id" field is one higher than you might expect because the counter is shared with the block repository
curl -H 'Accept:application/json' http://${DOMAIN}/api/reservations
