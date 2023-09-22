FROM pandoc/latex:3.1.1-ubuntu
MAINTAINER Marat Akhin <marat.akhin@jetbrains.com>

WORKDIR /source

# install Java 11
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9
RUN apt-add-repository 'deb http://repos.azulsystems.com/ubuntu stable main'
RUN apt-get update
RUN apt-get install -y zulu-11

# install packages for the Kotlin spec
RUN apt-get install -y ca-certificates curl gnupg git gpp librsvg2-bin
RUN mkdir -p /etc/apt/keyrings
RUN curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg
RUN echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_20.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list
RUN apt-get update
RUN apt-get install -y nodejs

# install more TeX Live packages
RUN tlmgr install --no-persistent-downloads enumitem
RUN tlmgr install --no-persistent-downloads cancel
RUN tlmgr install --no-persistent-downloads todonotes
RUN tlmgr install --no-persistent-downloads titlesec
RUN tlmgr install --no-persistent-downloads newunicodechar

# add build-spec.sh file for GitHub Actions
COPY build-spec.sh /build-spec.sh
