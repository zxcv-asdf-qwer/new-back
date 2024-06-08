FROM eclipse-temurin:17 as jre-build
VOLUME /tmp
COPY build/libs/app.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=${PROFILE}","-Dlog4j2.disable.jmx=true","-Duser.timezone=Asia/Seoul","-Djava.security.egd=file:/dev/./urandom","-Duser.country=KR","-Duser.language=ko","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/tmp/dump.hprof","-jar","/app.jar"]
#-Djava.security.egd=file:/dev/./urandom: 난수 생성 속도를 향상시키기 위한 옵션입니다.
# 리눅스와 유닉스 시스템에서 제공하는 가상 장치 파일입니다. 이 파일은 커널의 엔트로피 풀에서 읽은 데이터를 바탕으로 난수를 생성합니다.
# 엔트로피 풀은 시스템의 환경 요인, 사용자 입력, 네트워크 트래픽 등을 수집하여 무작위성을 증가
#-Duser.country=KR: 사용자의 국가를 한국으로 설정하는 옵션입니다.
#-Duser.language=ko: 사용자의 언어를 한국어로 설정하는 옵션입니다.
#-Duser.timezone=Asia/Seoul 시간 설정
#-XX:+HeapDumpOnOutOfMemoryError: 메모리 부족 오류가 발생하면 힙 덤프 파일을 생성하는 옵션입니다.
#-XX:HeapDumpPath=/tmp/dump.hprof: 힙 덤프 파일의 경로를 /tmp/dump.hprof로 설정하는 옵션입니다.
