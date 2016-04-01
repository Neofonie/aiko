# Pull base image.
FROM williamyeh/java8
ENV TERM screen

# Add application
ADD https://dl.bintray.com/neofonie/maven/de/neofonie/aiko/1.2/aiko-1.2.jar 	/opt/application/application.jar
ADD payloads/aiko          	/usr/local/bin/aiko
RUN chmod a+x /usr/local/bin/aiko

# Set working dir
WORKDIR /opt/application

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# Define entrypoint
CMD ["bash"]
