# การเปิดใช้งานแอพพลิเคชันผ่าน docker container

---

## ข้อกำหนดเบื้องต้น

- ติดตั้ง **Docker Desktop** แล้ว
- มีไฟล์ `docker-compose.yml` และไฟล์ backup `SampleDB.bak` ในโฟลเดอร์โปรเจค

---

## การเปิดใช้งานแอพ

1. **เปิด Docker Desktop**

   - เปิดโปรแกรม Docker Desktop และรอจนกว่าจะพร้อมใช้งาน (สถานะเป็นสีเขียว)

2. **เปิด Terminal**

   - เปิด PowerShell หรือ Command Prompt (cmd)

   > ⚠️ **หมายเหตุ:** ห้ามใช้ Git Bash เพราะอาจทำให้คำสั่งไม่ทำงาน

3. **เข้าไปที่โฟลเดอร์โปรเจค**

   ```bash
   cd path/to/your/project
   ```
   ตัวอย่าง:
   ```bash
   cd C:\Users\YourName\Documents\MyProject
   ```

4. **build image**

   ```bash
   docker-compose build
   ```
   
5. **เปิดใช้งาน container แอพฯโดยจะรัน mssql + springboot ใน container**

   ```bash
   docker-compose up -d
   ```

6. **กู้คืน seed ข้อมูลสำหรับฐานข้อมูลที่จะใช้ในโปรเจค**

   ```bash
   docker exec -it mssql-server /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -Q "RESTORE DATABASE [SampleDB] FROM DISK = '/backups/SampleDB.bak' WITH MOVE 'SampleDB' TO '/var/opt/mssql/data/SampleDB.mdf', MOVE 'SampleDB_log' TO '/var/opt/mssql/data/SampleDB_log.ldf', REPLACE"
   ```
   - หากเห็นข้อความว่า restore สำเร็จ แสดงว่าพร้อมใช้งานแล้ว!  

7. **ใช้งานได้ที่ localhost:8080**

## การแก้ปัญหาเบื้องต้น

- **ปัญหา: Docker’s build cache corrupted**

  ```bash
   docker builder prune -af
   ```
