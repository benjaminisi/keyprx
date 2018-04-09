#  Keypr challenge implementation by David Benjamin

##  The Challenge

Provide a basic reservation API with simple overbooking.

##  Usage

Spring HATEOAS provides discoverability of REST API links.  See

  https://docs.spring.io/spring-data/rest/docs/3.0.5.RELEASE/reference/html/#repository-resources.resource-discoverability

### API

The following URLs provides entrypoints into the HATEOAS "self-documenting" API

```bash
  curl http://localhost:8087/api
  
  # add a block.  Only the first block added is used
  curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"Hotel David\",  \"capacity\" : \"3\",  \"overbookPercent\" : \"50\" }" http://localhost:8087/api/blocks
    
  # request all blocks  
  curl -H 'Accept:application/json' http://localhost:8087/api/blocks
    
  # request adding a reservation.   Do repeatedly and the block will fill up
  curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"DavidBenjamin\",  \"email\" : \"davidkeyprx@dbenjamin.com\",  \"startTime\" : \"$((`date +%s` + 99999))000\",  \"endTime\" : \"$((`date +%s` + 199999))000\" }" http://localhost:8087/api/reservations
    
  # request all accepted reservations  
  curl -H 'Accept:application/json' http://localhost:8087/api/reservations
```

### Resources

* **Blocks**: a set of reservable units (rooms).   Currently, only one block is supported and all reservations are assumed to be for that block.
** Capacity is the physical number of reservable units
** Overbooking percent indicates the number of reservations beyond the capacity

* **Reservations**: bookings of units without exceeding capacity plus allowed overbooking
** Name
** Email
** Start time is the arrival/check in time
** End time is the departure / check out time

Times are "unix epoch milliseconds" in UTC.   It is up to the interface to accept and present times as appropriate to the client/user, which is likely to be using the hotel's time zone.  Also, the caller is responsible for setting the arrival / check in time day and the departure / check out time of day.


##  Build and Deploy

Clone the github...benjaminisi/keyprx and cd into the checkout directory
```bash
git clone 
```
Or in a container using the included Dockerfile.  Have localhost port 8087 available or specify a different port mapping in docker/docker-compose.yml
```bash
cd keyprx
bash ./gradlew build
cd docker
docker build -t keyprx:1.0.0 .
docker-compose up
curl http://localhost:8087/api/...
```
For a real-world application, containers for each "concern" would be separate and networked together.  For example, the database and the Java application would each be in their own container.  Persistent storage for the database would also best be externalized, if not using an entirely managed cloud database such as Amazon Aurora or RDS.

## If not using the included Docker configuration

A postgres database is expected with the login/password of postgres/postgres (see section below "Future directions").  If you are not using the included docker-compose.yml, then the DB needs to be set up as follows.
```sql
create database keyperx;
grant all PRIVILEGES on database keyprx to postgres;
```
With the database in place, bring up the application.  It will create the tables needed in the database.
```bash
cd keyprx
bash ./gradlew build
java -jar build/libs/keyprx-1.0.0.jar
curl http://localhost:8087/api/...
```
##  Running Automated Tests

docker exec -it <container ID> .......

## Future directions

* Secure database access by making passwords randomized and not retrievable by the likes of
 ```bash
 $ docker run --rm -it test sh echo $ADMIN_USER)
 ```
* Test performance and optimize algorithm and caching based on ratio of lookups and saves/updates 

* Externalize configuration such as Postgres user/password and db name

* Index startTime and endTime in the database

* ReservationHelper service should have an interface extracted

* This assumes an API Gateway that logs accesses

* Add logging to aid in client diagnosis; bad inputs log at warn or info level

* Validate create/save of Block for fields capacity and overbook percent

* Either remove PUT/PATCH for Reservations or fix validation to allow for reusing the existing reservation in filling the modified reservation. 

* Configure JSON keys to conform to snake case

* Add Functional/Integration tests

* Add comments on public methods

* Support reservations for more than one block.  A reservation would need to identify the intended block.  Filter reservation searches by block id.

* Support multiple tenants -- isolated sets of data, usually by client business

* Support hierarchies of blocks.  For example, fancier rooms can be reserved as overflow for lower-cost rooms.

* Secure for use on the public Internet.  Add user management, authentication, and Authorization through services, maybe microservices.

* Add a UI

* Hold a reservation while a user thinks it over.  This also serves for transactionality among clustered reservation servers.

* Add continuous integration

* Support devops with a health check endpoint

* Support analytics

* Support BI, say capacity planning and marketing, with plots

* Reject or warn when a decrease in capacity or overbooking percent causes a violoation with existing reservations

* Parallelize and/or cache the block fetch in the Create Reservation validator; do appropriate cache invalidation when clustering

* Externalize SQL constant 

* Trap outgoing 5xx error responses to log extensively and avoid exposing internals

##  Technical discussion

The reservation helper service layer can grow to support horizontal scale out through a managed shared cache.

Spring Data REST exposes error responses out to the http client by default.  The service layer would be expanded to wrap most of the persistence layer if this program were to support more internal consumers of reservations.   As a microservice supporting a front-end service, the current implementation is okay in this regard.

