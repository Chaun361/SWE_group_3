#!/bin/bash

# Function to wait for SQL Server
wait_for_sql() {
    echo "Waiting for SQL Server to start..."
    for i in {1..50}; do
        if /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -Q "SELECT 1" &> /dev/null; then
            echo "SQL Server is ready!"
            return 0
        fi
        echo "Waiting for SQL Server to start... ($i/50)"
        sleep 2
    done
    echo "ERROR: SQL Server failed to start within the expected time."
    return 1
}

# Function to check if database exists
database_exists() {
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -Q "SELECT name FROM sys.databases WHERE name = 'SampleDB'" | grep -q "SampleDB"
}

# Function to restore database
restore_database() {
    echo "Starting database restore..."
    
    # Check if backup file exists
    if [ ! -f "/backups/SampleDB.bak" ]; then
        echo "ERROR: Backup file not found: /backups/SampleDB.bak"
        echo "Available files in /backups:"
        ls -la /backups/ 2>/dev/null || echo "No backups directory found"
        return 1
    fi

    echo "Backup file found: /backups/SampleDB.bak"
    
    # Try to restore with the exact command that worked manually
    echo "Attempting database restore..."
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -Q "
    RESTORE DATABASE [SampleDB] 
    FROM DISK = '/backups/SampleDB.bak' 
    WITH 
        MOVE 'SampleDB' TO '/var/opt/mssql/data/SampleDB.mdf',
        MOVE 'SampleDB_log' TO '/var/opt/mssql/data/SampleDB_log.ldf',
        REPLACE,
        STATS = 5
    "
    
    local restore_status=$?
    
    if [ $restore_status -eq 0 ]; then
        echo "SUCCESS: Database restored successfully!"
        
        # Verify the database is accessible
        echo "Verifying database access..."
        /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -d "SampleDB" -Q "SELECT 'Database is accessible' as Status"
        
        if [ $? -eq 0 ]; then
            echo "SUCCESS: Database verification passed!"
        else
            echo "WARNING: Database restored but verification failed"
        fi
        
        return 0
    else
        echo "ERROR: Database restore failed with status: $restore_status"
        
        # Try to get more info about the backup file
        echo "Attempting to read backup file information..."
        /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -Q "RESTORE FILELISTONLY FROM DISK = '/backups/SampleDB.bak'" || echo "Failed to read backup file info"
        
        return 1
    fi
}

# Main execution
echo "Starting SQL Server with database restore..."

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &

# Wait for SQL Server to be ready
if wait_for_sql; then
    echo "Checking if SampleDB exists..."
    if database_exists; then
        echo "SampleDB already exists, skipping restore."
        
        # Verify the existing database is accessible
        echo "Verifying existing database access..."
        /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -d "SampleDB" -Q "SELECT 'Database is accessible' as Status"
        
        if [ $? -ne 0 ]; then
            echo "WARNING: Existing database is not accessible"
        fi
    else
        echo "SampleDB not found, attempting restore..."
        restore_database
    fi
else
    echo "FATAL: Failed to connect to SQL Server, cannot restore database."
    exit 1
fi

# Keep container running
echo "SQL Server initialization complete. Container is running."
wait