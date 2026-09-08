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

# Install the additional TeX Live packages used by the PDF build.  tlmgr may
# exit successfully after an individual download has failed, so retry each
# package and verify the file that Pandoc/LaTeX will actually load.
RUN for attempt in 1 2 3; do \
        if tlmgr update --self; then break; fi; \
        if [[ "$attempt" == 3 ]]; then exit 1; fi; \
        sleep "$((attempt * 5))"; \
    done \
    && install_tex_package() { \
        local package="$1" style="$2" attempt; \
        for attempt in 1 2 3; do \
            tlmgr install --no-persistent-downloads "$package" || true; \
            if kpsewhich "$style" >/dev/null; then return 0; fi; \
            echo "TeX package $package did not provide $style (attempt $attempt/3)" >&2; \
            if [[ "$attempt" != 3 ]]; then sleep "$((attempt * 5))"; fi; \
        done; \
        return 1; \
    } \
    && install_tex_package enumitem enumitem.sty \
    && install_tex_package cancel cancel.sty \
    && install_tex_package xkeyval xkeyval.sty \
    && install_tex_package todonotes todonotes.sty \
    && install_tex_package titlesec titlesec.sty \
    && install_tex_package newunicodechar newunicodechar.sty

# add build-spec.sh file for GitHub Actions
COPY build-spec.sh /build-spec.sh
