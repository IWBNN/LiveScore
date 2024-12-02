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
  <img src="https://github.com/user-attachments/assets/f914ebb1-08d4-46a9-8552-f8b801945baf" alt="메인페이지" width="400px"/>
</p>

- **bracketPage** : 리그, 토너먼트 형식의 대진표 제공

<p align="middle">
  <img src="https://github.com/user-attachments/assets/91753022-7b71-413a-9936-fd6e957a1c2b" alt="대진표페이지" width="400px"/>
</p>

- **myPage** : 내 정보 조회 및 닉네임, 비밀번호, 로그아웃 기능 제공

<p align="middle">
  <img src="https://github.com/user-attachments/assets/a7207648-2c4d-47f1-86c3-2a2fbf40c0f6" alt="마이페이지" width="400px"/>
</p>

- **LiveStreamingPage** : 실시간 비디오 스트리밍 및 채팅 기능 제공 

<p align="middle">
  <img src="https://github.com/user-attachments/assets/907c8ed5-0814-4c5f-8912-e964eeff80d5" alt="라이브스트리밍페이지" width="400px"/>
</p>

- **로그인/회원가입** : MFA를 활용한 이메일 인증 방식으로 회원가입, 로그인 관리

<p align="middle">
  <img src="https://github.com/user-attachments/assets/b215ff6f-2180-4b79-ab13-eaada62056b4" alt="로그인" width="400px"/>
  <img src="https://github.com/user-attachments/assets/e830b2ff-d869-4ece-ad8f-c6215069e15b" alt="회원가입" width="400px"/>
</p>

- **관리자페이지** : 팀 생성, 경기 생성, 방송 송출 기능

<p align="middle">
  <img src="https://github.com/user-attachments/assets/cae45aab-402c-4592-bf69-62c8333004aa" alt="팀 생성" width="400px"/>
  <img src="https://github.com/user-attachments/assets/1654fcd9-97c4-48e9-bdaa-e4dbd5540cc2" alt="경기 생성" width="400px"/>
</p>

- 방송 송출 페이지
<p align="middle">
  <img src="https://github.com/user-attachments/assets/ff1d86c3-3e47-4013-952f-e6563f0ebefd" alt="방송 송출" width="400px"/>
</p>
  

<br/>

## 🔨 프로젝트 구조

- **Frontend**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/3eb7fd09-39c6-4c72-8cc1-56a36020f852" alt="frontend" width="600px"/>
</p>

- **Backend - 서버**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/81174db2-63bc-4198-8d5f-f97e69762db5" alt="backend-서버" width="600px"/>
</p>

- **Backend - DB**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/6f784e1d-2705-41f6-be5b-79871c4b2d5b" alt="backend-db" width="600px"/>
</p>

- **배포 흐름도**

<p align="middle">
  <img src="https://github.com/user-attachments/assets/a538f9ee-7732-442d-8599-212e1c43cd76" alt="전체 흐름도" width="600px"/>
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
  <img src="https://github.com/user-attachments/assets/87ed3aa7-0345-44aa-9253-0b8996af58e8" alt="팀원들" width="600px"/>
</p>
