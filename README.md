การเปิดใช้งาน Database
คู่มือนี้จะช่วยให้คุณเปิดใช้งาน SQL Server Database ผ่าน Docker สำหรับการพัฒนาโปรเจค
ข้อกำหนดเบื้องต้น

ติดตั้ง Docker Desktop แล้ว
มีไฟล์ docker-compose.yml และไฟล์ backup SampleDB.bak ในโฟลเดอร์โปรเจค

ขั้นตอนการเปิดใช้งาน

1. เปิด Docker Desktop
   เปิดโปรแกรม Docker Desktop และรอจนกว่าจะพร้อมใช้งาน (สถานะเป็นสีเขียว)
2. เปิด Terminal
   เปิด PowerShell หรือ Command Prompt (cmd)

⚠️ หมายเหตุ: ห้ามใช้ Git Bash เพราะอาจทำให้คำสั่งไม่ทำงาน

3. เข้าไปที่โฟลเดอร์โปรเจค
   bashcd path/to/your/project
   ตัวอย่าง:
   bashcd C:\Users\YourName\Documents\MyProject
4. เริ่มต้น Docker Container
   รันคำสั่งเพื่อเปิดใช้งาน SQL Server:

   docker-compose up -d

   รอสักครู่จนกว่า container จะเริ่มทำงาน (ประมาณ 10-30 วินาที)

Server name: localhost,1433 หรือ localhost
Login: sa
Password: MyStrong!Pass123
Database name: SampleDB
ตัวอย่าง Connection String:
Server=localhost,1433;Database=SampleDB;User Id=sa;Password=MyStrong!Pass123;TrustServerCertificate=True;

การปิดการใช้งาน เมื่อต้องการปิด SQL Server:

bashdocker-compose down

หากต้องการลบข้อมูลทั้งหมดและเริ่มใหม่:

bashdocker-compose down -v

การแก้ปัญหาเบื้องต้น
ปัญหา: Port 1433 ถูกใช้งานอยู่แล้ว
ปิด SQL Server อื่นๆ ที่กำลังทำงานบนเครื่อง
หรือแก้ไข port ในไฟล์ docker-compose.yml

ปัญหา: Docker container ไม่ขึ้น
ตรวจสอบว่า Docker Desktop เปิดอยู่และทำงานปกติ
ลองรัน docker-compose down แล้วรัน docker-compose up -d ใหม่

ปัญหา: Connect ไม่ได้
รอสัก 10-30 วินาทีหลังรัน docker-compose up -d เพราะ SQL Server ต้องใช้เวลาเริ่มต้น
ตรวจสอบว่าใช้ข้อมูล login ถูกต้อง

💡 เคล็ดลับ: หลังจาก setup ครั้งแรกเสร็จแล้ว ครั้งต่อไปแค่เปิด Docker Desktop แล้วรัน docker-compose up -d ก็ใช้งานได้เลย!
