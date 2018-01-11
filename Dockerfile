FROM iotaledger/iri:latest as iri

FROM maven:3-jdk-8

RUN apt-get update && \
    apt-get install -y \
        vim-tiny \
    && \
    rm -r /var/lib/apt/lists/*

RUN bash -c 'mkdir -p /opt/iota /opt/iccr/{bak,bin,conf,data,download,lib,logs,tmp}'

COPY . /src
WORKDIR /src

RUN mvn clean package
RUN bash release-iccr.bash

WORKDIR /src/dist
RUN bash install-iccr.bash

COPY --from=iri /iri/iri.jar /opt/iota/IRI.jar

EXPOSE 14265 14266

