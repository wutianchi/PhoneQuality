SELECT * FROM PQ_GEO_LOCATION;

SELECT * FROM PQ_SPEED_TARGET;

SELECT * FROM PQ_SPEED_RESULT;

SELECT * FROM PQ_WIFI_RESULT;

SELECT datetime(1399793050910/1000, 'unixepoch', 'localtime');

SELECT * FROM PQ_CALL_STRUCTURE;

SELECT
  (SELECT count(id) FROM PQ_CALL_STRUCTURE WHERE strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')
                                              AND CAUSE in ('OUT_OF_SERVICE')) as dropcall,
  (SELECT count(id) FROM PQ_CALL_STRUCTURE WHERE strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as calls