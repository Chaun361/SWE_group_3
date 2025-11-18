# การเปิดใช้งาน Database

คู่มือนี้จะช่วยให้คุณเปิดใช้งาน SQL Server Database ผ่าน Docker สำหรับการพัฒนาโปรเจค

---

## ข้อกำหนดเบื้องต้น

- ติดตั้ง **Docker Desktop** แล้ว
- มีไฟล์ `docker-compose.yml` และไฟล์ backup `SampleDB.bak` ในโฟลเดอร์โปรเจค

---

## คู่มือสำหรับนักพัฒนา (Frontend & Backend)

### สำหรับ Frontend Developer

- ให้นักพัฒนา Frontend เข้าไปทำงานในโฟลเดอร์ **`/demo/src/main/resources/static`**
- สามารถวางไฟล์ HTML, CSS หรือโปรเจค Frontend ของคุณในโฟลเดอร์นี้ได้

### สำหรับ Backend Developer

- ให้นักพัฒนา Backend ใช้โฟลเดอร์ **`/demo`** สำหรับสร้าง/วางโปรเจค Spring Boot
- สามารถวางโค้ด Backend ในโฟลเดอร์นี้ได้เลย

---

## ขั้นตอนการเปิดใช้งาน

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

4. **เริ่มต้น Docker Container**

   - รันคำสั่งเพื่อเปิดใช้งาน SQL Server:

     ```bash
     docker-compose up -d
     ```

   - รอสักครู่จนกว่า container จะเริ่มทำงาน (ประมาณ 10-30 วินาที)

5. **Restore ฐานข้อมูล (เฉพาะครั้งแรกเท่านั้น)**
   สำคัญ: รันคำสั่งนี้เฉพาะครั้งแรกที่ setup เท่านั้น หากเคย restore แล้ว ข้อมูลจะยังอยู่บนเครื่อง
   ```bash
   docker exec -it mssql-server /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "MyStrong!Pass123" -C -Q "RESTORE DATABASE [SampleDB] FROM DISK = '/backups/SampleDB.bak' WITH MOVE 'SampleDB' TO '/var/opt/mssql/data/SampleDB.mdf', MOVE 'SampleDB_log' TO '/var/opt/mssql/data/SampleDB_log.ldf', REPLACE"
   ```
   - หากเห็นข้อความว่า restore สำเร็จ แสดงว่าพร้อมใช้งานแล้ว!

ข้อมูลสำหรับ Connect เข้า Database
ใช้ข้อมูลด้านล่างนี้เมื่อต้องการเชื่อมต่อกับ SQL Server (เช่น ผ่าน SQL Server Management Studio)
---

### ข้อมูลสำหรับเชื่อมต่อ

- **Server name:** `localhost,1433` หรือ `localhost`
- **Login:** `sa`
- **Password:** `MyStrong!Pass123`
- **Database name:** `SampleDB`

**ตัวอย่าง Connection String:**

```
Server=localhost,1433;Database=SampleDB;User Id=sa;Password=MyStrong!Pass123;TrustServerCertificate=True;
```

---

## การปิดการใช้งาน

- เมื่อต้องการปิด SQL Server:

  ```bash
  docker-compose down
  ```

- หากต้องการลบข้อมูลทั้งหมดและเริ่มใหม่:

  ```bash
  docker-compose down -v
  ```

---

## การแก้ปัญหาเบื้องต้น

- **ปัญหา: Port 1433 ถูกใช้งานอยู่แล้ว**
  - ปิด SQL Server อื่นๆ ที่กำลังทำงานบนเครื่อง
  - หรือแก้ไข port ในไฟล์ `docker-compose.yml`

- **ปัญหา: Docker container ไม่ขึ้น**
  - ตรวจสอบว่า Docker Desktop เปิดอยู่และทำงานปกติ
  - ลองรัน `docker-compose down` แล้วรัน `docker-compose up -d` ใหม่

- **ปัญหา: Connect ไม่ได้**
  - รอสัก 10-30 วินาทีหลังรัน `docker-compose up -d` เพราะ SQL Server ต้องใช้เวลาเริ่มต้น
  - ตรวจสอบว่าใช้ข้อมูล login ถูกต้อง

---
