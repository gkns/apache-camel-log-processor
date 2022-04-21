Problem
---
A company has a business process that requires processing of text files from external
customers. Some information about the text file:
* Each line of the text file is an individual record and can be processed
independently of the other records in the text file
* The size of the file can range from a few KB to a few GB
* The lines are ordered by the Date field in ascending order.

Format of each line in the text file:
---
[Date in YYYY-MM-DDThh:mm:ssZ Format][space][Email Address][space][Session Id in GUID format]

Eg:
--
2020-12-04T11:14:23Z jasdfne.dsdfoe@esdfmsdfail.com 3f30eb2b-a637-4c93-a633-b4567bc374576

Write an application to parse the text files above and expose an HTTP API to
serve the data contained in the text files.

1. expose an HTTP POST route on the / path
2. accept iso8601 UTC timestamps only
3. The array must be ordered by eventTime from earliest to latest
4. The ordering of the keys within the JSON objects does not matter

Input
---
`curl -XPOST localhost:8279/ -d &#39;{"filename":"sample1.txt",
"from":"2021-07-06T23:00:00Z", "to": "2020-07-06T23:00:00Z"}&#39;`

Output
---
```
[{
      "eventTime": "2000-01-01T03:05:58Z",
      "email": "test123@test.com",
      "sessionId": "97994694-ea5c-4da7-a4bb-d6423321ccd0"
   },
   {
      "eventTime": "2000-01-01T04:05:58Z",
      "email": "test456@test.com",
      "sessionId": "97994694-ea5c-4da7-a4bb-d6423321ccd1"
   }
]
```


Design
---

**On a high level, we have two main tasks in this application:**

1. Injesting data from the text files and store it in a structured format, for easy querying.
2. Expose HTTP endpoint for accessing the injested data.

[1] Is taken care in this preliminary implementation in a more robust way, In that:

- Incoming data-files are processed in a continuous and memory efficient way (streaming)
- Data files can be added when the application is running, and they will be processed and added to the DB.
- New record formats can be easily plugged using Camel components.
- New data sources (other than file) can be easily plugged.
- The embedded DB is located at the directory named: **derby_data_dir** in the working directory.

[2] Can be optimized, explained in : **Why Derby embedded DB**

Assumptions:
---
Due to the nature of the problem, This implementation is a reduced scope implementation, in that, there are multiple
possible optimizations.

**Why use Apache camel and a DB and not something from scratch:**

1. Camel is purpose built for applications like this, Where data needs to flow from one system to another, including
   file watching+parsing+transforming.

**Why Java+SpringBoot**

1. Springboot, gets us easy server+API and especially Camel and DB support.
2. Reduce the amount of custom code, which will minimize testing effort.
3. Fastest MVP (minimum viable product), since all solution components needed for this problem are pre-built.

**About the correctness of output:**
The correctness was verified manually with a reduced dataset. Also the timestamp used is natively supported by the
database. (Derby TIMESTAMP type)

**File IO based search is eliminated because:**

1. Consumes a lot of time for each API call, in repeated work.
2. We cannot make use of the timestamp format for effective querying, unless the data is in-memory.
3. If we preprocess files, create index and search using binary/n-ary search, it is similar to the strategy used in
   databases.

**Why Derby embedded DB:**
Ideally for range queries, we should go with a timeseries DB, or DBs which have efficient implementations supporting
range queries. Mongo DB could be used, But this will mean a separate service, avoiding in this preliminary
implementation.

**Additional Notes:**

1. There is an extra field (Id) in the API result. (Introduced after the screenshots were captured.)
   This is the primary key of the table, (Instead of the earlier SesionID GUID).
2. Dockerfile and dockerization in this project is a basic one, uses the fat jar of the springboot application.

How to scale this:
---
**Choice of database:**
Derby is not the right choice of database. For existing implementation, We can split the database at a million record or
such, But ideally we will run a timeseries database and make camel-route output data into that.
**Load balancing:**
Use camel's in-built loadbalancing component to direct messages to different DB instances.

**Known improvements:**

1. check if Camel is currently processing, on API call, If result not found respond with an appropriate error advising
   retry after sometime.
2. Change to an appropriate database.
3. Use an application level logic for partitioning data to DB instances.
4. Use of a cache for saving recent range queries, and query DB only for new ranges.
5. Implement pagination for the REST API, so that large ranges wouldn't time out/go over HTTP body size limitations.
6. File based logging for easier debugging, relatively very easy, due to springboot.
7. Mid-way in the implmentation, to speed-up DB insertions, I changed to Batch update mode, so one of the Camel
   processor is unused from the original design.
8. Use prepared statements for SQL in the record search implementation:
   SessionDataRepositoryImpl.findSessionsInTimeRange() to mitigate SQL injection.
9. API spec is currently non-standard, because only one API is there., We should use a swagger spec, so we have
   documentation too without much efforts.
10. Although implemented in Java-17, not really using much of its new features, we can use Records for some classes.
11. Lot of negative test-cases in the UTs.

Implementation specifics:
---

* This is a springboot web application.
* The meat of the solution is in FileRoute.java.
* File component of apache camel library is used for injesting the data from text files.
* Camel runs continuously in a multi-threaded mode, injesting data from the files placed under **input_files_folder** in
  application.properties, and unmarshall each record-line from the text file to a model and diverts this structured data
  to the DB.
* Unmarshalling is aided by the Camel Bindy Data format, which readily parses the records.
* Camel uses multiple threads (no. of avaliable cores on the system).
* There is an index created on the "eventTime" column to speed up range queries.
* To speed-up data insertion into the table, Spring JDBC batch update used.
* Tested insertion of 3 million records (takes around ~4 mins).
* To avoid some boilerplate code, Lombok java library was used.

How to run this:
---
Remember to set the property: **input_files_folder** in **application.properties**
Or add the command-line parameter: -Dspring-boot.run.arguments=--input_files_folder=/input/files/path/

to an appropriate directory in your system containing the sample\*.txt which contains the recoreds in the given format:
Eg: `2000-01-01T17:25:49Z dsfgedsdfrisdfgc_strdfgosin@adsfgdamsdfgs.co.uk dfgdfg-dfgdfg-3425fgh-ghgh-dfghdfgh4345`

**Run command:**

`./mvnw spring-boot:run <-Dspring-boot.run.arguments=--input_files_folder=/location-of-input-text-files/>`

**If using docker (testing pending)**
`docker run -p8080:8080 app:latest'`

**Note: ** First run might take time, to download dependencies, build and processing the text data.