Anyframe Log Manager, release 1.6.0 (2012.12)
------------------------------------------------
http://www.anyframejava.org/project/logmanager

1. Anyframe Log Manager 소개
개발 및 유지보수 단계에서 로컬 개발 환경이 아닌 서버 개발환경(개발/스테이징/운영) 서버의 로그데이타에 대한 접근성을 높이고, 분석을 위한 필터 및 편의기능 제공을 위한 웹 기반의 도구로써 
원격에서 Legacy Application의 로그 정책을 관리한다. 또한, 최근에 이슈가 되고 있는 경량의 NoSQL DB 중 하나인 MongoDB를 log repository로 활용하고 있다. 

* Anyframe 포탈 사이트 : http://www.anyframejava.org


2. 배포 파일 구조(zip)

1) anyframe-logmanager-web-x.x.x-bin.zip
   - licenses		:	Anyframe Log Manager를 통해 배포되는 3rd party 라이브러리들에 대한 라이선스 본문과 정리된 목록 포함
   - conf			:	Anyframe Log Manager Web 모듈을 자체적으로 구동하는 경우 필요한 환경설정 파일들을 모아놓은 곳
   - lib			:	Anyframe Log Manager Web 모듈을 자체적으로 구동시키기 위한 runtime library들을 모아놓음
   - 기타				:	log manager web application 파일(anyframe-logmanager-web-x.x.x.war), 
						log manager runtime 구동 모듈 (anyframe-logmanager-packager.jar),
						Anyframe Log Manager 소개 및 기본 사항(readme.txt), 
						버전 별 변경 사항(changelog.txt), 
						Anyframe Log Manager 라이선스(license.txt),
						윈도우용 구동 스크립트(startup.cmd),
						Unix용 구동 스크립트(startup.sh),
						Unix용 종료 스크립트(shutdown.sh)
   				  

2) anyframe-logmanager-web-x.x.x-src.zip
   - src			:	Anyframe Log Manager Web 기능을 제공하는 세부 프로젝트 별 소스 코드 포함
   - licenses		:	Anyframe Log Manager Web을 통해 배포되는 3rd party 라이브러리들에 대한 라이선스 본문과 정리된 목록 포함
   - 기타				:	Anyframe Log Manager 소개 및 기본 사항(readme.txt), 버전 별 변경 사항(changelog.txt), Anyframe Log Manager 라이선스(license.txt)


3. 라이선스 정책

Anyframe Log Manager 프로젝트는 라이선스 정책으로 Apache Licence (http://www.apache.org), Version 2.0을 채택하고 있다.
