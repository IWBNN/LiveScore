<p align="middle">
  <img src="https://github.com/user-attachments/assets/063ff518-4d5c-4633-ad8d-bb65f8b3c834" alt="사진 헤더" width="600px"/>
</p>

<h1 align="middle">LiveScore</h1>
<h3 align="middle">체육대회 실시간 스트리밍 서비스</h3>

<br/>

## 📝 작품소개

본 서비스는 알기 어려운 체육대회 일정과 결과들을 일목요연하게 정리하고 체육대회를 하는 장소에 있지 않더라도 체육대회 현황을 실시간으로 확인할 수 있는 대진표와 실시간으로 소통할 수 있는 라이브 채팅, 댓글 기능, 그리고 현재 진행중인 대회와 과거에 한 대회를 스트리밍 하는 서비스를 제공합니다.

<br/>

## 🌁 프로젝트 배경

학교 체육대회는 참가자나 관람객들이 모든 정보를 실시간으로 파악하기 어려운 경우가 많습니다. 특히 대회 현장에서 경기를 직접 관람하지 않는 경우, 경기 진행 상황을 정확히 확인하기 힘들고, 대진표나 결과를 확인하기 위해 별도의 자료를 찾아야 하는 불편함이 존재합니다. 이러한 문제를 해결하기 위해 본 서비스는 체육대회의 일정과 결과를 효율적으로 관리하고, 실시간 대진표 및 경기 상황을 손쉽게 확인할 수 있도록 합니다. 이를 통해 사용자들은 현장에 있지 않아도 모든 경기를 실시간으로 스트리밍으로 즐길 수 있으며, 과거에 진행된 경기 역시 손쉽게 다시 볼 수 있습니다.

<br/>


## ⭐ 주요 기능

- **mainPage** : 과거-현재-미래에 진행할 대진표 종목별, 일별로 제공

<p align="middle">
  <img src="https://github.com/user-attachments/assets/e6a86a3e-ecfa-40fd-a4c8-f1ceb096dc78" alt="메인페이지" width="400px"/>
</p>

- **bracketPage** : 리그, 토너먼트 형식의 대진표 제공

<p align="middle">
  <img src="https://github.com/user-attachments/assets/76a41ee3-99c2-405f-940e-7aa1accd1db5" alt="대진표페이지" width="400px"/>
</p>

- **myPage** : 내 정보 조회 및 닉네임, 비밀번호, 로그아웃 기능 제공

<p align="middle">
  <img src="https://github.com/user-attachments/assets/3ade32d8-81ed-4b0b-96ea-be758d112c09" alt="마이페이지" width="400px"/>
</p>

- **LiveStreamingPage** : 실시간 비디오 스트리밍 및 채팅 기능 제공 

<p align="middle">
  <img src="https://github.com/user-attachments/assets/d2c312b4-d947-45af-a229-324cc32605df" alt="라이브스트리밍페이지" width="400px"/>
</p>

- **로그인/회원가입** : MFA를 활용한 이메일 인증 방식으로 회원가입, 로그인 관리

<p align="middle">
  <img src="https://github.com/user-attachments/assets/263562b5-e11c-4857-bec0-9b3ec4e60027" alt="로그인" width="400px"/>
  <img src="https://github.com/user-attachments/assets/42900751-86ff-4dcc-9fd3-92cba1cb04df" alt="회원가입" width="400px"/>
</p>

- **관리자페이지** : 팀 생성, 경기 생성, 방송 송출 기능

<p align="middle">
  <img src="https://github.com/user-attachments/assets/3598dc6f-fda3-44da-b51c-00949686fcad" alt="팀 생성" width="400px"/>
  <img src="https://github.com/user-attachments/assets/acd3c301-b665-4c3d-95a2-86fc9065ec7a" alt="경기 생성" width="400px"/>
</p>

- 방송 송출 페이지
<p align="middle">
  <img src="https://github.com/user-attachments/assets/e5cc5e52-a667-40b9-9032-b6f82ec55b3d" alt="방송 송출" width="400px"/>
</p>
  

<br/>

## 🔨 프로젝트 구조

- **Frontend**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/c2611829-909c-4483-a22d-f5912d55eea1" alt="frontend" width="600px"/>
</p>

- **Backend - 서버**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/910bb8ba-255a-4d4c-a1c3-950a7b60464d" alt="backend-서버" width="600px"/>
</p>

- **Backend - DB**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/7e8646c4-fe1a-44b2-afbb-766039ac2914" alt="backend-db" width="600px"/>
</p>

- **배포 흐름도**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/0e4aa4c4-2de8-4b8f-b89e-98430eb1e134" alt="전체 흐름도" width="600px"/>
</p>

<br/>

## 🔧 Stack

**Frontend(Web)**  
- **Language** : JavaScript, TypeScript  
- **Library & Framework** : React, Styled-Components, Axios  
- **Deploy**: AWS(S3+CloudFront)

<br/>

**Backend**  
- **Language** : Java  
- **Library & Framework** : Spring Boot, WebSocket, rtmp-hls  
- **Database** : MySQLDB  
- **ORM** : JPA  
- **Deploy**: AWS(EC2, RDS)

<br/>

## 🙋‍♂️ Developer

<p align="middle">
  <img src="https://github.com/user-attachments/assets/7e725bf8-05b6-447c-b05c-88ffd2ebf229" alt="팀원들" width="600px"/>
</p>
