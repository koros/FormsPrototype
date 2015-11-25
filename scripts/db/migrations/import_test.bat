ECHO Starting Import


mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_test -v < ".\exports\app_config.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_test -v < ".\exports\users.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_test -v < ".\exports\users_update_hashes.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_test -v < ".\exports\genres.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_test -v < ".\exports\slider_position_labels.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_test -v < ".\exports\slider_position_values.sql"
rem mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_test -v < ".\exports\community_sync_test_data.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\moods.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\eras.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\administrators.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\administrators_update_hashes.sql"

ECHO Import Completed
