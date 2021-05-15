# Druid Coding challenge - analyzing user sessions

## What we are doing?
The aim of the work to solve for coding challenge presented as part of interview process at Imply. The details of the coding challenge can be found here
https://gist.github.com/gianm/bfa7e4abebfb0da6346b

## Solution Proposed
The coding challenge presents with a large file with the schema \
timestamp:Path:userID
 
The aim is write out a program that scan through the log file of similar schema, of any size and presents output of paths along with their userid which have been  alteast visited N times by a user.

### Approach
The challenge is attempted in java as a Maven project. An in-memory data structure of form hashtable is maintained as records from the file are read into asynchronously. The hard table keys are user id and values are set of unique file paths visited by these user ids. 

The file is read using asynchronous nio library where is link to the stream of records is obtained. These streams is processed line by line reading the records into in memmory hash table called "storage". As file could grow very large, maintaining this in memory could be inefficient. Priordically, hash table size is checked against the memory consumed and is dumped onto underlying file storage. The design of file storage is done keeping in mind

- Fast random access to the file system having individual files of userid, having userid as the file-name and contents as the paths. This represents the in-memory hash table on disk. The updates are faster too as small size files are accessed.
- Accessing larger files before smaller, thus ensuring getting records which would meet the criteria of atleast N first

The flow of code runs as following
- Read a line from file
- Add it to the in-memory hash table with userid as key and value as Set of unique paths
- During addition to the in-memory storage, check if we are out of memory. If yes, then dump records onto disk in a temprory folder on disk. Each entry in the hash table has its own file and values.
- Once whole file is read and processed, the output is summarized
-- if in memory storage was dumped, the program goes over the list of files in the temp files, sorts them with size and presents a list. 
-- The files are read from the list, checked the number of records, if records are more than threshold them file is read and output is dumped.
-- The temprory storage is cleans before exitig

### How to test
* Git clone the project
* cd src; mvn clean compile assembly:single
* To execute
  * java -jar ./target/takehometest-1.0-jar-with-dependencies.jar \
    Program reads values from a large file and find userid of the  N distinct paths. \
    FileEngine -f <filePath> -n <minimum no of rows> -o <out directory>
  * for e.g. java -jar ./target/takehometest-1.0-jar-with-dependencies.jar -f /Users/VK/takehometest/access.log -n 10 -o /tmp/ \
    Check for file under /tmp/ -> output.log
 ```
 UserID: 36407 File Paths: [/item/72820, /item/46820, /item/69298, /item/85655, /item/22836, /item/67525, /item/68306, /item/79568, /item/25487, /item/73350, /item/10021, /item/42545]
UserID: 36408 File Paths: [/item/95512, /item/24623, /item/92043, /item/63295, /item/42157, /item/82217, /item/37317, /item/13704, /item/83574, /item/78765, /item/90961, /item/18840, /item/19745, /item/33248]
 ```
### Other Comments
#### Alternative Approach
 The key challenge in the approach is the speed of processing input file and in-memory usage. Reading a large file sequentially can be very time consuming thus an approach which can read parts of the file parallelly would help. The processing of the file, can be designed in phases where
 * The first phase reads thru parts of the file and creates small sets of files, with name as userid. Since the file is processed parallely, a file is never processed by two threads/programs. In situations two threads/precess find entries for same userid, a duplicated file with additional prefix (_#) is created. This ensures file is always processed by one process and not by two.
 * The second phases runs thru the list of the files and merges the files with same prefix i.e userid.
 * The third phase opens these files sequentially and summarizes the output.
#### Further Improvements
 * High speed storage for e.g. NvME flash disk would help in file processing.
 * Input file stored across cluster can help in processing chunks of data in parallel
 * The itermediate files can be replaced with key/value storage (flash disk) or key/value db over commodity storage can help speed up things faster
