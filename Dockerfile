FROM java:latest

# Install maven
RUN mkdir /prodcat
RUN rm -rf /prodcat/*
#RUN apt-get update  
#RUN apt-get install -y maven
COPY  ./target/*.jar /prodcat

#COPY ./pom.xml /prodcat
VOLUME /prodcat
WORKDIR /prodcat
# Modify per your build tool
# RUN "mvn"  install
EXPOSE 2400
ENTRYPOINT ["java","-cp","prodcat-jar-with-dependencies.jar","io.globomart.prodcat.App"]
CMD [""]