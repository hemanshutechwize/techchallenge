# mangalamInfo

# How to run this app locally?

To try this appication in IntelliJ,

1. pass the file name "application.properties" as a run time parameter to job in the Edit Configurations section
2. run com.mangalam.techchallenge.DataPipeline object.
3. There are multiple parameters in the application properties which will help tweak the behaviour of the job.


# About the Application

This application helps read the data from the below mentioned REST API on incremental basis and loads the data into the local MySQl database
and upload data to the Amazon AWS S3 bucket on daily basis as parquet format. In every 24 hours the process is going to repeat itself .


REST API:
---------
SEARCH :
https://bikeindex.org/documentation/api_v3#!/search/GET_version_search_format_get_0   

BIKE :
https://bikeindex.org/documentation/api_v3#!/bikes/GET_version_bikes_id_format_get_0

Installation :
---------
Clone this repository:
    '''
    git clone https://drive.google.com/file/d/11IauFbVW6qH2oCqEW0irSnZnLZe3P8L4/view?usp=share_link
    '''

Navigate to the project directory:
    '''
    cd MangalamTechChallenge/
    '''
Build the project using SBT.

CONFIGURATIONS :
---------

    # SPARK CONFIGURATIONS

    spark.master=local[1]
    spark.dynamicAllocation.enabled=true
    spark.sql.shuffle.partitions=20
    spark.debug.maxToStringFields=100
    spark.driver.memory=1g
    spark.executor.cores=1
    spark.executor.instances=1
    spark.executor.memory=1g

    # APPLICATION CONFIGURATIONS

    output.json.path=src/output/apiResponse/
    output.data.path=src/output/$DATE$
    
    # DATABASE CONFIGURATION
    
    database.url=jdbc:mysql://localhost:3306/<Schema Name>
    database.schema=<Schema Name>
    database.log.table=<Table Name>
    database.username=XXX
    database.password=XXX
    
    # AWS S3 CONFIGURATION
    
    aws.accesskey=XXX
    aws.secretkey=XXX
    aws.filesystem=s3.amazonaws.com
    aws.bucket=<Bucket Name>
    aws.flag=true
    
    # Time in millisecond 24 hrs
    # 86400000
    ingestion.time=86400000
    
    # Spark repartition
    
    num.repartition=1
    
    # Incidents Parameters
    
    incidents.url=https://bikeindex.org:443/api/v3/search?page=$TOTAL_PAGES$&per_page=$PER_PAGE$&location=$LOCATION$&distance=$DISTANCE$&stolenness=$STOLEN$
    bikers.url=https://bikeindex.org:443/api/v3/bikes/$BIKER_ID$
    incidents.total_page=4
    incidents.per_page=2
    incidents.location=IP
    incidents.distance=10
    incidents.stolenness=stolen

Functionality:
----------------------
1. Fetch data : Based on the below above properties in applicationProperties file,
    the job would get the API URLs and fetch the data in parallel. 


2. Fetch incrementally: Based on the below properties, the job will incrementally get the data in every run.
    -> Note that if the job is running for the first time in your local,
        it will use "currentDate" as initial datetime.
    -> After the first run, the job will store the datetime in MySQL and the date will be validated accordingly in 
    (this could be any relational DB. used MySQL). This will be used by further runs to incrementally pull the data.
       -> Every subsequent run will increment the datetime . 


3. Data Persisted: Parses the data fetched from API.
    To obtain all the information about bikers or bikes, we will first call the Search API.

    After that, we'll use the bike API, which will provide information on the bike ids we obtained via the search API.
    
   

4. Storing Data :  As we get the response we will convert it into parquet format . The functionality will fetch the records 
    and store it in folder called output and the file that are going to be created will be of the date name and in .json format .


5. AWS S3 : Once the data has been ingested into the database the files are going to be uploaded on the AWS s3 bucket on daily basis and date as their name
and the format of the files woule be parquet format .


6. Snapshot : The Snapshot is going to store the backup of each time data has been ingested into the MySQL database table.
    logs of the dataingestion is going to store over here when the last time data was ingested and there would be a date Validation too 
    for this process .


# Possible Test Cases 



The test source is available under src/test/scala .

To run all the tests, run
```
test 
```
Test Cases:

    1.Api_status_code_200_Incidents :This test case will determine whether or not the parameters we are sending to the search API are accurate.

    2.Api_status_code_200_Bikers : Verify whether the response's status code is 200 or not; if not, the test case will fail.


