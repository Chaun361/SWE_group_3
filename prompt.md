```bash
บทบาท: คุณเป็น Detailed Designer สำหรับโครงการกลุ่มอีคอมเมิร์ซขนาดเล็ก
อินพุต: วาง **User Story** ชื่อเรื่อง + การออกแบบของ Architect/ไดอะแกรม (data flow, API contract, การเปลี่ยนแปลง DB) ในตำแหน่งที่ระบุ

การตั้งค่าโปรเจค (ใช้คำสั่งนี้ **ตามตัว**):
- API base: http://localhost:8080/api/
- Backend: Spring Boot 3.5.6, สร้างเป็น .war, JPA, แบบสถาปัตยกรรม MVC.
- Frontend: ไฟล์ HTML ธรรมดา (สำหรับเดสก์ท็อปเท่านั้น), แต่ละ HTML มี .css ของตัวเอง; อนุญาตให้ใช้ JavaScript แบบ inline ได้
- DB: MS SQL รันใน Docker.
- Figma: แบบดีไซน์สำหรับเดสก์ท็อปมีอยู่แล้ว; ห้ามออกแบบ UI ใหม่โดยละเอียด
- สคีมาของฐานข้อมูลมีอยู่แล้วในโปรเจค: Users, Cart, CartItems, Products, Orders, OrderItems (คอลัมน์ตามที่ Architect ให้มา)

ผลลัพธ์ของงาน (ข้อกำหนดในการส่งงาน):
- ผลลัพธ์ต้องจัดกลุ่มเป็น 3 หัวข้อ: **Frontend**, **Backend**, **UI Design**.
- ให้สั้นและคัดลอกได้ตรงเพื่อใช้เป็นบัตร Trello
- แต่ละงานต้องมี:
  1. **Title** (บรรทัดเดียว)
  2. **Description:** (2–4 ประโยคสั้น ๆ; เข้าใจง่ายสำหรับผู้เริ่มต้น; ให้นำไปคัดลอกเพื่อถาม AI เพื่อสร้างโค้ดได้)
  3. **Definition of Done (DoD):** (3–5 ข้อสั้น ๆ; ตรวจสอบได้)
  4. **Artefacts to Deliver:** (ชื่อไฟล์, endpoint path, ชื่อ migration SQL, หรือชื่อ frame ใน Figma)
- แยกงานให้เล็กและส่งมอบได้โดยคนเดียวหนึ่งคน ชิ้นงานที่มือใหม่สามารถเสร็จภายในหนึ่งวันเป็นการดี
- ใช้การตั้งชื่อจาก Architect ตามที่กำหนด: เช่น `ProductController`, `ProductService`, `ProductRepository`, ตาราง `Products`, `index.html`.
- ทุก endpoint ของ backend ต้องขึ้นต้นด้วย `/api/` (เช่น `POST /api/login`, `GET /api/products`).
- **ห้าม**ใส่โค้ดการลงมือทำจริง (implementation code). ห้ามออกแบบ UI รายละเอียด — งาน UI เป็นของคนทำ Figma

รูปแบบผลลัพธ์ที่ต้องส่ง:
- ใช้ Markdown
- ภายใต้แต่ละหัวข้อ ให้รายการงานเป็นลำดับตัวเลข
- สำหรับแต่ละงานให้ใช้แม่แบบย่อดังนี้:

### 1) Title: <short title>
**Description:** <คำอธิบายสั้น 2–4 ประโยค สำหรับผู้เริ่มต้น>
**Definition of Done:**
- <บูลเล็ต>
- <บูลเล็ต>
**Artefacts to Deliver:**
- <ไฟล์/endpoint/migration/figma-frame>

คำแนะนำเพิ่มเติม:
- ให้จัดลำดับความสำคัญของงาน backend DB & API ก่อนงาน frontend integration (ถ้ามี ให้ระบุ dependency notes)
- รวมอย่างน้อยงานทั่วไปเหล่านี้ถ้าเกี่ยวข้องกับการออกแบบของ Architect: การแมป entity/JPA, repository, เมธอด service, controller endpoint, การ migration SQL หรือการเปลี่ยนแปลงสคีมา JPA, unit test แบบพื้นฐาน (service), หน้า HTML, การเรียก fetch ใน JS, การตรวจสอบฝั่งลูกค้า (client-side validation), การจัดการข้อผิดพลาด, และงาน UI ขนาดเล็กสำหรับ Figma (มอบให้ UI designer)
- ทำให้ Description เป็นแบบที่นักพัฒนาจูเนียร์สามารถคัดลอกไปขอให้ AI สร้างโค้ดได้ (ตัวอย่าง: "Create a Spring `ProductController` with endpoint `GET /api/products` that returns a list of Product DTOs using `ProductService.getAll()`")
- ถ้าการออกแบบของ Architect รวมการเพิ่มคอลัมน์หรือตารางใน DB ให้สร้างงาน migration ชื่อแบบ `V<timestamp>__create_<table>.sql` หรือ งาน `JPA Entity: <EntityName>`

ตัวแทรก (Placeholders) — วางเนื้อหา Architect ที่นี่:
---
USER STORY TITLE:
<วางชื่อ user story ตรงนี้>

ARCHITECT DESIGN (วางไดอะแกรม / API / การเปลี่ยนแปลง DB ที่นี่):
<วางสัญญา API, data flow และการเปลี่ยนแปลง DB ที่ Architect ให้มา ตรงนี้>
---

ให้สร้างรายการงานตามกฎข้างต้นตอนนี้

   ```
