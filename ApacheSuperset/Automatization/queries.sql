-- Chart: number of charts

SELECT count(watch_id) AS "COUNT(watch_id)"
FROM session_data
LIMIT 50000;


-- Chart: HRByTime


SELECT timestamp/60 AS timestamp,
                 sum(hr) AS "SUM(hr)",
                 60 AS "60"
FROM
  (SELECT TIMESTAMP, hr,
                     watch_id
   FROM session_data
   JOIN heart_rate_measurement ON session_data.id = heart_rate_measurement.session_id
   where hr_status = 1
     and session_id = 1) AS virtual_table
GROUP BY timestamp/60
ORDER BY "SUM(hr)" DESC



-- Affects


SELECT affect AS affect,
       count(affect) AS "COUNT(affect)"
FROM label_data
GROUP BY affect
ORDER BY "COUNT(affect)" DESC


-- duration

SELECT sum(duration) AS "SUM(duration)"
FROM session_data