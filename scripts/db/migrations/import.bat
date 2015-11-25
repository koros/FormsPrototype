ECHO Starting Import

mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\app_config.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\users.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\users_update_hashes.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\genres.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\slider_position_labels.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\slider_position_values.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\community_sync_test_data.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\moods.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\eras.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\countries_data.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\states_data.sql" 
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\administrators.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\administrators_update_hashes.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\manufacturers.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\devices.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\delete_manufacturer.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\cellular_plans.sql"
mysql --host www.mysqldb.net --port 3306 -u rokmobile-dbuser -pa11iedtechnique -D rokmobile_development -v < ".\exports\verizon_devices.sql"



ECHO Import Completed
