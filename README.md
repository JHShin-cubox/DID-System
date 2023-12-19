# DID System

> ## 프로젝트 목적
> 인천공항 실증 시 승객에게 X-ray 검사 이미지를 보여줄 수 있는 전용 디스플레이가 필요하여 개발하게 되었다.

> ## 설치 가이드
> 1. Jar파일로 압축
> 2. 서버에 Jar파일 복사 ```C:\project```
> 3. ```services.msc``` 실행 후  DidSystemService 서비스 재시작

> ## 사용 방법
> 1. API로 외부 이미지 및 데이터를 Post 방식으로 받음
> 2. API호출 시 웹소켓으로 프론트엔드 제어

> ## 설정 및 구성
> 프로젝트 환경 설정과 구성에 대한 정보입니다.
> + application.yml 파일에서 기본설정
> + WebMvcConfig로 외부파일 접근 및 설정
> + WebSocketConfig로 웹소켓 기본 설정
