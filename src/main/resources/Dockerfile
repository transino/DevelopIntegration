FROM java:8
VOLUME /tmp
ADD developintegration-0.0.1.war app.war
COPY ./aml src/main/resources/aml
COPY ./testproject src/main/resources/testproject
RUN bash -c 'touch /app.war'
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.war"]