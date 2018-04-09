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

**Blocks**: a set of reservable units (rooms).   Currently, only one block is supported and all reservations are assumed to be for that block.
* `Capacity` is the physical number of reservable units
* `Overbooking percent` indicates the number of reservations beyond the capacity

**Reservations**: bookings of units without exceeding capacity plus allowed overbooking
* `Name`
* `Email`
* `Start time` is the arrival/check in time
* `End time` is the departure / check out time

Times are "unix epoch milliseconds" in UTC.   It is up to the front-end app to accept and present times as appropriate to the client/user, which is likely to be using the hotel's time zone.  The requester is responsible for adjusting times to the arrival / check in time-of-day and the departure / check out time-of-day.


##  Build and Deploy

Required software includes git, Java 8, and docker.  Dependencies will be downloaded by the build.

Clone the github...benjaminisi/keyprx (or download zip) 0and cd into the checkout directory
```bash
git clone https://github.com/benjaminisi/keyprx.git
# or using ssh
git clone git@github.com:benjaminisi/keyprx.git
```
Or in a container using the included Dockerfile.  Have localhost port 8087 available or specify a different port mapping in docker/docker-compose.yml
```bash
cd keyprx
bash ./gradlew build
cd docker
docker build -t keyprx:latest .
docker-compose up
# and in another shell
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

The unit tests are runnable in the IDE (I use IntelliJ).   Find them under src/test/unit/java.  They were helpful in correcting my interpreation of the challenge.  

Given more time, I would supply the command line invocation of the unit tests that run without attempting to connect to the Postgres database.   I would also supply integration/functional automated tests.

## Algorithm notes

Checking that a requested reservation does not exceed capacity is done as follows.
* fetch all reservations that are contained in or overlap the requested reservation
* moving forward in time, track the number of concurrent reservations at each reservations start time -- add one and subtract all 'live ones' that ended before this one's start
* return as soon as the capacity is reached

This algorithm can be optimized in many imaginable ways.   Optimization should be driven by experience.  For example, are there many more inquiries than changes to reservations?  Is it really common to be near capacity?  Do we need to support multiple servers for high availability or scale out?

## Future directions

* Externalize configuration such as Postgres user/password and db name

* Secure database access with network partitioning and by making passwords randomized.  Best practices would make the passwords not retrievable by the likes of
 ```bash
 $ docker run --rm -it test sh echo \$ADMIN_USER
 ```
* Test performance and scaling.  Optimize the algorithm and add caching based on ratio of lookups and saves/updates 

* Index startTime and endTime in the database

* Extract an interface for the ReservationHelper service

* Impose an API Gateway that logs accesses.  Nginx and others can also offload SSL encryption and static file serving from the application.

* Add logging to aid in client diagnosis.  Bad inputs that return 400 log at warn or info level to keep ERROR level spam out of the logs and monitor alerts

* Either remove PUT/PATCH for Reservations or fix validation to allow for reusing the existing reservation in filling the modified reservation. 

* Configure the resource's JSON keys to conform to snake case if this is the local convention: "overbooking_percent"

* Add Functional/Integration tests

* Add more embedded API documentation that is exposed by swagger/Open API and add comments on all public methods

* Support reservations for more than one block.  A reservation would need to identify the intended block.  Filter reservation searches by block id

* Support multiple tenants -- isolated sets of data, usually by client business.  Add in API via authentication token, header, query parameter, or URI path (in that order of my preference)

* Support hierarchies of blocks.  For example, fancier rooms can be reserved as overflow for lower-cost rooms (maybe only for near-term reservations)

* Secure for use on the public Internet by implementing OWASP Top 10 and adding user management, authentication, and Authorization ()through services, maybe microservices).

* Add a UI

* Add administrative operations such as flagging reservations by date range and operating on them as a group.  This would be needed, for example, if the hotel had a shutdown

* Hold a reservation while a potential customer thinks it over.  This also facilitates transactionality among clustered reservation servers.

* Add continuous integration and minimize deployment steps to approach one-button deploys

* Support devops with a health check endpoint, release notes, deployment instructions (if not pushbutton), and mitigation instructions

* Support analytics

* Support BI, say capacity planning and marketing

* Reject or warn when a decrease in capacity or overbooking percent causes a violoation with existing reservations

* Parallelize and/or cache the block fetch in the Create Reservation validator; do appropriate cache invalidation when clustering

* Externalize SQL strings.  Never concatenate pieces to make a SQL string

* Trap outgoing 5xx error responses to log in detail while avoiding exposing internals to outsiders

##  Technical discussion

The reservation helper service layer can grow to support horizontal scale-out through a managed shared cache.

Spring Data REST exposes error responses out to the http client by default.  The service layer would be expanded to wrap most of the persistence layer if this program were to support more internal consumers of reservations.   As a microservice supporting a front-end service, the current implementation is okay in this regard.

