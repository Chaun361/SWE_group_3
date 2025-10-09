# restore-db.sh
#!/bin/bash

echo "Starting database restore process..."

# Wait for SQL Server to be ready
for i in {1..50}; do
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P YourStrong@Password123 -C -Q "SELECT 1" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "SQL Server is ready!"
        break
    fi
    echo "Waiting for SQL Server to start... ($i/50)"
    sleep 2
done

# Find the .bak file
BAK_FILE=$(ls /var/opt/mssql/backups/*.bak 2>/dev/null | head -n 1)

if [ -z "$BAK_FILE" ]; then
    echo "No .bak file found in /var/opt/mssql/backups/"
    echo "Please place your SampleDB.bak file in the 'backups' folder"
    exit 0
fi

echo "Found backup file: $BAK_FILE"

# Get logical file names from backup
echo "Getting file information from backup..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P YourStrong@Password123 -C -Q "RESTORE FILELISTONLY FROM DISK = N'$BAK_FILE'" -o /tmp/filelist.txt

# Extract logical names (usually the first two entries)
DATA_FILE=$(grep -v "^-" /tmp/filelist.txt | grep -v "LogicalName" | head -n 1 | awk '{print $1}')
LOG_FILE=$(grep -v "^-" /tmp/filelist.txt | grep -v "LogicalName" | tail -n 1 | awk '{print $1}')

echo "Data file logical name: $DATA_FILE"
echo "Log file logical name: $LOG_FILE"

# Restore the database
echo "Restoring SampleDB..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P YourStrong@Password123 -C -Q "
RESTORE DATABASE [SampleDB] 
FROM DISK = N'$BAK_FILE' 
WITH 
    MOVE N'$DATA_FILE' TO N'/var/opt/mssql/data/SampleDB.mdf',
    MOVE N'$LOG_FILE' TO N'/var/opt/mssql/data/SampleDB_log.ldf',
    REPLACE
"

if [ $? -eq 0 ]; then
    echo "Database restored successfully!"
else
    echo "Database restore failed. Check the logs above."
fi