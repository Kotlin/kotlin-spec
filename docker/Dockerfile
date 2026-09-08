FROM pandoc/latex:3.10.0.0-ubuntu

LABEL org.opencontainers.image.authors="Marat Akhin <marat.akhin@jetbrains.com>"

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

WORKDIR /source

ARG DEBIAN_FRONTEND=noninteractive

# Install Java 21 LTS and the native tools used by the specification build.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        git \
        gnupg \
        gpp \
        librsvg2-bin \
        openjdk-21-jdk-headless \
    && mkdir -p /opt/java \
    && ln -s "$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")" /opt/java/openjdk \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/opt/java/openjdk

# Install Node.js 20 LTS for the Kotlin/JS build.
RUN mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key \
        | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_20.x nodistro main" \
        > /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/*

# install more TeX Live packages
RUN tlmgr update --self \
    && tlmgr install --no-persistent-downloads \
    enumitem \
    cancel \
    todonotes \
    titlesec \
    newunicodechar

# add build-spec.sh file for GitHub Actions
COPY build-spec.sh /build-spec.sh
