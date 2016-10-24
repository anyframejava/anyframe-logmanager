Anyframe Log Manager, release 1.7.0 (2013.05)
------------------------------------------------
http://www.anyframejava.org/project/logmanager

1. Anyframe Log Manager 소개
개발 및 유지보수 단계에서 로컬 개발 환경이 아닌 서버 개발환경(개발/스테이징/운영) 서버의 로그데이타에 대한 접근성을 높이고, 분석을 위한 필터 및 편의기능 제공을 위한 웹 기반의 도구로써 
원격에서 Legacy Application의 로그 정책을 관리한다. 또한, 최근에 이슈가 되고 있는 경량의 NoSQL DB 중 하나인 MongoDB를 log repository로 활용하고 있다. 

* Anyframe 포탈 사이트 : http://www.anyframejava.org


2. Anyframe Log Manager Agent Service 소개
Anyframe Log Manager Agent Service는 Anyframe Log Manager 1.5.0 부터 제공되는 로그정책 관리 및 로그데이타 수집들을 담당하는 Agent 서비스 모듈로서 Apache Feilx 기반으로 개발되었다.
Log Manager Agent Service는 물리적인 서버 별로 설치되어 자체적으로 구동되는 독립적인 서비스이다.


3. 배포 파일 구조(zip)

anyframe-logmanager-agent-x.x.x.zip
   - licenses		:	Anyframe Log Manager를 통해 배포되는 3rd party 라이브러리들에 대한 라이선스 본문과 정리된 목록 포함
   - bin			:	Anyframe Log Manager Agent를 구동하거나 종료할 때 필요한 스크립트 경로
   - bundle			:	Anyframe Log Manager Agent를 구동할 때 필요한 osgi bundle 및 라이브러리 경로
   - conf			:	Anyframe Log Manager Agent를 구동할 때 필요한 환경설정파일(logagent.ini) 경로
   - 기타				:	Anyframe Log Manager 소개 및 기본 사항(readme.txt), 
						버전 별 변경 사항(changelog.txt), 
						Anyframe Log Manager 라이선스(license.txt)
							

4. 라이선스 정책

Anyframe Log Manager 프로젝트는 라이선스 정책으로 Apache Licence (http://www.apache.org), Version 2.0을 채택하고 있다.
