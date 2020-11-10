FROM openjdk:14.0.2-slim

ENV SSG_HOME="/opt/ssg"
ENV SSG_FILE_WATCHER="POLLING"

ENV PATH="${PATH}:${SSG_HOME}/bin"

ENTRYPOINT ["/docker-entrypoint.sh"]

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
