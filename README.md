# highload-architect-homework (indexes)

## Requirements

- Docker
- Docker Compose

## Run

```shell
docker-compose up
```

## Test

Use postman collection HIGHLOAD-ARCHITECT-HOMEWORK.postman_collection.json to execute HTTP requests.

## Searching user by first name and last name

### HTTP Request

```http request
GET http://localhost:8080/users?firstName=Li&lastName=Ad
Authorization: Basic ZWxvbjcxOnNlY3JldA==
```

### SQL Query

```sql
select u.username,
       u.first_name,
       u.last_name,
       u.sex,
       u.age,
       u.city,
       i.description as interest
from user u
         left join interest i on u.username = i.username
where u.first_name like 'Li%'
  and u.last_name like 'Ad%'
order by u.username, i.id
```

### Load testing

#### Properties

```
Number of threads: 90
Ramp-up period: 2700 sec
1 thread starts each 30 sec
```

#### Without index

##### Query plan

```
-> Nested loop left join  (cost=140413.83 rows=30473) (actual time=217.424..339.305 rows=321 loops=1)
    -> Filter: ((u.first_name like 'Li%') and (u.last_name like 'Ad%'))  (cost=107327.17 rows=13019) (actual time=217.386..338.586 rows=149 loops=1)
        -> Table scan on u  (cost=107327.17 rows=1054719) (actual time=0.052..273.154 rows=1000001 loops=1)
    -> Index lookup on i using username (username=u.username)  (cost=2.31 rows=2) (actual time=0.004..0.005 rows=2 loops=149)
```

##### Latency

![Latency Vs Threads Without Index](images/LatencyVsThreads_without_index.png "Latency")

##### Throughput

![Throughput Vs Time Without Index](images/ThroughputVsTime_without_index.png "Throughput")

#### With index

##### Index definition

```sql
alter table user
    add index user_first_name_last_name_idx (first_name, last_name);
```

##### Query plan

```
-> Nested loop left join  (cost=36867.37 rows=86856) (actual time=1.215..4.665 rows=321 loops=1)
    -> Index range scan on u using user_first_name_last_name_idx over ('Li' <= first_name <= 'Li􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿' AND 'Ad' <= last_name <= 'Ad􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿􏿿'), with index condition: ((u.first_name like 'Li%') and (u.last_name like 'Ad%'))  (cost=18668.34 rows=37106) (actual time=1.202..3.844 rows=149 loops=1)
    -> Index lookup on i using username (username=u.username)  (cost=2.31 rows=2) (actual time=0.005..0.005 rows=2 loops=149)
```

##### Latency

![Latency Vs Threads With Index](images/LatencyVsThreads_with_index.png "Latency")

##### Throughput

![Throughput Vs Time With Index](images/ThroughputVsTime_with_index.png "Throughput")

#### Explanation

##### Критерии выбора индекса

Поиск осуществляется по полям first_name, last_name одновременно, поэтому оба поля есть в индексе.
Порядок полей такой из-за особенностей сгенерированных данных - столбец first_name имеет большую селективность, т.к. количество уникальных значений в нём больше, чем в столбце last_name.

##### Улучшение latency и throughput

По соответствующим графикам видно, что после добавления индекса улучшились показатели: latency ~в 3.3 раза (2с без индекса, 0.6с с индексом при 10 потоках), throughput ~в 4 раза (4 запроса в секунду без индекса, 16 запросов в секунду с индексом).
