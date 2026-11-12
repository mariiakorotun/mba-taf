FROM maven:3.9-eclipse-temurin-17

RUN apt-get update \
 && apt-get install -y --no-install-recommends wget gnupg \
 && wget -q -O /tmp/chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
 && apt-get install -y --no-install-recommends /tmp/chrome.deb \
 && rm -rf /var/lib/apt/lists/* /tmp/chrome.deb

WORKDIR /taf
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src

ENTRYPOINT ["mvn", "-B", "test", "-Dheadless=true"]
