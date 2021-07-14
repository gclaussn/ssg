FROM openjdk:11.0.11-slim

ENV SSG_HOME="/opt/ssg"
ENV SSG_SERVER_HOST="0.0.0.0"
ENV SSG_SERVER_PORT="8080"

ENV PATH="${PATH}:${SSG_HOME}/bin"

RUN apt-get update && \
    apt-get install -y unzip

RUN echo "#!/bin/bash" >> /docker-entrypoint.sh && \
    echo "exec ssg \"\$@\"" >> /docker-entrypoint.sh && \
    chmod 750 /docker-entrypoint.sh

COPY ssg.zip /tmp/ssg.zip

RUN unzip -d /opt/ssg /tmp/ssg.zip && \
    rm /tmp/ssg.zip && \
    chmod 750 /opt/ssg/bin/ssg

WORKDIR /site

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]
