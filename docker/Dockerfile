# ___________           .__                                            __
# \_   _____/ _______  _|__|______  ____   ____   _____   ____   _____/  |_
#  |    __)_ /    \  \/ /  \_  __ \/  _ \ /    \ /     \_/ __ \ /    \   __\
#  |        \   |  \   /|  ||  | \(  <_> )   |  \  Y Y  \  ___/|   |  \  |
# /_______  /___|  /\_/ |__||__|   \____/|___|  /__|_|  /\___  >___|  /__|
#         \/     \/                           \/      \/     \/     \/
#                   _____       .___  .___.__  __  .__
#                  /  _  \    __| _/__| _/|__|/  |_|__| ____   ____   ______
#                 /  /_\  \  / __ |/ __ | |  \   __\  |/  _ \ /    \ /  ___/
#                /    |    \/ /_/ / /_/ | |  ||  | |  (  <_> )   |  \\___ \
#                \____|__  /\____ \____ | |__||__| |__|\____/|___|  /____  >
#                        \/      \/    \/                         \/     \/
#
# =~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~
#
# To build the Docker image:
#   docker build . --tag="eabuilder:latest" --progress="tty" --no-cache --compress
#
# To run the maven build process:
#   docker run --rm -it -v="$PWD:/work" -v="$PWD/bin:/out" eabuilder:latest

FROM alpine:latest
LABEL author="xIRoXaSx" \
      version="1.0.0" \
      written="2023/01/11"
ENV PATH="/usr/share/java/maven-3/bin/mvn:$PATH"
RUN apk update && apk add --no-cache openjdk17 maven \
    && mkdir /out
WORKDIR /work
CMD ["/bin/sh", "-c", "mvn --version && mvn package && mv ./target/*.jar /out"]
