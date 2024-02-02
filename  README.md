# Database: Horizontal Sharding

Compare performance of 3 cases ( without sharding, FDW, and approach of your choice )
* Setup horizontal/vertical sharding as it’s described in this lesson and with alternative tool ( citus, pgpool-|| postgres-xl )
* Insert 1 000 000 rows into books
* Do the same without sharding
  
Measure performance for reads and writes

## Testing

Init scripts:
* Horizontal Sharding:
  * for main server [fdw_main_server.sql](scripts%2Ffdw_main_server.sql)
  * shard 1 [fdw_shard_server1.sql](scripts%2Ffdw_shard_server1.sql)
  * shard 2 [fdw_shard_server2.sql](scripts%2Ffdw_shard_server2.sql)
* Without sharding [postgresql_single.sql](scripts%2Fpostgresql_single.sql)
* Citus with 2 workers [citus_master.sql](scripts%2Fcitus_master.sql)


category_id - integer field with values in range from 1 to 10000


#### Citus after configuration
```sql
postgres=# SELECT master_get_active_worker_nodes();
      master_get_active_worker_nodes      
------------------------------------------
 (database-sharding-citus-worker1-1,5432)
 (database-sharding-citus-worker2-1,5432)
(2 rows)
```

```sql
postgres=# select * from citus_shards;
table_name | shardid |  shard_name  | citus_table_type | colocation_id |             nodename              | nodeport | shard_size 
------------+---------+--------------+------------------+---------------+-----------------------------------+----------+------------
 books      |  102008 | books_102008 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2801664
 books      |  102009 | books_102009 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    8945664
 books      |  102010 | books_102010 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2572288
 books      |  102011 | books_102011 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2793472
 books      |  102012 | books_102012 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2768896
 books      |  102013 | books_102013 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2785280
 books      |  102014 | books_102014 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2777088
 books      |  102015 | books_102015 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2916352
 books      |  102016 | books_102016 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2875392
 books      |  102017 | books_102017 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2908160
 books      |  102018 | books_102018 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2818048
 books      |  102019 | books_102019 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2850816
 books      |  102020 | books_102020 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2703360
 books      |  102021 | books_102021 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2809856
 books      |  102022 | books_102022 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2916352
 books      |  102023 | books_102023 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2850816
 books      |  102024 | books_102024 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2818048
 books      |  102025 | books_102025 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2777088
 books      |  102026 | books_102026 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    3039232
 books      |  102027 | books_102027 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2736128
 books      |  102028 | books_102028 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2719744
 books      |  102029 | books_102029 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    3055616
 books      |  102030 | books_102030 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2949120
 books      |  102031 | books_102031 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2752512
 books      |  102032 | books_102032 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    8888320
 books      |  102033 | books_102033 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2646016
 books      |  102034 | books_102034 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2736128
 books      |  102035 | books_102035 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2850816
 books      |  102036 | books_102036 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2760704
 books      |  102037 | books_102037 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2801664
 books      |  102038 | books_102038 | distributed      |             2 | database-sharding-citus-worker1-1 |     5432 |    2760704
 books      |  102039 | books_102039 | distributed      |             2 | database-sharding-citus-worker2-1 |     5432 |    2777088

```

1000000 sequential inserts of books:
```
docker run app
```

### Results:

| operations                                          | without sharding | FDW        | citus      |
|-----------------------------------------------------|------------------|------------|------------|
| 1000000 inserts                                     | 1886995 ms       | 2717092 ms | 1811105 ms |
| select count(*) from books where category_id = 1235 | 65 ms            | 122 ms     | 99 ms      |